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
public class ApproveRequest {
	@Column(nullable = false)
    private Long userId;
    private String comment;
}

