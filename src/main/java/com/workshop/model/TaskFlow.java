package com.workshop.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("task_flow")
public class TaskFlow {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Long taskId;           // 任务ID
    private Long processId;        // 工序ID
    private Long operatorId;       // 操作人ID
    private String action;         // 操作类型 ASSIGN:分派, START:开始, COMPLETE:完成, REWORK:返工
    private String remark;         // 备注
    private Date actionTime;       // 操作时间
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableLogic
    private Integer deleted;
} 