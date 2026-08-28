package com.ecommerce.order.saga;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "saga_state")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SagaState {

    @Id
    @Column(nullable = false, unique = true)
    private String orderNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStep currentStep;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SagaStatus status;

    private String userEmail;
    private String failureReason;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate  protected void onUpdate()  { updatedAt = LocalDateTime.now(); }

    public enum SagaStep {
        STARTED, RESERVE_STOCK, PROCESS_PAYMENT, SEND_NOTIFICATION, COMPENSATE_STOCK
    }

    public enum SagaStatus {
        STARTED, COMPLETED, COMPENSATING, FAILED
    }
}
