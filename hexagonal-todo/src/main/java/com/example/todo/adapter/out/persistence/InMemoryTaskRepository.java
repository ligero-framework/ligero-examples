package com.example.todo.adapter.out.persistence;

import com.example.todo.domain.model.Task;
import com.example.todo.domain.port.TaskRepository;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Adaptador de salida: implementación del puerto de persistencia en memoria. */
public final class InMemoryTaskRepository implements TaskRepository {

    private final Map<String, Task> store = new ConcurrentHashMap<>();

    @Override
    public List<Task> findAll() {
        return store.values().stream()
            .sorted(Comparator.comparingLong(Task::createdAtEpochMs))
            .toList();
    }

    @Override
    public Optional<Task> findById(String id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Task save(Task task) {
        store.put(task.id(), task);
        return task;
    }
}
