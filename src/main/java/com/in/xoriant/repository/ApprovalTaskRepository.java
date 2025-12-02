package com.in.xoriant.repository;



import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.in.xoriant.entity.ApprovalTask;

import jakarta.persistence.LockModeType;

public interface ApprovalTaskRepository extends JpaRepository<ApprovalTask, Long> {
    List<ApprovalTask> findByLockOwner(String lockOwner);
    List<ApprovalTask> findByStatus(String status);
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select t from ApprovalTask t where t.id = :id")
    Optional<ApprovalTask> findByIdForUpdate(UUID id);
    List<ApprovalTask> findByLockOwnerIsNullAndStatus(String status);
}
