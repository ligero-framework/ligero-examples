package com.example.jpatodo;

import com.example.jpatodo.controller.TodoController;
import com.example.jpatodo.repository.JpaTodoRepository;
import com.example.jpatodo.repository.TodoRepository;
import com.example.jpatodo.service.DefaultTodoService;
import com.example.jpatodo.service.TodoService;
import com.ligero.Ligero;
import com.ligero.LigeroModule;
import com.ligero.beans.Beans;
import com.ligero.jpa.Jpa;

/**
 * The "todos" feature: boots the JPA unit and wires the layers. The {@link Jpa}
 * bean is closed automatically on shutdown (it is {@link AutoCloseable}, and
 * the container closes beans in reverse order).
 */
public final class TodosModule implements LigeroModule {

    @Override
    public void beans(Beans.Builder builder) {
        builder.bind(Jpa.class,            b -> Jpa.forUnit("todo"));
        builder.bind(TodoRepository.class, b -> new JpaTodoRepository(b.get(Jpa.class)));
        builder.bind(TodoService.class,    b -> new DefaultTodoService(b.get(TodoRepository.class)));
        builder.bind(TodoController.class, b -> new TodoController(b.get(TodoService.class)));
    }

    @Override
    public void routes(Ligero app, Beans beans) {
        beans.get(TodoController.class).register(app);
    }
}
