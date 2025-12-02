package com.in.xoriant.controller;



package com.in.xoriant.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.in.xoriant.dto.ApprovalTaskDto;
import com.in.xoriant.dto.ApprovalWorkflowDto;
import com.in.xoriant.dto.ApproveRequest;
import com.in.xoriant.dto.ClaimRequest;
import com.in.xoriant.dto.CreateTaskRequest;
import com.in.xoriant.entity.ApprovalAudit;
import com.in.xoriant.response.ApiResponse;
import com.in.xoriant.serviceImpl.ApprovalServiceImpl;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Validated
public class ApprovalController {

    @Autowired
    private final ApprovalServiceImpl service;

    // --------------------------
    // CREATE WORKFLOW
    // --------------------------
    @PostMapping("/workflows")
    public ResponseEntity<ApiResponse<ApprovalWorkflowDto>> createWorkflow(
            @Valid @RequestBody ApprovalWorkflowDto dto) {

        ApiResponse<ApprovalWorkflowDto> response = new ApiResponse<>();
        ApprovalWorkflowDto workflow = service.createWorkflow(dto);

        if (workflow != null) {
            response.setCode(200);
            response.setMessage("Workflow Created Successfully");
            response.setData(workflow);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setCode(500);
            response.setMessage("Workflow Creation Failed");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // --------------------------
    // GET WORKFLOW
    // --------------------------
    @GetMapping("/workflows/{id}")
    public ResponseEntity<ApiResponse<ApprovalWorkflowDto>> getWorkflow(@PathVariable UUID id) {
        ApiResponse<ApprovalWorkflowDto> response = new ApiResponse<>();
        ApprovalWorkflowDto workflow = service.getWorkflow(id);

        if (workflow != null) {
            response.setCode(200);
            response.setMessage("Workflow Fetched Successfully");
            response.setData(workflow);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setCode(404);
            response.setMessage("Workflow Not Found");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    // --------------------------
    // CREATE TASK
    // --------------------------
    @PostMapping("/approval-tasks")
    public ResponseEntity<ApiResponse<ApprovalTaskDto>> createTask(
            @Valid @RequestBody CreateTaskRequest req) {

        ApiResponse<ApprovalTaskDto> response = new ApiResponse<>();
        ApprovalTaskDto task = service.createTask(req);

        if (task != null) {
            response.setCode(200);
            response.setMessage("Approval Task Created Successfully");
            response.setData(task);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setCode(500);
            response.setMessage("Approval Task Creation Failed");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
   
    @PostMapping("/approval-tasks/{id}/claim")
    public ResponseEntity<ApiResponse<ApprovalTaskDto>> claimTask(
            @PathVariable Long id,
            @Valid @RequestBody ClaimRequest req) {

        ApiResponse<ApprovalTaskDto> response = new ApiResponse<>();
        ApprovalTaskDto task = service.claimTask(id, req.getUserId(), 60);

        if (task != null) {
            response.setCode(200);
            response.setMessage("Task Claimed Successfully");
            response.setData(task);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setCode(500);
            response.setMessage("Task Claim Failed");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @PostMapping("/approval-tasks/{id}/approve")
    public ResponseEntity<ApiResponse<ApprovalTaskDto>> approveTask(
            @PathVariable Long id,
            @Valid @RequestBody ApproveRequest req) {

        ApiResponse<ApprovalTaskDto> response = new ApiResponse<>();
        ApprovalTaskDto task = service.approveTask(id, req.getUserId(), req.getComment());

        if (task != null) {
            response.setCode(200);
            response.setMessage("Task Approved Successfully");
            response.setData(task);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setCode(500);
            response.setMessage("Task Approval Failed");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

   
    @PostMapping("/approval-tasks/{id}/reject")
    public ResponseEntity<ApiResponse<ApprovalTaskDto>> rejectTask(
            @PathVariable Long id,
            @Valid @RequestBody ApproveRequest req) {

        ApiResponse<ApprovalTaskDto> response = new ApiResponse<>();
        ApprovalTaskDto task = service.rejectTask(id, req.getUserId(), req.getComment());

        if (task != null) {
            response.setCode(200);
            response.setMessage("Task Rejected Successfully");
            response.setData(task);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.setCode(500);
            response.setMessage("Task Rejection Failed");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    
    @GetMapping("/approval-tasks")
    public ResponseEntity<ApiResponse<List<ApprovalTaskDto>>> listTasks(
            @RequestParam(required = false) String assignee,
            @RequestParam(required = false) String status) {

        ApiResponse<List<ApprovalTaskDto>> response = new ApiResponse<>();
        List<ApprovalTaskDto> tasks = service.listTasks(assignee, status);

        if (tasks.isEmpty()) {
            response.setCode(404);
            response.setMessage("No Tasks Found");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            response.setCode(200);
            response.setMessage("Tasks Fetched Successfully");
            response.setData(tasks);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    
    @GetMapping("/approval-tasks/{id}/audit")
    public ResponseEntity<ApiResponse<List<ApprovalAudit>>> audit(@PathVariable Long id) {

        ApiResponse<List<ApprovalAudit>> response = new ApiResponse<>();
        List<ApprovalAudit> auditList = service.getAudit(id);

        if (auditList.isEmpty()) {
            response.setCode(404);
            response.setMessage("No Audit Records Found");
            response.setData(null);
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } else {
            response.setCode(200);
            response.setMessage("Audit Records Fetched Successfully");
            response.setData(auditList);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}

