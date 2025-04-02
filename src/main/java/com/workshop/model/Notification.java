package com.workshop.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("notification")
public class Notification {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long userId;           // 接收用户ID
    private String type;           // TASK_ASSIGN:任务分派, PROCESS_COMPLETE:工序完成, QC_REQUIRED:待质检
    private String title;          // 通知标题
    private String content;        // 通知内容
    private Long relatedId;        // 相关任务/工序ID
    private Boolean isRead;        // 是否已读
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableLogic
    private Integer deleted;
} 