package com.workshop.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.workshop.model.ImportLog;
import com.workshop.model.Task;
import com.workshop.dto.TaskExcelDTO;
import com.workshop.service.ImportService;
import com.workshop.service.TaskService;
import com.workshop.service.ProcessService;
import com.workshop.service.ImportLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ImportServiceImpl implements ImportService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private ProcessService processService;

    @Autowired
    private ImportLogService importLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ImportLog importExcel(MultipartFile file, Long userId) {
        ImportLog importLog = new ImportLog();
        importLog.setFileName(file.getOriginalFilename());
        importLog.setImporterId(userId);
        
        List<String> errorMessages = new ArrayList<>();
        try {
            // 使用EasyExcel读取文件
            EasyExcel.read(file.getInputStream(), TaskExcelDTO.class, new AnalysisEventListener<TaskExcelDTO>() {
                private List<Task> tasks = new ArrayList<>();
                private int totalCount = 0;
                private int successCount = 0;
                private int failCount = 0;

                @Override
                public void invoke(TaskExcelDTO data, AnalysisContext context) {
                    totalCount++;
                    try {
                        // 数据校验
                        if (validateData(data)) {
                            Task task = convertToTask(data);
                            tasks.add(task);
                            successCount++;
                        } else {
                            failCount++;
                            errorMessages.add("第" + context.readRowHolder().getRowIndex() + "行数据校验失败");
                        }
                    } catch (Exception e) {
                        failCount++;
                        errorMessages.add("第" + context.readRowHolder().getRowIndex() + "行处理异常：" + e.getMessage());
                    }
                }

                @Override
                public void doAfterAllAnalysed(AnalysisContext context) {
                    // 批量保存任务
                    if (!tasks.isEmpty()) {
                        taskService.saveBatch(tasks);
                    }
                    
                    // 更新导入日志
                    importLog.setTotalCount(totalCount);
                    importLog.setSuccessCount(successCount);
                    importLog.setFailCount(failCount);
                    importLog.setErrorLog(String.join("\n", errorMessages));
                    importLog.setStatus(failCount == 0 ? "SUCCESS" : 
                                     (successCount > 0 ? "PARTIAL" : "FAIL"));
                }
            }).sheet().doRead();
            
        } catch (Exception e) {
            log.error("导入Excel异常", e);
            importLog.setStatus("FAIL");
            importLog.setErrorLog("文件处理失败：" + e.getMessage());
        }
        
        importLogService.save(importLog);
        return importLog;
    }

    private boolean validateData(TaskExcelDTO data) {
        if (data.getBillNo() == null || data.getBillNo().trim().isEmpty()) {
            return false;
        }
        if (data.getProductName() == null || data.getProductName().trim().isEmpty()) {
            return false;
        }
        if (data.getProcessName() == null || data.getProcessName().trim().isEmpty()) {
            return false;
        }
        if (data.getPlanTotalQuantity() == null || data.getPlanTotalQuantity() <= 0) {
            return false;
        }
        if (data.getPlanStartTime() == null || data.getPlanFinishTime() == null) {
            return false;
        }
        if (data.getWorkshop() == null || data.getWorkshop().trim().isEmpty()) {
            return false;
        }
        return true;
    }

    private Task convertToTask(TaskExcelDTO data) {
        Task task = new Task();
        // 生成任务编号：单据编号 + 工序名称
        task.setTaskNo(data.getBillNo() + "-" + data.getProcessName());
        // 生成任务名称：产品名称 + 工序名称
        task.setTaskName(data.getProductName() + "-" + data.getProcessName());
        // 生成任务描述
        StringBuilder description = new StringBuilder();
        description.append("产品规格：").append(data.getSpecification()).append("\n");
        description.append("计划批号：").append(data.getPlanBatchNo()).append("\n");
        description.append("工号：").append(data.getWorkNo()).append("\n");
        description.append("计划数量：").append(data.getPlanTotalQuantity())
                  .append(data.getActivityUnitName());
        task.setDescription(description.toString());
        
        // 设置基础信息
        task.setStatus("PENDING");
        task.setPriority(calculatePriority(data.getPlanStartTime(), data.getPlanFinishTime()));
        task.setDeadline(data.getPlanFinishTime());
        task.setWorkshop(data.getWorkshop());
        
        // 其他信息
        task.setCurrentProcessId(null);  // 需要根据工序名称查询对应的工序ID
        task.setAttachmentUrl(null);     // 附件URL待上传
        task.setAssignerId(null);        // 待分派
        task.setWorkerId(null);          // 待分派
        
        return task;
    }

    /**
     * 根据计划时间计算优先级
     * 1: 非常紧急 (已超期)
     * 2: 紧急 (3天内)
     * 3: 普通 (7天内)
     * 4: 较低 (14天内)
     * 5: 低 (14天以上)
     */
    private Integer calculatePriority(Date startTime, Date finishTime) {
        if (startTime == null || finishTime == null) {
            return 5;
        }
        
        long now = System.currentTimeMillis();
        long finish = finishTime.getTime();
        long days = (finish - now) / (1000 * 60 * 60 * 24);
        
        if (days < 0) return 1;
        if (days <= 3) return 2;
        if (days <= 7) return 3;
        if (days <= 14) return 4;
        return 5;
    }

    @Override
    public List<ImportLog> getAllImportLogs() {
        return importLogService.list();
    }

    @Override
    public ImportLog getImportLogById(Long id) {
        return importLogService.getById(id);
    }

    @Override
    public ImportLog createImportLog(ImportLog importLog) {
        importLogService.save(importLog);
        return importLog;
    }
}
