package com.in.xoriant.dto;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalStepDto {
    private Long id;
    private Integer stepOrder;
    private String role;
    private Long approverId;
}
