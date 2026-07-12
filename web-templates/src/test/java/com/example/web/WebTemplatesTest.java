package com.example.web;

import com.ligero.middleware.StaticFilesMiddleware;
import com.ligero.test.LigeroTest;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Renders the Pebble guestbook (layout inheritance, autoescape) and exercises
 * the classic POST + redirect form flow, on an ephemeral port.
 */
class WebTemplatesTest {

    @Test
    void rendersGuestbookAndSignsViaPostRedirect() {
        List<WebApp.Entry> entries = new CopyOnWriteArrayList<>();

        try (LigeroTest test = LigeroTest.create(app -> {
            app.use(StaticFilesMiddleware.classpath("/static", "static"));
            app.get("/", ctx -> ctx.render("guestbook", Map.of("entries", entries)));
            app.post("/sign", ctx -> {
                String name = ctx.formParam("name");
                String message = ctx.formParam("message");
                if (name != null && !name.isBlank() && message != null && !message.isBlank()) {
                    entries.add(new WebApp.Entry(name.strip(), message.strip()));
                }
                ctx.redirect("/", 303);
            });
        })) {
            // the layout + empty-state block render
            LigeroTest.TestResponse home = test.get("/").execute();
            assertEquals(200, home.status());
            assertTrue(home.body().contains("Libro de visitas"));
            assertTrue(home.body().contains("Sin firmas"));

            // POST /sign -> 303 See Other back to /
            LigeroTest.TestResponse signed = test.post("/sign")
                .header("Content-Type", "application/x-www-form-urlencoded")
                .body("name=Ada&message=Hola")
                .execute();
            assertEquals(303, signed.status());
            assertEquals("/", signed.header("Location"));

            // the new entry now shows on the page (HTML-escaped by Pebble)
            assertTrue(test.get("/").execute().body().contains("Ada"));
        }
    }
}
