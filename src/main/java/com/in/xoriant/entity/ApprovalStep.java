package com.in.xoriant.entity;
import jakarta.persistence.*;
import lombok.*;


@Getter 
@Setter 
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "approval_steps")
public class ApprovalStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Integer stepOrder;
    private String role;
   private Long approverId;
}
