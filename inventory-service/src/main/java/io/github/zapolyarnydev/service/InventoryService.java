package io.github.zapolyarnydev.service;

import io.github.zapolyarnydev.entity.InventoryItemEntity;
import io.github.zapolyarnydev.exception.ItemHasNameException;
import io.github.zapolyarnydev.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
