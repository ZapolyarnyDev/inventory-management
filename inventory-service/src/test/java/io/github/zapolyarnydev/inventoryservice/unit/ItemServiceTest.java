package io.github.zapolyarnydev.inventoryservice.unit;

import io.github.zapolyarnydev.inventoryservice.entity.InventoryItemEntity;
import io.github.zapolyarnydev.inventoryservice.exception.ItemHasNameException;
import io.github.zapolyarnydev.inventoryservice.exception.SmallItemQuantityException;
import io.github.zapolyarnydev.inventoryservice.repository.InventoryRepository;
import io.github.zapolyarnydev.inventoryservice.service.InventoryService;
import io.github.zapolyarnydev.kafkaevents.dto.OrderItemEventDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Работа сервиса, взаимодействующего с товарами на складе")
@Tag("unit")
public class ItemServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Captor
    private ArgumentCaptor<InventoryItemEntity> itemCaptor;

    @Nested
    @DisplayName("Добавление и удаление товаров со склада")
    public class InventoryServiceWorkingWithRepositoryTests {

        @DisplayName("Сервис должен успешно добавить новый предмет на склад")
        @Test
        void shouldAddNewItem() {
            String name = "entity1";
            int quantity = 10;

            when(inventoryRepository.existsByName(name)).thenReturn(false);

            InventoryItemEntity savedEntity = inventoryService.addItem(name, quantity);

            verify(inventoryRepository).save(itemCaptor.capture());

            assertEquals(name, itemCaptor.getValue().getName());
            assertEquals(quantity, itemCaptor.getValue().getQuantity());
            assertEquals(savedEntity.getName(), name);
            assertEquals(savedEntity.getQuantity(), quantity);
        }

        @DisplayName("Сервис должен выбросить исключение, если товар с таким именем уже существует")
        @Test
        void shouldThrowWhenItemNameExists() {
            String name = "Item1";
            when(inventoryRepository.existsByName(name)).thenReturn(true);

            assertThrows(ItemHasNameException.class, () -> inventoryService.addItem(name, 5));
        }

        @DisplayName("Сервис должен удалить предмет со склада")
        @Test
        public void shouldServiceRemoveItemFromInventoryRepository() {
            var itemEntity = new InventoryItemEntity("entity1", 30);

            inventoryService.removeItem(itemEntity);

            verify(inventoryRepository).delete(itemCaptor.capture());

            var captorValue = itemCaptor.getValue();

            assertEquals(itemEntity.getName(), captorValue.getName());
            assertEquals(itemEntity.getQuantity(), captorValue.getQuantity());
        }

    }

    @Nested
    @DisplayName("Поиск товаров по UUID и имени")
    public class FindItemTests {

        @DisplayName("Сервис должен вернуть товар по UUID")
        @Test
        void shouldFindItemByUUID() {
            UUID uuid = UUID.randomUUID();
            var entity = new InventoryItemEntity("entity1", 10);
            when(inventoryRepository.findById(uuid)).thenReturn(Optional.of(entity));

            InventoryItemEntity result = inventoryService.findItemEntity(uuid);

            assertEquals(entity, result);
        }

        @DisplayName("Сервис должен выбросить исключение, если товар с UUID не найден")
        @Test
        void shouldThrowWhenItemByUUIDNotFound() {
            UUID uuid = UUID.randomUUID();
            when(inventoryRepository.findById(uuid)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> inventoryService.findItemEntity(uuid));
        }

        @DisplayName("Сервис должен вернуть товар по имени")
        @Test
        void shouldFindItemByName() {
            String name = "itemX";
            var entity = new InventoryItemEntity(name, 5);
            when(inventoryRepository.findByName(name)).thenReturn(Optional.of(entity));

            InventoryItemEntity result = inventoryService.findItemEntity(name);

            assertEquals(entity, result);
        }

        @DisplayName("Сервис должен выбросить исключение, если товар с таким именем не найден")
        @Test
        void shouldThrowWhenItemByNameNotFound() {
            String name = "NotExist";
            when(inventoryRepository.findByName(name)).thenReturn(Optional.empty());

            assertThrows(EntityNotFoundException.class, () -> inventoryService.findItemEntity(name));
        }
    }


    @Nested
    @DisplayName("Работа с количеством товаров на складе")
    public class InventoryServiceQuantityChangingTests {

        @ParameterizedTest(name = "{index} - при значении {0} должно выброситься исключение IllegalArgumentException")
        @DisplayName("Изменение количества товаров не должно произойти при негативном значении")
        @ValueSource(ints = {0, -1, -9000})
        public void shouldIncreaseMethodThrowsOnNegativeValues(int value) {
            UUID uuid = UUID.randomUUID();

            assertThrows(IllegalArgumentException.class, () -> inventoryService.increaseItemQuantity(uuid, value));
            assertThrows(IllegalArgumentException.class, () -> inventoryService.decreaseItemQuantity(uuid, value));
        }

        @ParameterizedTest(name = "{index} - к предмету с начальным количеством на складе {0}, должно корректно прибавить {1} шт.")
        @DisplayName("Корректное увеличение числа товаров")
        @MethodSource("increaseItemEntityQuantityArgs")
        public void shouldIncreaseItemEntityQuantity(int startQuantity, int increaseValue) {
            var entityId = UUID.randomUUID();
            var entity = new InventoryItemEntity("entity1", startQuantity);

            when(inventoryRepository.findById(entityId)).thenReturn(Optional.of(entity));

            inventoryService.increaseItemQuantity(entityId, increaseValue);

            assertEquals(entity.getQuantity(), startQuantity + increaseValue);
        }

        @ParameterizedTest(name = "{index} - от предмета с начальным количеством на складе {0}, должно корректно отнять {1} шт.")
        @DisplayName("Корректное уменьшение числа товаров")
        @MethodSource("decreaseItemEntityQuantityArgs")
        public void shouldDecreaseItemEntityQuantity(int startQuantity, int decreaseValue) {
            var entityId = UUID.randomUUID();
            var entity = new InventoryItemEntity("entity1", startQuantity);

            when(inventoryRepository.findById(entityId)).thenReturn(Optional.of(entity));

            inventoryService.decreaseItemQuantity(entityId, decreaseValue);

            assertEquals(entity.getQuantity(), startQuantity - decreaseValue);
        }

        @ParameterizedTest(name = "{index} - должно выбросить SmallItemException, на складе {0} таких предметов, а значение списания - {1}")
        @DisplayName("Уменьшение количества товаров не должно произойти при их недостаточном количестве")
        @MethodSource("throwOnDecreaseItemEntityQuantityArgs")
        public void shouldThrowSmallItemException_WhenDecreaseQuantityIsGreaterThenStartQuantity(int startQuantity, int decreaseValue) {
            var entityId = UUID.randomUUID();
            var entity = new InventoryItemEntity("entity1", startQuantity);

            when(inventoryRepository.findById(entityId)).thenReturn(Optional.of(entity));

            assertThrows(SmallItemQuantityException.class, () -> inventoryService.decreaseItemQuantity(entityId, decreaseValue));

        }

        private static Stream<Arguments> increaseItemEntityQuantityArgs() {
            return Stream.of(
                    Arguments.of(10, 17),
                    Arguments.of(0, 135),
                    Arguments.of(0, 1),
                    Arguments.of(23412, 2)
            );
        }

        private static Stream<Arguments> decreaseItemEntityQuantityArgs() {
            return Stream.of(
                    Arguments.of(30, 21),
                    Arguments.of(240, 1),
                    Arguments.of(97, 8),
                    Arguments.of(153, 153)
            );
        }

        private static Stream<Arguments> throwOnDecreaseItemEntityQuantityArgs() {
            return Stream.of(
                    Arguments.of(1, 75),
                    Arguments.of(1, 2),
                    Arguments.of(2, 3),
                    Arguments.of(3, 4),
                    Arguments.of(5, 6),
                    Arguments.of(0, 1),
                    Arguments.of(920, 921),
                    Arguments.of(4, 112353)
            );
        }
    }

    @Nested
    @DisplayName("Резервирование товаров")
    public class ReservationTests {

        @DisplayName("canReserve должен вернуть true, если всех товаров хватает")
        @Test
        void shouldCanReserveReturnTrueIfEnoughItems() {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();

            var item1 = new InventoryItemEntity("entity1", 10);
            var item2 = new InventoryItemEntity("entity2", 15);

            var dto1 = new OrderItemEventDTO(id1, 5);
            var dto2 = new OrderItemEventDTO(id2, 10);

            when(inventoryRepository.findById(id1)).thenReturn(Optional.of(item1));
            when(inventoryRepository.findById(id2)).thenReturn(Optional.of(item2));

            boolean result = inventoryService.canReserve(List.of(dto1, dto2));

            assertTrue(result);
        }

        @DisplayName("canReserve должен вернуть false, если хотя бы одного товара не хватает")
        @Test
        void shouldCanReserveReturnFalseIfAnyItemInsufficient() {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();

            var item1 = new InventoryItemEntity("entity1", 10);
            var item2 = new InventoryItemEntity("entity2", 5);

            var dto1 = new OrderItemEventDTO(id1, 5);
            var dto2 = new OrderItemEventDTO(id2, 10);

            when(inventoryRepository.findById(id1)).thenReturn(Optional.of(item1));
            when(inventoryRepository.findById(id2)).thenReturn(Optional.of(item2));

            boolean result = inventoryService.canReserve(List.of(dto1, dto2));

            assertFalse(result);
        }

        @DisplayName("reserveItems должен корректно уменьшать количество каждого предмета")
        @Test
        void shouldReserveItemsCallDecreaseForEachItem() {
            UUID id = UUID.randomUUID();
            var item = new InventoryItemEntity("entity1", 10);
            var dto = new OrderItemEventDTO(id, 2);

            when(inventoryRepository.findById(id)).thenReturn(Optional.of(item));

            inventoryService = org.mockito.Mockito.spy(inventoryService);
            inventoryService.reserveItems(List.of(dto));

            verify(inventoryService).decreaseItemQuantity(id, 2);
        }

        @DisplayName("recoverItems должен корректно увеличивать количество каждого предмета")
        @Test
        void shouldRecoverItemsCallIncreaseForEachItem() {
            UUID id = UUID.randomUUID();
            var item = new InventoryItemEntity("entity1", 10);
            var dto = new OrderItemEventDTO(id, 3);

            inventoryService = org.mockito.Mockito.spy(inventoryService);
            when(inventoryRepository.findById(id)).thenReturn(Optional.of(item));

            inventoryService.recoverItems(List.of(dto));

            verify(inventoryService).increaseItemQuantity(id, 3);
        }
    }

}
