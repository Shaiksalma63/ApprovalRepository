package com.in.xoriant.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.in.xoriant.entity.ApprovalAudit;

public interface ApprovalAuditRepository extends JpaRepository<ApprovalAudit, Long> {
    List<ApprovalAudit> findByTaskId(UUID taskId);
}

