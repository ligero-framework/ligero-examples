package com.example.todo;

import com.example.todo.adapter.in.http.TaskHttpAdapter;
import com.example.todo.adapter.out.persistence.InMemoryTaskRepository;
import com.example.todo.application.TaskService;
import com.ligero.test.LigeroTest;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class HexagonalTodoTest {

    @Test
    void createAndCompleteTaskThroughHttpAdapter() {
        try (LigeroTest test = LigeroTest.create(app ->
                new TaskHttpAdapter(new TaskService(new InMemoryTaskRepository())).register(app))) {

            var created = test.post("/api/tasks").json("{\"title\":\"aprender hexagonal\"}").execute();
            assertEquals(201, created.status());
            String id = created.body().replaceAll(".*\"id\":\"([^\"]+)\".*", "$1");

            var completed = test.post("/api/tasks/" + id + "/complete").execute();
            assertTrue(completed.body().contains("\"done\":true"));

            assertEquals(404, test.post("/api/tasks/nope/complete").execute().status());
            assertEquals(400, test.post("/api/tasks").json("{}").execute().status());
        }
    }
}
