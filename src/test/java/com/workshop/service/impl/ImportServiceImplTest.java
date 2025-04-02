package com.workshop.service.impl;

import com.workshop.mapper.ImportLogMapper;
import com.workshop.model.ImportLog;
import com.workshop.service.ImportService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
class ImportServiceImplTest {

    @Mock
    private ImportLogMapper importLogMapper;

    @InjectMocks
    private ImportServiceImpl importService;

    @Test
    void getAllImportLogs_ShouldReturnLogs() {
        ImportLog mockLog = new ImportLog();
        when(importLogMapper.selectList(null)).thenReturn(Collections.singletonList(mockLog));

        List<ImportLog> result = importService.getAllImportLogs();
        
        assertEquals(1, result.size());
        verify(importLogMapper, times(1)).selectList(null);
    }

    @Test
    void getImportLogById_ShouldReturnLog() {
        ImportLog mockLog = new ImportLog();
        mockLog.setId(1L);
        when(importLogMapper.selectById(1L)).thenReturn(mockLog);

        ImportLog result = importService.getImportLogById(1L);
        
        assertEquals(1L, result.getId());
        verify(importLogMapper, times(1)).selectById(1L);
    }

    @Test
    void createImportLog_ShouldInsertAndReturnLog() {
        ImportLog newLog = new ImportLog();
        newLog.setFileName("test.csv");
        when(importLogMapper.insert(newLog)).thenReturn(1);

        ImportLog result = importService.createImportLog(newLog);
        
        assertEquals("test.csv", result.getFileName());
        verify(importLogMapper, times(1)).insert(newLog);
    }
}
