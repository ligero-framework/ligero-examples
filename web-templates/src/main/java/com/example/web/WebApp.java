package com.example.web;

import com.ligero.Ligero;
import com.ligero.middleware.RequestLoggingMiddleware;
import com.ligero.middleware.StaticFilesMiddleware;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * App web server-side: templates Pebble (herencia de layout, autoescape),
 * formulario clásico (POST + redirect) y assets estáticos del classpath.
 */
public final class WebApp {

    public record Entry(String name, String message) {
    }

    private static final List<Entry> ENTRIES = new CopyOnWriteArrayList<>();

    public static void main(String[] args) throws Exception {
        long startNanos = System.nanoTime();

        Ligero app = create();
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        long millis = (System.nanoTime() - startNanos) / 1_000_000;
        System.out.println("web-templates started in " + millis + " ms -> http://localhost:" + app.port());
    }

    public static Ligero create() {
        Ligero app = Ligero.create(8082);
        app.use(new RequestLoggingMiddleware());
        app.use(StaticFilesMiddleware.classpath("/static", "static"));

        app.get("/", ctx -> ctx.render("guestbook", Map.of("entries", ENTRIES)));

        app.post("/sign", ctx -> {
            String name = ctx.formParam("name");
            String message = ctx.formParam("message");
            if (name != null && !name.isBlank() && message != null && !message.isBlank()) {
                ENTRIES.add(new Entry(name.strip(), message.strip()));
            }
            ctx.redirect("/", 303); // Post/Redirect/Get
        });

        return app;
    }
}
