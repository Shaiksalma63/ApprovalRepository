package com.in.xoriant.entity;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;


@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder
@Entity
@Table(name = "approval_workflows")
public class ApprovalWorkflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    
    private Long id;

    private String name;

    @Column(columnDefinition = "TEXT")
    private String conditionsJson;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "workflow_id")
    @OrderBy("stepOrder ASC")
    private List<ApprovalStep> steps;
}
