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
    private static final int MAX_ENTRIES = 200;
    private static final int MAX_NAME = 60;
    private static final int MAX_MESSAGE = 500;

    private static String truncate(String value, int max) {
        return value.length() <= max ? value : value.substring(0, max);
    }

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
                // Bound what anonymous visitors can store: field lengths and total entries.
                ENTRIES.add(new Entry(truncate(name.strip(), MAX_NAME), truncate(message.strip(), MAX_MESSAGE)));
                while (ENTRIES.size() > MAX_ENTRIES) {
                    ENTRIES.remove(0);
                }
            }
            ctx.redirect("/", 303); // Post/Redirect/Get
        });

        return app;
    }
}
