package com.workshop.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.workshop.common.Result;
import com.workshop.model.Process;
import com.workshop.service.ProcessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/process")
public class ProcessController {

    @Autowired
    private ProcessService processService;

    @GetMapping("/list")
    public Result<List<Process>> getProcessList(
            @RequestParam(required = false) String department) {
        List<Process> processList = processService.getProcessList(department);
        return Result.success(processList);
    }

    @GetMapping("/page")
    public Result<Page<Process>> getProcessPage(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String department) {
        Page<Process> page = new Page<>(current, size);
        Page<Process> processPage = processService.getProcessPage(page, department);
        return Result.success(processPage);
    }

    @GetMapping("/{id}")
    public Result<Process> getProcess(@PathVariable Long id) {
        Process process = processService.getById(id);
        return Result.success(process);
    }

    @GetMapping("/next/{currentProcessId}")
    public Result<Process> getNextProcess(@PathVariable Long currentProcessId) {
        Process nextProcess = processService.getNextProcess(currentProcessId);
        return Result.success(nextProcess);
    }

    @PostMapping
    public Result<Boolean> addProcess(@RequestBody Process process) {
        boolean success = processService.addProcess(process);
        return Result.success(success);
    }

    @PutMapping
    public Result<Boolean> updateProcess(@RequestBody Process process) {
        boolean success = processService.updateProcess(process);
        return Result.success(success);
    }

    @DeleteMapping("/{id}")
    public Result<Boolean> deleteProcess(@PathVariable Long id) {
        boolean success = processService.removeById(id);
        return Result.success(success);
    }
} 