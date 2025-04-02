package com.workshop.service;

import com.workshop.model.ImportLog;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface ImportService {
    /**
     * 导入Excel文件
     * @param file Excel文件
     * @param userId 导入用户ID
     * @return 导入日志记录
     */
    ImportLog importExcel(MultipartFile file, Long userId);

    /**
     * 获取所有导入日志
     * @return 导入日志列表
     */
    List<ImportLog> getAllImportLogs();

    /**
     * 根据ID获取导入日志
     * @param id 日志ID
     * @return 导入日志
     */
    ImportLog getImportLogById(Long id);

    /**
     * 创建导入日志
     * @param importLog 导入日志
     * @return 创建的日志
     */
    ImportLog createImportLog(ImportLog importLog);
}
