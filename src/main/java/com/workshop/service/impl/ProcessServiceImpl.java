package com.workshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workshop.mapper.ProcessMapper;
import com.workshop.model.Process;
import com.workshop.service.ProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
public class ProcessServiceImpl extends ServiceImpl<ProcessMapper, Process> implements ProcessService {

    @Override
    public List<Process> getProcessList(String department) {
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(department)) {
            wrapper.eq(Process::getDepartment, department);
        }
        wrapper.orderByAsc(Process::getOrderNum);
        return this.list(wrapper);
    }

    @Override
    public Page<Process> getProcessPage(Page<Process> page, String department) {
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(department)) {
            wrapper.eq(Process::getDepartment, department);
        }
        wrapper.orderByAsc(Process::getOrderNum);
        return this.page(page, wrapper);
    }

    @Override
    public Process getProcessByName(String processName) {
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Process::getProcessName, processName);
        return this.getOne(wrapper);
    }

    @Override
    public Process getNextProcess(Long currentProcessId) {
        Process currentProcess = this.getById(currentProcessId);
        if (currentProcess == null) {
            return null;
        }

        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Process::getDepartment, currentProcess.getDepartment())
               .gt(Process::getOrderNum, currentProcess.getOrderNum())
               .orderByAsc(Process::getOrderNum)
               .last("LIMIT 1");
        return this.getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addProcess(Process process) {
        // 检查工序名称是否已存在
        Process existProcess = this.getProcessByName(process.getProcessName());
        if (existProcess != null) {
            throw new RuntimeException("工序名称已存在");
        }

        // 如果未设置顺序号，则自动设置为当前部门最大顺序号+1
        if (process.getOrderNum() == null) {
            Integer maxOrderNum = this.getMaxOrderNum(process.getDepartment());
            process.setOrderNum(maxOrderNum + 1);
        }

        return this.save(process);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateProcess(Process process) {
        // 检查工序名称是否已存在（排除自身）
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Process::getProcessName, process.getProcessName())
               .ne(Process::getId, process.getId());
        if (this.count(wrapper) > 0) {
            throw new RuntimeException("工序名称已存在");
        }

        return this.updateById(process);
    }

    /**
     * 获取部门最大顺序号
     */
    private Integer getMaxOrderNum(String department) {
        LambdaQueryWrapper<Process> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Process::getDepartment, department)
               .orderByDesc(Process::getOrderNum)
               .last("LIMIT 1");
        Process process = this.getOne(wrapper);
        return process != null ? process.getOrderNum() : 0;
    }
} 