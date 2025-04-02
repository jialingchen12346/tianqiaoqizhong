package com.workshop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workshop.common.Result;
import com.workshop.dto.TaskAssignDTO;
import com.workshop.dto.TaskStatusDTO;
import com.workshop.dto.QCResultDTO;
import com.workshop.model.Task;
import com.workshop.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/task")
public class TaskController {

    @Autowired
    private TaskService taskService;

    // 主管：批量分派任务
    @PostMapping("/assign")
    public Result<Boolean> assignTasks(@RequestBody TaskAssignDTO assignDTO) {
        return Result.success(taskService.assignTasks(assignDTO.getTaskIds(), 
            assignDTO.getWorkerId(), assignDTO.getAssignerId()));
    }

    // 主管：上传图纸
    @PostMapping("/drawing")
    public Result<String> uploadDrawing(@RequestParam("file") MultipartFile file, 
                                      @RequestParam("taskId") Long taskId) {
        return Result.success(taskService.uploadDrawing(file, taskId));
    }

    // 员工：获取待处理任务
    @GetMapping("/pending")
    public Result<Page<Task>> getPendingTasks(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam Long workerId) {
        return Result.success(taskService.getPendingTasks(new Page<>(current, size), workerId));
    }

    // 员工：更新任务状态
    @PostMapping("/status")
    public Result<Boolean> updateTaskStatus(@RequestBody TaskStatusDTO statusDTO) {
        return Result.success(taskService.updateTaskStatus(statusDTO.getTaskId(), 
            statusDTO.getStatus(), statusDTO.getOperatorId(), statusDTO.getRemark()));
    }

    // 质检：获取待质检任务
    @GetMapping("/qc/pending")
    public Result<Page<Task>> getPendingQCTasks(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size) {
        return Result.success(taskService.getPendingQCTasks(new Page<>(current, size)));
    }

    // 质检：更新质检结果
    @PostMapping("/qc/result")
    public Result<Boolean> updateQCResult(@RequestBody QCResultDTO resultDTO) {
        return Result.success(taskService.qualityCheck(resultDTO.getTaskId(), 
            resultDTO.getOperatorId(), resultDTO.getResult(), resultDTO.getRemark()));
    }
} 