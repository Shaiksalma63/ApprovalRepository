package com.in.xoriant.service;
import java.util.List;

import com.in.xoriant.dto.ApprovalTaskDto;
import com.in.xoriant.dto.ApprovalWorkflowDto;
import com.in.xoriant.dto.CreateTaskRequest;
import com.in.xoriant.entity.ApprovalAudit;
public interface ApprovalService {
	ApprovalWorkflowDto createWorkflow(ApprovalWorkflowDto dto);

    ApprovalWorkflowDto getWorkflow(Long id);

    ApprovalTaskDto createTask(CreateTaskRequest req);

    ApprovalTaskDto claimTask(Long taskId, Long userId, long ttlSeconds);

    ApprovalTaskDto approveTask(Long taskId, Long userId, String comment);

    ApprovalTaskDto rejectTask(Long taskId, Long userId, String comment);

    List<ApprovalTaskDto> listTasks(String assignee, String status);

    List<ApprovalAudit> getAudit(Long taskId);
}
