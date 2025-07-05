package io.github.zapolyarnydev.inventoryservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "inventory_items")
@NoArgsConstructor
public class InventoryItemEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID uuid;

    @Column(unique = true)
    private String name;

    private int quantity;

    public InventoryItemEntity(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
}
