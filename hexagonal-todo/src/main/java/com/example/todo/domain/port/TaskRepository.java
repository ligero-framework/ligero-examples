package com.example.todo.domain.port;

import com.example.todo.domain.model.Task;

import java.util.List;
import java.util.Optional;

/** Puerto de salida (driven port): el dominio define el contrato de persistencia. */
public interface TaskRepository {

    List<Task> findAll();

    Optional<Task> findById(String id);

    Task save(Task task);
}
