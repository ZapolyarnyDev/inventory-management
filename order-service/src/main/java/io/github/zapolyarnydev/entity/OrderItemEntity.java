package io.github.zapolyarnydev.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@Setter
public class OrderItemEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID uuid;

    private UUID itemId;
    private int quantity;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    public OrderItemEntity(UUID itemId, int quantity) {
        this.itemId = itemId;
        this.quantity = quantity;
    }
}
