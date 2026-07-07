package com.example.jpatodo.service;

import com.example.jpatodo.domain.Todo;

import java.util.List;

public interface TodoService {

    List<Todo> list();

    Todo get(long id);

    Todo create(String title);

    void complete(long id);

    void delete(long id);
}
