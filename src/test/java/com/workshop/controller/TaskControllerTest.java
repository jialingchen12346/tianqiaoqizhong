package com.workshop.controller;

import com.workshop.common.Result;
import com.workshop.dto.TaskAssignDTO;
import com.workshop.dto.TaskStatusDTO;
import com.workshop.dto.QCResultDTO;
import com.workshop.model.Task;
import com.workshop.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;

import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.hasSize;

import java.util.Collections;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username="test", roles={"USER"})
class TaskControllerTest {

    @BeforeEach
    void setup(WebApplicationContext webApplicationContext) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Test
    void assignTasks_ShouldReturnSuccess() throws Exception {
        TaskAssignDTO dto = new TaskAssignDTO();
        dto.setTaskIds(Collections.singletonList(1L));
        dto.setWorkerId(1L);
        dto.setAssignerId(2L);
        
        when(taskService.assignTasks(any(), any(), any())).thenReturn(true);
        
        mockMvc.perform(post("/task/assign")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"taskIds\":[1],\"workerId\":1,\"assignerId\":2}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data", is(true)));
    }

    @Test
    void uploadDrawing_ShouldReturnFilename() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
            "file", "test.pdf", "application/pdf", "test content".getBytes());
        
        when(taskService.uploadDrawing(any(), any())).thenReturn("test.pdf");
        
        mockMvc.perform(multipart("/task/drawing")
               .file(file)
               .param("taskId", "1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data", is("test.pdf")));
    }

    @Test
    void getPendingTasks_ShouldReturnPage() throws Exception {
        Page<Task> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new Task()));
        
        when(taskService.getPendingTasks(any(), any())).thenReturn(page);
        
        mockMvc.perform(get("/task/pending?workerId=1"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    void updateTaskStatus_ShouldReturnSuccess() throws Exception {
        TaskStatusDTO dto = new TaskStatusDTO();
        dto.setTaskId(1L);
        dto.setStatus("IN_PROGRESS");
        dto.setOperatorId(1L);
        
        when(taskService.updateTaskStatus(any(), any(), any(), any())).thenReturn(true);
        
        mockMvc.perform(post("/task/status")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"taskId\":1,\"status\":\"IN_PROGRESS\",\"operatorId\":1}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data", is(true)));
    }

    @Test
    void getPendingQCTasks_ShouldReturnPage() throws Exception {
        Page<Task> page = new Page<>(1, 10);
        page.setRecords(Collections.singletonList(new Task()));
        
        when(taskService.getPendingQCTasks(any())).thenReturn(page);
        
        mockMvc.perform(get("/task/qc/pending"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data.content", hasSize(1)));
    }

    @Test
    void updateQCResult_ShouldReturnSuccess() throws Exception {
        QCResultDTO dto = new QCResultDTO();
        dto.setTaskId(1L);
        dto.setOperatorId(1L);
        dto.setResult("PASS");
        
        when(taskService.qualityCheck(any(), any(), any(), any())).thenReturn(true);
        
        mockMvc.perform(post("/task/qc/result")
               .contentType(MediaType.APPLICATION_JSON)
               .content("{\"taskId\":1,\"operatorId\":1,\"result\":\"PASS\"}"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.data", is(true)));
    }
}
