package com.workshop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workshop.common.Result;
import com.workshop.model.TaskFlow;
import com.workshop.service.TaskFlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/taskFlow")
public class TaskFlowController {

    @Autowired
    private TaskFlowService taskFlowService;

    @GetMapping("/list/{taskId}")
    public Result<List<TaskFlow>> getTaskFlowList(@PathVariable Long taskId) {
        List<TaskFlow> flowList = taskFlowService.getTaskFlowList(taskId);
        return Result.success(flowList);
    }

    @GetMapping("/page")
    public Result<Page<TaskFlow>> getTaskFlowPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long taskId,
            @RequestParam(required = false) Long processId,
            @RequestParam(required = false) String action) {
        Page<TaskFlow> page = new Page<>(current, size);
        Page<TaskFlow> flowPage = taskFlowService.getTaskFlowPage(page, taskId, processId, action);
        return Result.success(flowPage);
    }

    @PostMapping
    public Result<Boolean> addTaskFlow(@RequestBody TaskFlow taskFlow) {
        boolean success = taskFlowService.addTaskFlow(taskFlow);
        return Result.success(success);
    }
} 