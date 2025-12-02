package com.in.xoriant.dto;
import lombok.*;
import java.util.List;

import jakarta.persistence.Column;


@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class ApprovalWorkflowDto {
	@Column(nullable = false)
    private Long id;
    private String name;
    private String conditionsJson;
    private List<ApprovalStepDto> steps;
}
