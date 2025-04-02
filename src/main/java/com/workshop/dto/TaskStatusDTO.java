package com.workshop.dto;

import lombok.Data;

@Data
public class TaskStatusDTO {
    private Long taskId;
    private String status;
    private Long operatorId;
    private String remark;
} 