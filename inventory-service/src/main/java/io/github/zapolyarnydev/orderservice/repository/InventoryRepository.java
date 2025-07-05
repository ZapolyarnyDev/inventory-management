package io.github.zapolyarnydev.orderservice.repository;

import io.github.zapolyarnydev.inventoryservice.entity.InventoryItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepository extends JpaRepository<InventoryItemEntity, UUID> {

    Optional<InventoryItemEntity> findByName(String name);

    boolean existsByName(String name);
}
