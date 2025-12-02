package com.in.xoriant.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;


@Getter
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
@Builder
@Entity
@Table(name = "approval_tasks", indexes = {@Index(columnList = "entityType,entityId")})
public class ApprovalTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String entityType;
    
    private Long entityId;

    private Integer currentStepIndex;
    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    private String lockOwner;
    private Instant lockExpiresAt;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
        if (status == null) status = TaskStatus.PENDING;
        if (currentStepIndex == null) currentStepIndex = 0;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = Instant.now();
    }
}
