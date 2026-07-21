package com.example.events;

import com.ligero.Ligero;
import com.ligero.events.Events;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-process events + lifecycle hooks: POST /users publishes a typed
 * UserCreated event; two subscribers (a mock mailer and an audit log) react.
 * GET /log shows what they recorded.
 */
public final class EventsDemoApp {

    public record UserCreated(String name) { }

    public static void main(String[] args) throws Exception {
        Ligero app = Ligero.create(8080);
        Events events = new Events();
        List<String> log = new CopyOnWriteArrayList<>();

        events.subscribe(UserCreated.class, e -> log.add("email sent to " + e.name()));
        events.subscribe(UserCreated.class, e -> log.add("audit: user '" + e.name() + "' created"));

        app.post("/users", ctx -> {
            String name = String.valueOf(ctx.body(Map.class).get("name"));
            events.publish(new UserCreated(name));
            ctx.status(201).json(Map.of("created", name));
        });

        app.get("/log", ctx -> ctx.json(Map.of("events", log)));

        app.onStart(() -> System.out.println("events-demo ready on :" + app.port()));
        app.start();
    }
}
