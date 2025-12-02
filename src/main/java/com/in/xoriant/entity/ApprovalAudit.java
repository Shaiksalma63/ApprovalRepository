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
@Table(name = "approval_audit")
public class ApprovalAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
  
    private Long id;
    private Long taskId;
    private String action;
    private String performedBy;
    private String comment;
    private Instant createdAt;

    @PrePersist
    public void prePersist() { createdAt = Instant.now(); }
}
