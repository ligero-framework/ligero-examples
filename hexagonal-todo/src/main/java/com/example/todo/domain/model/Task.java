package com.example.todo.domain.model;

/** Núcleo del dominio: sin frameworks, sin anotaciones, solo reglas. */
public record Task(String id, String title, boolean done, long createdAtEpochMs) {

    public Task {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title must not be blank");
        }
    }

    public Task complete() {
        return new Task(id, title, true, createdAtEpochMs);
    }
}
