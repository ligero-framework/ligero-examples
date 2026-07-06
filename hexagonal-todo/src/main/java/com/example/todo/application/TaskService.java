package com.example.todo.application;

import com.example.todo.domain.model.Task;
import com.example.todo.domain.port.TaskRepository;
import com.example.todo.domain.port.TaskUseCases;
import com.ligero.http.NotFoundException;

import java.util.List;
import java.util.UUID;

/**
 * Capa de aplicación: implementa los casos de uso orquestando el dominio.
 * Depende solo de puertos — no sabe si la persistencia es memoria, JDBC o
 * una API externa, ni si la entrada es HTTP, CLI o una cola.
 */
public final class TaskService implements TaskUseCases {

    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Task> listTasks() {
        return repository.findAll();
    }

    @Override
    public Task createTask(String title) {
        return repository.save(new Task(UUID.randomUUID().toString(), title, false, System.currentTimeMillis()));
    }

    @Override
    public Task completeTask(String id) {
        Task task = repository.findById(id)
            .orElseThrow(() -> new NotFoundException("Task " + id + " not found"));
        return repository.save(task.complete());
    }
}
