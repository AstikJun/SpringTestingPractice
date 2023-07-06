package com.example.springrestpractice.controller;

import com.example.springrestpractice.records.Task;
import com.example.springrestpractice.repos.InMemTaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvc.*;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
class TasksRestControllerIT {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    InMemTaskRepository taskRepository;

    @Test
    void handleGetAllTasks_ReturnsValidResponseEntity() throws Exception {
        //given

        var requestBuilder = MockMvcRequestBuilders.get("/api/tasks");
        this.taskRepository.getTasks()
                .addAll(List.of(new Task(UUID.fromString("c5c8cc73-8724-491f-afba-143aea480aee"), "Первая задача", false),
                        new Task(UUID.fromString("0742f17e-aac5-4681-8596-30f57d6ac680"), "Вторая задача", true)
                ));

        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                [
                                {
                                "id": "c5c8cc73-8724-491f-afba-143aea480aee",
                                "details": "Первая задача",
                                "completed": false
                                },
                                {
                                "id": "0742f17e-aac5-4681-8596-30f57d6ac680",
                                "details": "Вторая задача",
                                "completed": true
                                }
                                ]
                                """)
                );
    }

    @Test
    void handleCreateNewTask_PayloadIsInvalid_ReturnsValidResponseEntity() throws Exception {
        //given

        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.ACCEPT_LANGUAGE, "en ")
                .content("""
                        {
                        "details": null
                         }
                        """);
        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isBadRequest(),
                        header().doesNotExist(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "errors": ["Task details must be set"]                                }
                                """, true)
                );

        assertTrue(this.taskRepository.getTasks().isEmpty());


    }

    @Test
    void handleCreateNewTask_PayloadIsValid_ReturnsValidResponseEntity() throws Exception {
        //given

        var requestBuilder = post("/api/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                        "details": "Third task"
                         }
                        """);
        //when
        this.mockMvc.perform(requestBuilder)
                //then
                .andExpectAll(
                        status().isCreated(),
                        header().exists(HttpHeaders.LOCATION),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "details": "Third task",
                                "completed": false
                                }
                                """),
                        jsonPath("$.id").exists());
        assertEquals(1, this.taskRepository.getTasks().size());
        final var task = this.taskRepository.getTasks().get(0);
        assertNotNull(task.id());
        assertEquals("Third task", task.details());
        assertFalse(task.completed());


    }
}



