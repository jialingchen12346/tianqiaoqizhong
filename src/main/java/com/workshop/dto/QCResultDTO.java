package com.workshop.dto;

import lombok.Data;

@Data
public class QCResultDTO {
    private Long taskId;
    private Long operatorId;
    private String result;  // PASS/REWORK/SCRAP
    private String remark;
} 