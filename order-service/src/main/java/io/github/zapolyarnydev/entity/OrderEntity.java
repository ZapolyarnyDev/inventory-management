package io.github.zapolyarnydev.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "orders")
@NoArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue
    @Column(name = "id")
    private UUID uuid;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItemEntity> orderItems;

    @Enumerated(value = EnumType.STRING)
    private OrderStatus orderStatus;

    private String statusReason;

    private LocalDateTime createdDateTime;
    private LocalDateTime releaseDateTime;

    public OrderEntity(List<OrderItemEntity> orderItems, LocalDateTime releaseDateTime) {
        this.orderItems = orderItems;
        this.releaseDateTime = releaseDateTime;
        this.createdDateTime = LocalDateTime.now();

        if(orderItems != null){
            orderItems.forEach(orderItemEntity -> orderItemEntity.setOrder(this));
        }
    }
}
