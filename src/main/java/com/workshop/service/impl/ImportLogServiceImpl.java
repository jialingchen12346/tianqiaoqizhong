package com.workshop.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.workshop.mapper.ImportLogMapper;
import com.workshop.model.ImportLog;
import com.workshop.service.ImportLogService;
import org.springframework.stereotype.Service;

@Service
public class ImportLogServiceImpl extends ServiceImpl<ImportLogMapper, ImportLog> implements ImportLogService {
} 