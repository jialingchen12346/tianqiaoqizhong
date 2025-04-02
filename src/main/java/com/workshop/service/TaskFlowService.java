package com.workshop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.workshop.model.TaskFlow;

import java.util.List;

public interface TaskFlowService extends IService<TaskFlow> {
    /**
     * 获取任务的流转记录
     * @param taskId 任务ID
     * @return 流转记录列表
     */
    List<TaskFlow> getTaskFlowList(Long taskId);

    /**
     * 分页查询流转记录
     * @param page 分页参数
     * @param taskId 任务ID
     * @param processId 工序ID
     * @param action 操作类型
     * @return 流转记录分页数据
     */
    Page<TaskFlow> getTaskFlowPage(Page<TaskFlow> page, Long taskId, Long processId, String action);

    /**
     * 添加流转记录
     * @param taskFlow 流转记录
     * @return 是否成功
     */
    boolean addTaskFlow(TaskFlow taskFlow);
} 