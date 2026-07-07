package com.example.jpatodo.service;

import com.example.jpatodo.domain.Todo;
import com.example.jpatodo.repository.TodoRepository;
import com.ligero.beans.stereotype.Service;
import com.ligero.http.NotFoundException;

import java.util.List;

@Service
public class DefaultTodoService implements TodoService {

    private final TodoRepository repository;

    public DefaultTodoService(TodoRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Todo> list() {
        return repository.all();
    }

    @Override
    public Todo get(long id) {
        return repository.find(id).orElseThrow(() -> new NotFoundException("Todo " + id + " not found"));
    }

    @Override
    public Todo create(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        return repository.add(title);
    }

    @Override
    public void complete(long id) {
        if (!repository.complete(id)) {
            throw new NotFoundException("Todo " + id + " not found");
        }
    }

    @Override
    public void delete(long id) {
        if (!repository.delete(id)) {
            throw new NotFoundException("Todo " + id + " not found");
        }
    }
}
