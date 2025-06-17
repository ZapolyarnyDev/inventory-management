package io.github.zapolyarnydev.service;

import io.github.zapolyarnydev.entity.InventoryItemEntity;
import io.github.zapolyarnydev.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("Тестирование сервиса работающего с товарами на складе")
public class ItemServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @InjectMocks
    private InventoryService inventoryService;

    @Captor
    private ArgumentCaptor<InventoryItemEntity> itemCaptor;

    @DisplayName("Сервис должен сохранить предмет Book на склад")
    @Test
    public void shouldServiceSaveItemToInventoryRepository() {
        var itemEntity = new InventoryItemEntity("Book", 30);

        inventoryService.saveItem(itemEntity);

        verify(inventoryRepository).save(itemCaptor.capture());

        assertEquals(itemEntity.getName(), itemCaptor.getValue().getName());
    }

    @DisplayName("Сервис должен удалить предмет Book со склада")
    @Test
    public void shouldServiceRemoveItemFromInventoryRepository() {
        var itemEntity = new InventoryItemEntity("Book", 30);

        inventoryService.removeItem(itemEntity);

        verify(inventoryRepository).delete(itemCaptor.capture());

        assertEquals(itemEntity.getName(), itemCaptor.getValue().getName());
    }
}
