package com.in.xoriant.dto;
import lombok.*;
import java.util.List;


@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
public class ApprovalWorkflowDto {
    private Long id;
    private String name;
    private String conditionsJson;
    private List<ApprovalStepDto> steps;
}
