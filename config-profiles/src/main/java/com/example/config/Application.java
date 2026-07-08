package com.example.config;

import com.ligero.Ligero;
import com.ligero.config.Config;
import com.ligero.middleware.RequestLoggingMiddleware;

import java.util.Map;

/**
 * Config with YAML + profiles (ligero-config-yaml).
 *
 * <p>Framework settings (server.port, server.gzip) are read from ligero.yml
 * automatically. Application settings are read through {@link Config}. Run
 * with {@code LIGERO_PROFILE=dev} to overlay ligero-dev.yml (port 8081, a
 * different greeting and an H2 URL).</p>
 *
 * <pre>{@code
 * gradle run                       # base profile  -> :8080
 * LIGERO_PROFILE=dev gradle run    # dev overlay    -> :8081
 * }</pre>
 */
public final class Application {

    public static void main(String[] args) throws Exception {
        Config config = Config.load();

        Ligero app = create(config);
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));

        System.out.println("profile   : " + config.profile().orElse("(none)"));
        System.out.println("app.name  : " + config.get("app.name", "?"));
        System.out.println("db.url    : " + config.get("db.url", "?"));
        System.out.println("running   : http://localhost:" + app.port());
    }

    public static Ligero create(Config config) {
        Ligero app = Ligero.create();   // server.port comes from ligero.yml (+ profile)
        app.use(new RequestLoggingMiddleware());

        // Expose the resolved config so you can see the profile take effect.
        app.get("/config", ctx -> ctx.json(Map.of(
            "profile", config.profile().orElse("(none)"),
            "greeting", config.get("app.greeting", ""),
            "dbUrl", config.get("db.url", ""),
            "dbPool", config.getInt("db.pool", 0))));

        app.get("/hello", ctx -> ctx.text(config.get("app.greeting", "hi")));
        return app;
    }
}
