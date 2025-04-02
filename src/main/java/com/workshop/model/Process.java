package com.workshop.model;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.util.Date;

@Data
@TableName("process")
public class Process {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String processName;     // 工序名称
    private String description;     // 工序描述
    private Integer orderNum;       // 工序顺序
    private String department;      // 所属部门
    private Integer estimatedTime;  // 预计耗时(分钟)
    
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;
    
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;
    
    @TableLogic
    private Integer deleted;
} 