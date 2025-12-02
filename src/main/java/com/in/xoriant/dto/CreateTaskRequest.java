package com.in.xoriant.dto;


import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor
@Builder
public class CreateTaskRequest {
	@Column(nullable = false)
    private String entityType;
    @Column(nullable = false)
    private Long entityId;
    @Column(nullable = false)
    private Long initiatorId;
}
