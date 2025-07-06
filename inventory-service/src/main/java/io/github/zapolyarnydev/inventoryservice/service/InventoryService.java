package io.github.zapolyarnydev.inventoryservice.service;

import io.github.zapolyarnydev.kafkaevents.dto.OrderItemEventDTO;
import io.github.zapolyarnydev.inventoryservice.entity.InventoryItemEntity;
import io.github.zapolyarnydev.inventoryservice.exception.ItemHasNameException;
import io.github.zapolyarnydev.inventoryservice.exception.SmallItemQuantityException;
import io.github.zapolyarnydev.inventoryservice.repository.InventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;

    @Transactional
    public InventoryItemEntity addItem(String name, int quantity) throws ItemHasNameException {
        if(repository.existsByName(name))
            throw new ItemHasNameException(String.format("Entity with name: '%s' already exists", name));

        var entity = new InventoryItemEntity(name, quantity);
        repository.save(entity);

        return entity;
    }

    public void saveItem(InventoryItemEntity entity){
        repository.save(entity);
    }

    public void removeItem(InventoryItemEntity entity){
        repository.delete(entity);
    }

    @NotNull
    public InventoryItemEntity findItemEntity(UUID uuid) throws EntityNotFoundException {
        return repository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Item with UUID: " + uuid + " not found"));
    }

    @NotNull
    public InventoryItemEntity findItemEntity(String name) throws EntityNotFoundException {
        return repository.findByName(name)
                .orElseThrow(() -> new EntityNotFoundException("Item with name: " + name + " not found"));
    }

    @Transactional
    public void increaseItemQuantity(UUID uuid, int value){
        if(value <= 0)
            throw new IllegalArgumentException(String.format("Increase value must be positive. Passed: %d", value));

        var entity = findItemEntity(uuid);
        entity.setQuantity(entity.getQuantity() + value);
        saveItem(entity);
    }

    @Transactional
    public void decreaseItemQuantity(UUID uuid, int value){
        if(value <= 0)
            throw new IllegalArgumentException(String.format("Decrease value must be positive. Passed: %d", value));

        var entity = findItemEntity(uuid);
        int quantity = entity.getQuantity();
        if(quantity < value) throw new SmallItemQuantityException(quantity, value);
        entity.setQuantity(quantity - value);
        saveItem(entity);
    }

    public boolean canReserve(List<OrderItemEventDTO> orderItems) throws EntityNotFoundException {
        for(var item : orderItems){
            var entity = findItemEntity(item.inventoryItemId());
            if(entity.getQuantity() < item.quantity()) return false;
        }
        return true;
    }

    @Transactional
    public void reserveItems(List<OrderItemEventDTO> orderItems) {
        for(var item : orderItems){
            decreaseItemQuantity(item.inventoryItemId(), item.quantity());
        }
    }

    @Transactional
    public void recoverItems(List<OrderItemEventDTO> orderItems) {
        for(var item : orderItems){
            increaseItemQuantity(item.inventoryItemId(), item.quantity());
        }
    }
}
