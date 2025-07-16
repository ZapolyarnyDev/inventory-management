package io.github.zapolyarnydev.inventoryservice.integration;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.zapolyarnydev.inventoryservice.controller.response.ApiResponse;
import io.github.zapolyarnydev.inventoryservice.dto.ItemEntityDTO;
import io.github.zapolyarnydev.inventoryservice.entity.InventoryItemEntity;
import io.github.zapolyarnydev.inventoryservice.repository.InventoryRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Stream;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext
@Testcontainers
@AutoConfigureMockMvc
@Tag("integration")
@DisplayName("Процесс регистрации предмета на склад")
public class RegistrationInventoryItemTests {

    @Container
    private static final ConfluentKafkaContainer kafkaContainer = new ConfluentKafkaContainer(
            DockerImageName.parse("confluentinc/cp-kafka:7.8.0")
                    .asCompatibleSubstituteFor("apache/kafka")
    );

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private InventoryRepository inventoryRepository;

    @DynamicPropertySource
    private static void kafkaProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.kafka.bootstrap-servers", kafkaContainer::getBootstrapServers);
    }

    @AfterEach
    public void cleanDatabase(){
        jdbcTemplate.execute("DELETE FROM inventory_items");
    }

    @DisplayName("Должно вернуть ответ с сущностью зарегистрированного предмета при его регистрации на складе")
    @ParameterizedTest(name = "Корректный HTTP запрос регистраци - {index} - предмет с названием {0} должен быть зарегистрирован в количестве {1} штук")
    @MethodSource("registerInventoryItemRequestCorrectArgs")
    public void shouldAddItemInInventoryWithCorrectData(String requestName, int quantity) throws Exception {
        String request = createRegisterInventoryItemRequest(requestName, quantity);

        MvcResult result = mockMvc.perform(post("/inventory/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andReturn();

        ApiResponse<ItemEntityDTO> apiResponse = objectMapper.readValue(
                result.getResponse().getContentAsString(), new TypeReference<>() {});

        ItemEntityDTO entityDTO = apiResponse.data();

        assertThat(entityDTO)
                .extracting(ItemEntityDTO::name,
                        ItemEntityDTO::quantity)
                .containsExactly(requestName,
                        quantity);

        InventoryItemEntity entity = inventoryRepository.findById(entityDTO.uuid()).orElseThrow();

        assertThat(entity.getName()).isEqualTo(entityDTO.name());
        assertThat(entity.getQuantity()).isEqualTo(entityDTO.quantity());
    }

    @DisplayName("Должно вернуть ответ с отклонением запроса регистрации предмета при неправильном начальном количеств")
    @ParameterizedTest(name = "Ошибочный HTTP запрос регистрации - {index} - предмет с названием {0} не должен быть зарегистрирован, количество {1} не допускается")
    @MethodSource("registerInventoryItemRequestWrongArgs")
    public void shouldRejectItemAddingInInventoryWithWrongData(String requestName, int quantity) throws Exception {
        String request = createRegisterInventoryItemRequest(requestName, quantity);

        mockMvc.perform(post("/inventory/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> registerInventoryItemRequestCorrectArgs() {
        return Stream.of(
                Arguments.of("Banana", 1),
                Arguments.of("Apple", 400),
                Arguments.of("Joystick", 0),
                Arguments.of("Workbench", 920000)
        );
    }

    private static Stream<Arguments> registerInventoryItemRequestWrongArgs() {
        return Stream.of(
                Arguments.of("Bad Banana", -1),
                Arguments.of("Bad Apple", -400),
                Arguments.of("Bad Workbench", -920000)
        );
    }

    private String createRegisterInventoryItemRequest(String name, int quantity) {
        return String.format("""
                {
                    "name": "%s",
                    "quantity": %d
                }
                """, name, quantity);
    }
}
