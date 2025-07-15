package io.github.zapolyarnydev.orderservice.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.zapolyarnydev.kafkaevents.dto.OrderItemEventDTO;
import io.github.zapolyarnydev.kafkaevents.event.order.OrderPlacedEvent;
import io.github.zapolyarnydev.orderservice.controller.response.ApiResponse;
import io.github.zapolyarnydev.orderservice.entity.OrderEntity;
import io.github.zapolyarnydev.orderservice.entity.OrderStatus;
import io.github.zapolyarnydev.orderservice.repository.OrderRepository;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@Testcontainers
@AutoConfigureMockMvc
@DisplayName("Интеграционный тест публикации заказа")
@Tag("integration")
public class OrderPlaceServiceIntegrationTest {

    @Container
    private static final ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.8.0")
                    .asCompatibleSubstituteFor("apache/kafka"));

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ConsumerFactory<String, OrderPlacedEvent> consumerFactory;

    private Consumer<String, OrderPlacedEvent> kafkaConsumer;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @DynamicPropertySource
    static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @AfterEach
    public void cleanDatabase(){
        jdbcTemplate.execute("DELETE FROM order_items");
        jdbcTemplate.execute("DELETE FROM orders");
    }

    @Nested
    @DisplayName("Проверка ответов контроллера на запрос создания заказа")
    public class OrderPlaceTests {

        @DisplayName("Должно вернуть корректное содержимое в ответе и статус 202 (Accepted)")
        @ParameterizedTest(name = "{index} - дата получения заказа веная, запрос должен пройти")
        @ValueSource(ints = {1, 2, 180, 65536})
        public void shouldReturnAcceptedStatusWithCorrectReleaseDateTime(int plusDay) throws Exception {
            UUID inventoryItemId = UUID.randomUUID();

            String orderRequest = getOrderRequest(inventoryItemId, LocalDateTime.now().plusDays(plusDay));

            mockMvc.perform(post("/orders/place")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(orderRequest)
                    )
                    .andExpect(status().isAccepted())
                    .andExpect(jsonPath("$.success").value(true));

        }

        @DisplayName("Должно вернуть статус 400 (Bad Request)")
        @ParameterizedTest(name = "{index} - дата получения заказа ошибочная, запрос не должен пройти")
        @ValueSource(ints = {0, 1, 27, 65536})
        public void shouldReturnBadRequestStatusWithWrongReleaseDateTime(int minusDays) throws Exception {
            UUID inventoryItemId = UUID.randomUUID();

            String orderRequest = getOrderRequest(inventoryItemId, LocalDateTime.now().minusDays(minusDays));

            mockMvc.perform(post("/orders/place")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(orderRequest)
                    )
                    .andExpect(status().isBadRequest());

        }
    }

    @DisplayName("Проверка сохранения заказа в базе данных")
    @Test
    @Transactional
    public void shouldSaveOrderEntityToDatabase() throws Exception{
        UUID inventoryItemId = UUID.randomUUID();
        var releaseDateTime = LocalDateTime.now().plusDays(1);

        String orderRequest = getOrderRequest(inventoryItemId, releaseDateTime);

        MvcResult response = mockMvc.perform(post("/orders/place")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(orderRequest)
                )
                .andExpect(status().isAccepted())
                .andReturn();

        ApiResponse<UUID> apiResponse = objectMapper.readValue(response.getResponse().getContentAsString(),
                new TypeReference<>() {});

        var entity = orderRepository.findById(apiResponse.data()).orElseThrow();

        assertThat(entity)
                .extracting(OrderEntity::getOrderStatus,
                        OrderEntity::getStatusReason,
                        OrderEntity::getReleaseDateTime,
                        o -> o.getOrderItems().size(),
                        o -> o.getOrderItems().getFirst().getItemId())
                .containsExactly(OrderStatus.PENDING,
                        "Order is being validated",
                        releaseDateTime,
                        1,
                        inventoryItemId);
    }

    @Nested
    @DisplayName("Публикация событий в kafka")
    public class PublishEventsToKafkaTests {

        @Autowired
        private KafkaAdmin kafkaAdmin;

        @BeforeEach
        public void cleanKafkaTopicsAndSetupConsumer() throws Exception {
            try (AdminClient adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties())) {
                String topic = "orders.placed";

                adminClient.deleteTopics(List.of(topic)).all().get();

                await().atMost(Duration.ofSeconds(5))
                        .until(() -> !adminClient.listTopics().names().get().contains(topic));

                adminClient.createTopics(List.of(new NewTopic(topic, 1, (short) 1))).all().get();
            }

            kafkaConsumer = consumerFactory.createConsumer(UUID.randomUUID().toString());
            kafkaConsumer.subscribe(List.of("orders.placed"));
            kafkaConsumer.poll(Duration.ZERO);
            kafkaConsumer.seekToBeginning(kafkaConsumer.assignment());
        }

        @AfterEach
        public void closeConsumer() {
            kafkaConsumer.close();
        }

        @DisplayName("Проверка публикации событий в kafka")
        @Test
        public void shouldPublishOrderPlacedEvent() throws Exception {
            UUID inventoryItemId = UUID.randomUUID();
            String orderRequest = getOrderRequest(inventoryItemId, LocalDateTime.now().plusDays(1));

            mockMvc.perform(post("/orders/place")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(orderRequest)
                    )
                    .andExpect(status().isAccepted());

            ConsumerRecords<String, OrderPlacedEvent> consumerRecords = kafkaConsumer.poll(Duration.ofSeconds(10));
            Optional<ConsumerRecord<String, OrderPlacedEvent>> firstRecord =
                    StreamSupport.stream(consumerRecords.records("orders.placed").spliterator(), false)
                            .findFirst();

            var event = firstRecord.orElseThrow().value();
            OrderItemEventDTO orderItem = event.orderItems().getFirst();

            assertNotNull(event);

            assertThat(orderItem)
                    .extracting(OrderItemEventDTO::inventoryItemId,
                            OrderItemEventDTO::quantity)
                    .containsExactly(inventoryItemId,
                            2);
        }
    }

    private String getOrderRequest(UUID uuid, LocalDateTime releaseDateTime) {
        return String.format("""
                {
                    "releaseDateTime": "%s",
                    "orderItems": [
                        { "inventoryItemId": "%s", "quantity": 2 }
                    ]
                }
                """, releaseDateTime, uuid);
    }

}
