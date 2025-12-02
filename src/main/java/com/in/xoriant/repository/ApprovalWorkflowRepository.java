package com.in.xoriant.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.in.xoriant.entity.ApprovalWorkflow;

public interface ApprovalWorkflowRepository extends JpaRepository<ApprovalWorkflow, Long> {
}
