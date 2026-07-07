package com.example.jpatodo.repository;

import com.example.jpatodo.domain.Todo;

import java.util.List;
import java.util.Optional;

/** Data-access layer. An interface so devtools can trace it and tests can fake it. */
public interface TodoRepository {

    List<Todo> all();

    Optional<Todo> find(long id);

    Todo add(String title);

    boolean complete(long id);

    boolean delete(long id);
}
