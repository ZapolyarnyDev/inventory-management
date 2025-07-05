package io.github.zapolyarnydev.inventoryservice.orderservice.repository;

import io.github.zapolyarnydev.inventoryservice.orderservice.entity.OrderEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, UUID> {

}
