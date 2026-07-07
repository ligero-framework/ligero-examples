package com.example.jpatodo;

import com.ligero.Ligero;
import com.ligero.Modules;
import com.ligero.beans.Beans;
import com.ligero.devtools.Devtools;
import com.ligero.middleware.RequestLoggingMiddleware;

/**
 * Todo API backed by JPA/Hibernate on H2, wired with the Beans container and a
 * feature module. Devtools is mounted so you can watch each request traverse
 * controller -> service -> repository and into the entity manager.
 */
public final class Application {

    public static void main(String[] args) throws Exception {
        long startNanos = System.nanoTime();

        Ligero app = create();
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        long millis = (System.nanoTime() - startNanos) / 1_000_000;
        System.out.println("jpa-todo started in " + millis + " ms -> http://localhost:" + app.port());
        System.out.println("devtools -> http://localhost:" + app.port() + "/ligero/dev");
    }

    public static Ligero create() {
        Ligero app = Ligero.create(8080);
        app.use(new RequestLoggingMiddleware());

        Devtools devtools = Devtools.create();
        Beans beans = Modules.install(app, devtools.recorder(), new TodosModule());
        devtools.install(app, beans);

        // close the JPA factory (and any other AutoCloseable bean) on shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(beans::close));
        return app;
    }
}
