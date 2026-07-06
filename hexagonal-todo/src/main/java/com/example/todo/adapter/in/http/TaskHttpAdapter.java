package com.example.todo.adapter.in.http;

import com.example.todo.domain.port.TaskUseCases;
import com.ligero.Ligero;

/**
 * Adaptador de entrada: traduce HTTP <-> casos de uso. Solo conoce el
 * puerto de entrada; si mañana la entrada es gRPC o una cola, el dominio
 * y la aplicación no cambian.
 */
public final class TaskHttpAdapter {

    record CreateTaskRequest(String title) {
    }

    private final TaskUseCases useCases;

    public TaskHttpAdapter(TaskUseCases useCases) {
        this.useCases = useCases;
    }

    public void register(Ligero app) {
        app.group("/api/tasks", api -> {
            api.get("", ctx -> ctx.json(useCases.listTasks()));

            api.post("", ctx -> {
                CreateTaskRequest request = ctx.bodyValidator(CreateTaskRequest.class)
                    .check(r -> r.title() != null && !r.title().isBlank(), "title is required")
                    .get();
                ctx.status(201).json(useCases.createTask(request.title()));
            });

            api.post("/{id}/complete", ctx ->
                ctx.json(useCases.completeTask(ctx.pathParam("id"))));
        });
    }
}
