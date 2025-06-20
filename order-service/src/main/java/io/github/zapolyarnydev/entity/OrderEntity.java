package io.github.zapolyarnydev.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID uuid;

    private UUID itemId;

    private int quantity;

    private LocalDateTime createdDateTime;

    public OrderEntity(UUID itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
        this.createdDateTime = LocalDateTime.now();
    }
}
