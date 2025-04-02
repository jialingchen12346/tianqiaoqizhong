package com.workshop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.workshop.model.Process;

import java.util.List;

public interface ProcessService extends IService<Process> {
    /**
     * 获取工序列表
     * @param department 部门
     * @return 工序列表
     */
    List<Process> getProcessList(String department);

    /**
     * 分页查询工序
     * @param page 分页参数
     * @param department 部门
     * @return 工序分页数据
     */
    Page<Process> getProcessPage(Page<Process> page, String department);

    /**
     * 根据工序名称获取工序
     * @param processName 工序名称
     * @return 工序信息
     */
    Process getProcessByName(String processName);

    /**
     * 获取下一道工序
     * @param currentProcessId 当前工序ID
     * @return 下一道工序
     */
    Process getNextProcess(Long currentProcessId);

    /**
     * 新增工序
     * @param process 工序信息
     * @return 是否成功
     */
    boolean addProcess(Process process);

    /**
     * 更新工序
     * @param process 工序信息
     * @return 是否成功
     */
    boolean updateProcess(Process process);
} 