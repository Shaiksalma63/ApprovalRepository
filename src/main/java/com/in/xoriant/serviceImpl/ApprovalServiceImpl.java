package com.in.xoriant.serviceImpl;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.example.approvalservice.dto.*;
import com.example.approvalservice.entity.*;
import com.example.approvalservice.repository.*;
import com.example.approvalservice.service.ApprovalService;
import com.in.xoriant.dto.ApprovalTaskDto;
import com.in.xoriant.dto.ApprovalWorkflowDto;
import com.in.xoriant.dto.CreateTaskRequest;
import com.in.xoriant.entity.ApprovalAudit;
import com.in.xoriant.entity.ApprovalTask;
import com.in.xoriant.entity.ApprovalWorkflow;
import com.in.xoriant.entity.TaskStatus;
import com.in.xoriant.exception.BadRequestException;
import com.in.xoriant.exception.ResourceNotFoundException;
import com.in.xoriant.repository.ApprovalAuditRepository;
import com.in.xoriant.repository.ApprovalTaskRepository;
import com.in.xoriant.repository.ApprovalWorkflowRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApprovalServiceImpl implements ApprovalService {

    private final ApprovalWorkflowRepository workflowRepo;
    private final ApprovalTaskRepository taskRepo;
    private final ApprovalAuditRepository auditRepo;
    private final ModelMapper mapper;
    private final RoleService roleService;
    private final RestTemplate rest = new RestTemplate();

    @Override
    @Transactional
    public ApprovalWorkflowDto createWorkflow(ApprovalWorkflowDto dto) {
        ApprovalWorkflow w = mapper.map(dto, ApprovalWorkflow.class);
        ApprovalWorkflow saved = workflowRepo.save(w);
        return mapper.map(saved, ApprovalWorkflowDto.class);
    }

    @Override
    public ApprovalWorkflowDto getWorkflow(UUID id) {
        ApprovalWorkflow w = workflowRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Workflow not found"));
        return mapper.map(w, ApprovalWorkflowDto.class);
    }

    @Override
    @Transactional
    public ApprovalTaskDto createTask(CreateTaskRequest req) {
        ApprovalWorkflow wf = workflowRepo.findAll().stream().findFirst().orElse(null);
        ApprovalTask t = ApprovalTask.builder()
                .entityType(req.getEntityType())
                .entityId(req.getEntityId())
                .currentStepIndex(0)
                .status(TaskStatus.PENDING)
                .build();
        ApprovalTask saved = taskRepo.save(t);
        auditRepo.save(ApprovalAudit.builder().taskId(saved.getId()).action("CREATE").performedBy(req.getInitiatorId().toString()).build());
        return mapper.map(saved, ApprovalTaskDto.class);
    }

    @Override
    @Transactional
    public ApprovalTaskDto claimTask(UUID taskId, UUID userId, long ttlSeconds) {
        ApprovalTask t = taskRepo.findByIdForUpdate(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (t.getLockOwner()!=null && t.getLockExpiresAt()!=null && t.getLockExpiresAt().isAfter(Instant.now())) {
            throw new BadRequestException("Task already claimed by "+t.getLockOwner());
        }
        t.setLockOwner(userId.toString());
        t.setLockExpiresAt(Instant.now().plusSeconds(ttlSeconds));
        taskRepo.save(t);
        auditRepo.save(ApprovalAudit.builder().taskId(taskId).action("CLAIM").performedBy(userId.toString()).build());
        return mapper.map(t, ApprovalTaskDto.class);
    }

    @Override
    @Transactional
    public ApprovalTaskDto approveTask(UUID taskId, UUID userId, String comment) {
        ApprovalTask t = taskRepo.findByIdForUpdate(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        if (t.getLockOwner()!=null && !t.getLockOwner().equals(userId.toString()) && t.getLockExpiresAt()!=null && t.getLockExpiresAt().isAfter(Instant.now())) {
            throw new BadRequestException("Task locked by "+t.getLockOwner());
        }
        ApprovalWorkflow wf = workflowRepo.findAll().stream().findFirst().orElse(null);
        String requiredRole = null;
        if (wf!=null && wf.getSteps()!=null && !wf.getSteps().isEmpty()) {
            int idx = t.getCurrentStepIndex()==null?0:t.getCurrentStepIndex();
            if (idx < wf.getSteps().size()) requiredRole = wf.getSteps().get(Math.min(idx, wf.getSteps().size()-1)).getRole();
        }
        if (requiredRole!=null && !roleService.userHasRole(userId, requiredRole)) {
            throw new BadRequestException("User does not have required role: "+requiredRole);
        }
        auditRepo.save(ApprovalAudit.builder().taskId(taskId).action("APPROVE").performedBy(userId.toString()).comment(comment).build());
        int next = (t.getCurrentStepIndex()==null?0:t.getCurrentStepIndex()) + 1;
        boolean finalize = wf==null || wf.getSteps()==null || next >= (wf.getSteps()==null?0:wf.getSteps().size());
        if (finalize) {
            t.setStatus(TaskStatus.APPROVED);
            try {
                String callback = System.getProperty("expense.callback","http://localhost:8080/expenses/" + t.getEntityId() + "/finalize?payoutRef=APR-"+t.getEntityId());
                rest.postForObject(callback, Collections.singletonMap("approved", true), String.class);
            } catch (Exception ex) {
                // ignore in sample
            }
        } else {
            t.setCurrentStepIndex(next);
        }
        t.setLockOwner(null);
        t.setLockExpiresAt(null);
        taskRepo.save(t);
        return mapper.map(t, ApprovalTaskDto.class);
    }

    @Override
    @Transactional
    public ApprovalTaskDto rejectTask(UUID taskId, UUID userId, String comment) {
        ApprovalTask t = taskRepo.findByIdForUpdate(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
        auditRepo.save(ApprovalAudit.builder().taskId(taskId).action("REJECT").performedBy(userId.toString()).comment(comment).build());
        t.setStatus(TaskStatus.REJECTED);
        t.setLockOwner(null);
        t.setLockExpiresAt(null);
        taskRepo.save(t);
        return mapper.map(t, ApprovalTaskDto.class);
    }

    @Override
    public List<ApprovalTaskDto> listTasks(String assignee, String status) {
        List<ApprovalTask> list;
        if (assignee!=null) list = taskRepo.findByLockOwner(assignee);
        else if (status!=null) list = taskRepo.findByStatus(status);
        else list = taskRepo.findAll();
        return list.stream().map(t -> mapper.map(t, ApprovalTaskDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<ApprovalAudit> getAudit(UUID taskId) {
        return auditRepo.findByTaskId(taskId);
    }

    @Override
    @Scheduled(fixedDelay = 60000)
    @Transactional
    public void releaseExpiredLocks() {
        List<ApprovalTask> all = taskRepo.findAll();
        Instant now = Instant.now();
        for (ApprovalTask t : all) {
            if (t.getLockOwner()!=null && t.getLockExpiresAt()!=null && t.getLockExpiresAt().isBefore(now)) {
                t.setLockOwner(null);
                t.setLockExpiresAt(null);
                taskRepo.save(t);
                auditRepo.save(ApprovalAudit.builder().taskId(t.getId()).action("RELEASE").performedBy("system").comment("lock expired").build());
            }
        }
    }
}

