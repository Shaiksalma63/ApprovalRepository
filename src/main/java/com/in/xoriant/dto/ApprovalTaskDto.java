package com.in.xoriant.dto;
import lombok.*;
import java.time.Instant;


@Getter
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class ApprovalTaskDto {
    private Long id;
    private String entityType;
    private Long entityId;
    private Integer currentStepIndex;
    private String status;
    private String lockOwner;
    private Instant lockExpiresAt;
    private Instant createdAt;
    private Instant updatedAt;
}

