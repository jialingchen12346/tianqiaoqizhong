package com.workshop.controller;

import com.workshop.common.Result;
import com.workshop.model.ImportLog;
import com.workshop.service.ImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/import")
public class ImportController {

    @Autowired
    private ImportService importService;

    @PostMapping("/excel")
    public Result<ImportLog> importExcel(@RequestParam("file") MultipartFile file,
                                       @RequestParam("userId") Long userId) {
        ImportLog importLog = importService.importExcel(file, userId);
        return Result.success(importLog);
    }
} 