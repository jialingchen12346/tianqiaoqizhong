package com.workshop.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("task")
public class Task {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String taskNo;          // 任务编号
    private String taskName;        // 任务名称
    private String description;     // 任务描述
    private String status;          // PENDING:待处理, PROCESSING:进行中, COMPLETED:已完成, QC:质检中, STORED:已入库
    private Integer priority;       // 优先级 1-5
    private Long currentProcessId;  // 当前工序ID
    private String attachmentUrl;   // 附件URL
    private Long assignerId;        // 分派人ID
    private Long workerId;          // 工人ID
    private Date deadline;          // 截止时间
    private String qcResult;        // 质检结果 PASS:合格, REWORK:返工, SCRAP:报废
    private String qcRemark;        // 质检备注
    private String workshop;        // 所属车间
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    
    @TableLogic
    private Integer deleted;
} 