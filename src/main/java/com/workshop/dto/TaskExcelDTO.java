package com.workshop.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import java.util.Date;

@Data
public class TaskExcelDTO {
    @ExcelProperty("FBillNo")
    private String billNo;              // 单据编号
    
    @ExcelProperty("FProductId")
    private String productCode;         // 产品编码
    
    @ExcelProperty("FProductId#Name")
    private String productCodeName;     // 产品编码名称
    
    @ExcelProperty("FProductName")
    private String productName;         // 产品名称
    
    @ExcelProperty("FProSpecification")
    private String specification;       // 规格型号
    
    @ExcelProperty("FPlanStartTime")
    private Date planStartTime;         // 计划开工时间
    
    @ExcelProperty("FPlanFinishTime")
    private Date planFinishTime;        // 计划完工时间
    
    @ExcelProperty("FMONumber")
    private String moNumber;            // 生产订单编号
    
    @ExcelProperty("FMOEntrySeq")
    private String moEntrySeq;          // 生产订单行号
    
    @ExcelProperty("FMOUnitId#Name")
    private String unitName;            // 单位名称
    
    @ExcelProperty("FMTONo")
    private String mtoNo;               // 计划跟踪号
    
    @ExcelProperty("F_TQQZ_BaseProperty2")
    private String workshop;            // 执行车间
    
    @ExcelProperty("F_TQQZ_Text2")
    private String planBatchNo;         // 计划批号
    
    @ExcelProperty("F_TQQZ_Text4")
    private String workNo;              // 工号
    
    @ExcelProperty("FProcessId#Name")
    private String processName;         // 工序名称
    
    @ExcelProperty("FActivity1Qty")
    private Double planTotalQuantity;   // 计划总量
    
    @ExcelProperty("FActivity1BaseQty")
    private Double baseQuantity;        // 基本量
    
    @ExcelProperty("FActivity1UnitId#Name")
    private String activityUnitName;    // 活动单位名称
} 