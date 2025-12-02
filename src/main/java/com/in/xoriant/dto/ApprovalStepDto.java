package com.in.xoriant.dto;
import jakarta.persistence.Column;
import lombok.*;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApprovalStepDto {
	@Column(nullable = false)
    private Long id;
    private Integer stepOrder;
    private String role;
    private Long approverId;
}
