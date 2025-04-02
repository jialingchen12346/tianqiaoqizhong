package com.workshop.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("import_log")
public class ImportLog {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String fileName;        // 文件名
    private Long importerId;        // 导入人ID
    private Integer totalCount;     // 总记录数
    private Integer successCount;   // 成功数
    private Integer failCount;      // 失败数
    private String errorLog;        // 错误日志
    private String status;          // SUCCESS:成功, PARTIAL:部分成功, FAIL:失败
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableLogic
    private Integer deleted;
} 