package com.example.todo.domain.port;

import com.example.todo.domain.model.Task;

import java.util.List;

/** Puerto de entrada (driving port): lo que la aplicación ofrece al mundo. */
public interface TaskUseCases {

    List<Task> listTasks();

    Task createTask(String title);

    Task completeTask(String id);
}
