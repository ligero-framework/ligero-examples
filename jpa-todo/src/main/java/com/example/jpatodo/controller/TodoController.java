package com.example.jpatodo.controller;

import com.example.jpatodo.service.TodoService;
import com.ligero.Ligero;
import com.ligero.beans.stereotype.Controller;

@Controller
public class TodoController {

    public record NewTodo(String title) {
    }

    private final TodoService service;

    public TodoController(TodoService service) {
        this.service = service;
    }

    public void register(Ligero app) {
        app.group("/todos", api -> {
            api.get("", ctx -> ctx.json(service.list()));

            api.get("/{id}", ctx -> ctx.json(service.get(ctx.pathParamAsLong("id"))));

            api.post("", ctx -> {
                NewTodo body = ctx.bodyValidator(NewTodo.class)
                    .check(t -> t.title() != null && !t.title().isBlank(), "title is required")
                    .get();
                ctx.status(201).json(service.create(body.title()));
            });

            api.post("/{id}/complete", ctx -> {
                service.complete(ctx.pathParamAsLong("id"));
                ctx.status(204).res().end();
            });

            api.delete("/{id}", ctx -> {
                service.delete(ctx.pathParamAsLong("id"));
                ctx.status(204).res().end();
            });
        });
    }
}
