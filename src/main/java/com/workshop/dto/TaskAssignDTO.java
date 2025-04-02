package com.workshop.dto;

import lombok.Data;
import java.util.List;

@Data
public class TaskAssignDTO {
    private List<Long> taskIds;
    private Long workerId;
    private Long assignerId;
} 