package io.github.zapolyarnydev.invmanagement.service;

import io.github.zapolyarnydev.invmanagement.entity.InventoryItemEntity;
import io.github.zapolyarnydev.invmanagement.exception.ItemHasNameException;
import io.github.zapolyarnydev.invmanagement.exception.SmallItemQuantityException;
import io.github.zapolyarnydev.invmanagement.repository.InventoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepository repository;

    public void addItem(String name, int quantity) throws ItemHasNameException {
        if(repository.existsByName(name))
            throw new ItemHasNameException(String.format("Entity with name: '%s' already exists", name));

        var entity = new InventoryItemEntity(name, quantity);
        repository.save(entity);
    }

    public void saveItem(InventoryItemEntity entity){
        repository.save(entity);
    }

    public void removeItem(InventoryItemEntity entity){
        repository.delete(entity);
    }

    @NotNull
    public InventoryItemEntity findEntity(UUID uuid) throws EntityNotFoundException {
        return repository.findById(uuid)
                .orElseThrow(() -> new EntityNotFoundException("Item with UUID: " + uuid + " not found"));
    }

    @Transactional
    public void increaseItemQuantity(UUID uuid, int value){
        if(value <= 0)
            throw new IllegalArgumentException(String.format("Increase value must be positive. Passed: %d", value));

        var entity = findEntity(uuid);
        entity.setQuantity(entity.getQuantity() + value);
        saveItem(entity);
    }

    @Transactional
    public void decreaseItemQuantity(UUID uuid, int value){
        if(value <= 0)
            throw new IllegalArgumentException(String.format("Decrease value must be positive. Passed: %d", value));

        var entity = findEntity(uuid);
        int quantity = entity.getQuantity();
        if(quantity < value) throw new SmallItemQuantityException(quantity, value);
        entity.setQuantity(quantity - value);
        saveItem(entity);
    }
}
