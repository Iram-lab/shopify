package com.ecommerce.inventory.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    @Column(nullable = false)
    private Integer quantityAvailable;

    @Column(nullable = false)
    @Builder.Default
    private Integer reservedQuantity = 0;

    /**
     * Optimistic locking — prevents two concurrent order requests
     * from both reading qty=5 and both decrementing to 4.
     * The second update will throw OptimisticLockException and retry.
     */
    @Version
    private Long version;

    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Integer getAvailableStock() {
        return quantityAvailable - reservedQuantity;
    }
}
