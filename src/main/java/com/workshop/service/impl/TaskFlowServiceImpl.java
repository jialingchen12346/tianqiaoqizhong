package com.workshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workshop.mapper.TaskFlowMapper;
import com.workshop.model.TaskFlow;
import com.workshop.service.TaskFlowService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class TaskFlowServiceImpl extends ServiceImpl<TaskFlowMapper, TaskFlow> implements TaskFlowService {

    @Override
    public List<TaskFlow> getTaskFlowList(Long taskId) {
        LambdaQueryWrapper<TaskFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TaskFlow::getTaskId, taskId)
               .orderByAsc(TaskFlow::getActionTime);
        return this.list(wrapper);
    }

    @Override
    public Page<TaskFlow> getTaskFlowPage(Page<TaskFlow> page, Long taskId, Long processId, String action) {
        LambdaQueryWrapper<TaskFlow> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (taskId != null) {
            wrapper.eq(TaskFlow::getTaskId, taskId);
        }
        if (processId != null) {
            wrapper.eq(TaskFlow::getProcessId, processId);
        }
        if (action != null) {
            wrapper.eq(TaskFlow::getAction, action);
        }
        
        // 按操作时间降序排序
        wrapper.orderByDesc(TaskFlow::getActionTime);
        
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addTaskFlow(TaskFlow taskFlow) {
        return this.save(taskFlow);
    }
} 