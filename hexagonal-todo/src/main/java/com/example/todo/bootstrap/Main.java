package com.example.todo.bootstrap;

import com.example.todo.adapter.in.http.TaskHttpAdapter;
import com.example.todo.adapter.out.persistence.InMemoryTaskRepository;
import com.example.todo.application.TaskService;
import com.example.todo.domain.port.TaskUseCases;
import com.ligero.Ligero;
import com.ligero.middleware.RequestLoggingMiddleware;

/**
 * Bootstrap (composition root hexagonal): el ÚNICO lugar que conoce todas
 * las piezas. Conecta adaptadores a puertos; cambiar la persistencia o el
 * transporte es cambiar una línea acá.
 */
public final class Main {

    public static void main(String[] args) throws Exception {
        long startNanos = System.nanoTime();

        Ligero app = create();
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        long millis = (System.nanoTime() - startNanos) / 1_000_000;
        System.out.println("hexagonal-todo started in " + millis + " ms -> http://localhost:" + app.port());
    }

    public static Ligero create() {
        TaskUseCases useCases = new TaskService(new InMemoryTaskRepository());

        Ligero app = Ligero.create(8081);
        app.use(new RequestLoggingMiddleware());
        new TaskHttpAdapter(useCases).register(app);
        return app;
    }
}
