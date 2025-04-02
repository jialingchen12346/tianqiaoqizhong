package com.workshop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workshop.mapper.TaskMapper;
import com.workshop.model.Task;
import com.workshop.model.TaskFlow;
import com.workshop.model.Process;
import com.workshop.service.TaskFlowService;
import com.workshop.service.TaskService;
import com.workshop.service.NotificationService;
import com.workshop.service.ProcessService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class TaskServiceImpl extends ServiceImpl<TaskMapper, Task> implements TaskService {

    @Autowired
    private TaskFlowService taskFlowService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private ProcessService processService;

    @Override
    public Page<Task> getTaskList(Page<Task> page, String workshop, Long processId, String status) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        
        // 添加查询条件
        if (StringUtils.hasText(workshop)) {
            wrapper.eq(Task::getWorkshop, workshop);
        }
        if (processId != null) {
            wrapper.eq(Task::getCurrentProcessId, processId);
        }
        if (StringUtils.hasText(status)) {
            wrapper.eq(Task::getStatus, status);
        }
        
        // 按优先级升序，创建时间降序排序
        wrapper.orderByAsc(Task::getPriority)
               .orderByDesc(Task::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignTasks(List<Long> taskIds, Long workerId, Long assignerId) {
        // 批量更新任务
        for (Long taskId : taskIds) {
            Task task = this.getById(taskId);
            if (task != null) {
                task.setWorkerId(workerId);
                task.setAssignerId(assignerId);
                task.setStatus("PROCESSING");
                this.updateById(task);

                // 记录任务流转
                TaskFlow taskFlow = new TaskFlow();
                taskFlow.setTaskId(taskId);
                taskFlow.setProcessId(task.getCurrentProcessId());
                taskFlow.setOperatorId(assignerId);
                taskFlow.setAction("ASSIGN");
                taskFlow.setActionTime(new Date());
                taskFlowService.save(taskFlow);

                // 发送通知
                notificationService.sendTaskNotification(workerId, "TASK_ASSIGN", 
                    "新任务分派", "您有一个新的任务：" + task.getTaskName(), taskId);
            }
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateTaskStatus(Long taskId, String status, Long operatorId, String remark) {
        Task task = this.getById(taskId);
        if (task == null) {
            return false;
        }

        // 更新任务状态
        task.setStatus(status);
        this.updateById(task);

        // 记录任务流转
        TaskFlow taskFlow = new TaskFlow();
        taskFlow.setTaskId(taskId);
        taskFlow.setProcessId(task.getCurrentProcessId());
        taskFlow.setOperatorId(operatorId);
        taskFlow.setAction(status);
        taskFlow.setRemark(remark);
        taskFlow.setActionTime(new Date());
        taskFlowService.save(taskFlow);

        // 根据不同状态发送相应通知
        switch (status) {
            case "COMPLETED":
                notificationService.sendTaskNotification(task.getAssignerId(), "PROCESS_COMPLETE",
                    "工序完成", "任务：" + task.getTaskName() + " 已完成", taskId);
                break;
            case "QC":
                // 通知质检人员
                notificationService.sendQCNotification(task.getTaskName(), taskId);
                break;
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveBatch(List<Task> tasks) {
        return super.saveBatch(tasks);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean completeAndMoveNext(Long taskId, Long operatorId, String remark) {
        Task task = this.getById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        // 记录当前工序完成
        this.updateTaskStatus(taskId, "COMPLETED", operatorId, remark);

        // 获取下一道工序
        Process nextProcess = processService.getNextProcess(task.getCurrentProcessId());
        if (nextProcess == null) {
            // 如果没有下一道工序，则进入质检环节
            task.setStatus("QC");
            task.setCurrentProcessId(null);
            this.updateById(task);

            // 记录流转
            TaskFlow taskFlow = new TaskFlow();
            taskFlow.setTaskId(taskId);
            taskFlow.setProcessId(task.getCurrentProcessId());
            taskFlow.setOperatorId(operatorId);
            taskFlow.setAction("TO_QC");
            taskFlow.setRemark("工序完成，进入质检");
            taskFlow.setActionTime(new Date());
            taskFlowService.save(taskFlow);

            // 发送质检通知
            notificationService.sendQCNotification(task.getTaskName(), taskId);
        } else {
            // 流转到下一道工序
            task.setStatus("PENDING");
            task.setCurrentProcessId(nextProcess.getId());
            task.setWorkerId(null);  // 清空工人ID，等待分派
            this.updateById(task);

            // 记录流转
            TaskFlow taskFlow = new TaskFlow();
            taskFlow.setTaskId(taskId);
            taskFlow.setProcessId(nextProcess.getId());
            taskFlow.setOperatorId(operatorId);
            taskFlow.setAction("NEXT_PROCESS");
            taskFlow.setRemark("流转到下一工序：" + nextProcess.getProcessName());
            taskFlow.setActionTime(new Date());
            taskFlowService.save(taskFlow);
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean rework(Long taskId, Long processId, Long operatorId, String remark) {
        Task task = this.getById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        Process targetProcess = processService.getById(processId);
        if (targetProcess == null) {
            throw new RuntimeException("目标工序不存在");
        }

        // 更新任务状态
        task.setStatus("PENDING");
        task.setCurrentProcessId(processId);
        task.setWorkerId(null);  // 清空工人ID，等待重新分派
        this.updateById(task);

        // 记录流转
        TaskFlow taskFlow = new TaskFlow();
        taskFlow.setTaskId(taskId);
        taskFlow.setProcessId(processId);
        taskFlow.setOperatorId(operatorId);
        taskFlow.setAction("REWORK");
        taskFlow.setRemark("返工到工序：" + targetProcess.getProcessName() + "，原因：" + remark);
        taskFlow.setActionTime(new Date());
        taskFlowService.save(taskFlow);

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean qualityCheck(Long taskId, Long operatorId, String result, String remark) {
        Task task = this.getById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        task.setQcResult(result);
        task.setQcRemark(remark);

        switch (result) {
            case "PASS":
                // 质检通过，更新状态为已入库
                task.setStatus("STORED");
                break;
            case "REWORK":
                // 需要返工，状态设为待处理
                task.setStatus("PENDING");
                break;
            case "SCRAP":
                // 报废
                task.setStatus("SCRAPPED");
                break;
            default:
                throw new RuntimeException("无效的质检结果");
        }

        this.updateById(task);

        // 记录流转
        TaskFlow taskFlow = new TaskFlow();
        taskFlow.setTaskId(taskId);
        taskFlow.setOperatorId(operatorId);
        taskFlow.setAction("QC_" + result);
        taskFlow.setRemark("质检结果：" + result + "，备注：" + remark);
        taskFlow.setActionTime(new Date());
        taskFlowService.save(taskFlow);

        // 如果需要返工，通知相关人员
        if ("REWORK".equals(result)) {
            notificationService.sendTaskNotification(
                task.getAssignerId(),
                "TASK_REWORK",
                "任务需要返工",
                "任务：" + task.getTaskName() + " 质检不通过，需要返工。原因：" + remark,
                taskId
            );
        }

        return true;
    }

    @Override
    public String uploadDrawing(MultipartFile file, Long taskId) {
        // TODO: 实现文件上传逻辑
        return "drawing_url";
    }

    @Override
    public Page<Task> getPendingTasks(Page<Task> page, Long workerId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getWorkerId, workerId)
               .eq(Task::getStatus, "PENDING")
               .orderByAsc(Task::getPriority)
               .orderByDesc(Task::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public Page<Task> getPendingQCTasks(Page<Task> page) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getStatus, "QC")
               .orderByAsc(Task::getPriority)
               .orderByDesc(Task::getCreateTime);
        return this.page(page, wrapper);
    }

    @Override
    public List<Task> getAllTasks() {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Task::getPriority)
               .orderByDesc(Task::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public Task getTaskById(Long id) {
        return this.getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task createTask(Task task) {
        task.setCreateTime(new Date());
        task.setStatus("PENDING");
        this.save(task);
        return task;
    }

    @Override
    public List<Task> getTasksByStatus(String status) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getStatus, status)
               .orderByAsc(Task::getPriority)
               .orderByDesc(Task::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    public List<Task> getTasksByWorkerId(Long workerId) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getWorkerId, workerId)
               .orderByAsc(Task::getPriority)
               .orderByDesc(Task::getCreateTime);
        return this.list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task updateTaskStatus(Long taskId, String status) {
        Task task = this.getById(taskId);
        if (task == null) {
            throw new RuntimeException("任务不存在");
        }

        task.setStatus(status);
        this.updateById(task);

        // 记录任务流转
        TaskFlow taskFlow = new TaskFlow();
        taskFlow.setTaskId(taskId);
        taskFlow.setProcessId(task.getCurrentProcessId());
        taskFlow.setAction(status);
        taskFlow.setActionTime(new Date());
        taskFlowService.save(taskFlow);

        return task;
    }
}
