package com.workshop.service.impl;

import com.workshop.mapper.TaskMapper;
import com.workshop.model.Task;
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
class TaskServiceImplTest {

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskServiceImpl taskService;

    @Test
    void getAllTasks_ShouldReturnTasks() {
        Task mockTask = new Task();
        when(taskMapper.selectList(null)).thenReturn(Collections.singletonList(mockTask));

        List<Task> result = taskService.getAllTasks();
        
        assertEquals(1, result.size());
        verify(taskMapper, times(1)).selectList(null);
    }

    @Test
    void getTaskById_ShouldReturnTask() {
        Task mockTask = new Task();
        mockTask.setId(1L);
        when(taskMapper.selectById(1L)).thenReturn(mockTask);

        Task result = taskService.getTaskById(1L);
        
        assertEquals(1L, result.getId());
        verify(taskMapper, times(1)).selectById(1L);
    }

    @Test
    void getTaskById_ShouldThrowWhenNotFound() {
        when(taskMapper.selectById(1L)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> taskService.getTaskById(1L));
    }

    @Test
    void createTask_ShouldInsertAndReturnTask() {
        Task newTask = new Task();
        newTask.setTaskName("Test Task");
        when(taskMapper.insert(newTask)).thenReturn(1);

        Task result = taskService.createTask(newTask);
        
        assertEquals("Test Task", result.getTaskName());
        verify(taskMapper, times(1)).insert(newTask);
    }

    @Test
    void getTasksByStatus_ShouldReturnTasks() {
        Task mockTask = new Task();
        mockTask.setStatus("PENDING");
        when(taskMapper.selectList(any())).thenReturn(Collections.singletonList(mockTask));

        List<Task> result = taskService.getTasksByStatus("PENDING");
        
        assertEquals(1, result.size());
        assertEquals("PENDING", result.get(0).getStatus());
    }

    @Test
    void getTasksByWorkerId_ShouldReturnTasks() {
        Task mockTask = new Task();
        mockTask.setWorkerId(1L);
        when(taskMapper.selectList(any())).thenReturn(Collections.singletonList(mockTask));

        List<Task> result = taskService.getTasksByWorkerId(1L);
        
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getWorkerId());
    }

    @Test
    void updateTaskStatus_ShouldUpdateStatus() {
        Task existingTask = new Task();
        existingTask.setId(1L);
        existingTask.setStatus("PENDING");
        when(taskMapper.selectById(1L)).thenReturn(existingTask);
        when(taskMapper.updateById(any(Task.class))).thenReturn(1);

        Task result = taskService.updateTaskStatus(1L, "COMPLETED");
        
        assertEquals("COMPLETED", result.getStatus());
        verify(taskMapper, times(1)).selectById(1L);
        verify(taskMapper, times(1)).updateById(any(Task.class));
    }
}
