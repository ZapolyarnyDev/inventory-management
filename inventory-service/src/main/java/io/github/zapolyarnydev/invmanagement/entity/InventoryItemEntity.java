package io.github.zapolyarnydev.invmanagement.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "inventory_items")
public class InventoryItemEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID uuid;

    private String name;

    private int quantity;

    public InventoryItemEntity(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
    }
}
