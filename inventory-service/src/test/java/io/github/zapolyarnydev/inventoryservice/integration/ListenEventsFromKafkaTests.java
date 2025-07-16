package io.github.zapolyarnydev.inventoryservice.integration;

import io.github.zapolyarnydev.inventoryservice.listener.OrderEventConsumer;
import io.github.zapolyarnydev.kafkaevents.dto.OrderItemEventDTO;
import io.github.zapolyarnydev.kafkaevents.event.order.OrderPlacedEvent;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@Testcontainers
@Tag("integration")
@DisplayName("Процесс чтения события публикации заказа")
@EmbeddedKafka(partitions = 1, topics = { "orders.placed", "inventory.order-status-response", "orders.cancelled"})
public class ListenEventsFromKafkaTests {

    @Container
    private static final ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.8.0")
                    .asCompatibleSubstituteFor("apache/kafka")
    );

    @Autowired
    private KafkaTemplate<String, Object> orderTemplate;

    @MockitoSpyBean
    private OrderEventConsumer orderEventConsumer;

    @DynamicPropertySource
    private static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @DisplayName("Проверка чтения события в kafka")
    @Test
    public void shouldPublishOrderPlacedEvent() throws Exception {
        UUID inventoryItemId = UUID.randomUUID();
        var orderItemEventDTOList = List.of(new OrderItemEventDTO(inventoryItemId, 100));

        var event = new OrderPlacedEvent(UUID.randomUUID(), orderItemEventDTOList, LocalDateTime.now());
        orderTemplate.send("orders.placed", event);

        await()
                .atMost(Duration.of(5, ChronoUnit.SECONDS))
                .untilAsserted(() -> {
                    verify(orderEventConsumer, times(1))
                            .onPlaceOrder(argThat(injectedEvent -> injectedEvent.orderId().equals(event.orderId())));
                });
    }
}
