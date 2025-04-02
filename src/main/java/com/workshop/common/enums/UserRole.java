package com.workshop.common.enums;

public enum UserRole {
    SUPERVISOR("主管"),
    WORKER("员工"),
    QC("质检");

    private String description;

    UserRole(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
} 