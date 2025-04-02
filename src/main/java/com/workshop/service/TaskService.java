package com.workshop.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.workshop.model.Task;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface TaskService extends IService<Task> {
    /**
     * 获取所有任务
     * @return 任务列表
     */
    List<Task> getAllTasks();

    /**
     * 根据ID获取任务
     * @param id 任务ID
     * @return 任务
     */
    Task getTaskById(Long id);

    /**
     * 创建新任务
     * @param task 任务数据
     * @return 创建的任务
     */
    Task createTask(Task task);

    /**
     * 分页查询任务列表
     * @param page 分页参数
     * @param workshop 车间
     * @param processId 工序ID
     * @param status 状态
     * @return 任务分页数据
     */
    Page<Task> getTaskList(Page<Task> page, String workshop, Long processId, String status);

    /**
     * 批量分派任务
     * @param taskIds 任务ID列表
     * @param workerId 工人ID
     * @param assignerId 分派人ID
     * @return 是否成功
     */
    boolean assignTasks(List<Long> taskIds, Long workerId, Long assignerId);

    /**
     * 更新任务状态
     * @param taskId 任务ID
     * @param status 新状态
     * @param operatorId 操作人ID
     * @param remark 备注
     * @return 是否成功
     */
    boolean updateTaskStatus(Long taskId, String status, Long operatorId, String remark);

    /**
     * 批量保存任务
     * @param tasks 任务列表
     * @return 是否成功
     */
    boolean saveBatch(List<Task> tasks);

    /**
     * 完成当前工序并流转到下一工序
     * @param taskId 任务ID
     * @param operatorId 操作人ID
     * @param remark 备注
     * @return 是否成功
     */
    boolean completeAndMoveNext(Long taskId, Long operatorId, String remark);

    /**
     * 返工到指定工序
     * @param taskId 任务ID
     * @param processId 目标工序ID
     * @param operatorId 操作人ID
     * @param remark 返工原因
     * @return 是否成功
     */
    boolean rework(Long taskId, Long processId, Long operatorId, String remark);

    /**
     * 质检任务
     * @param taskId 任务ID
     * @param operatorId 质检人ID
     * @param result 质检结果
     * @param remark 质检备注
     * @return 是否成功
     */
    boolean qualityCheck(Long taskId, Long operatorId, String result, String remark);

    /**
     * 上传任务图纸
     */
    String uploadDrawing(MultipartFile file, Long taskId);

    /**
     * 获取待处理任务
     */
    Page<Task> getPendingTasks(Page<Task> page, Long workerId);

    /**
     * 获取待质检任务
     */
    Page<Task> getPendingQCTasks(Page<Task> page);

    /**
     * 根据状态获取任务列表
     * @param status 任务状态
     * @return 任务列表
     */
    List<Task> getTasksByStatus(String status);

    /**
     * 根据工人ID获取任务列表
     * @param workerId 工人ID
     * @return 任务列表
     */
    List<Task> getTasksByWorkerId(Long workerId);

    /**
     * 更新任务状态（简化版）
     * @param taskId 任务ID
     * @param status 新状态
     * @return 更新后的任务
     */
    Task updateTaskStatus(Long taskId, String status);
}
