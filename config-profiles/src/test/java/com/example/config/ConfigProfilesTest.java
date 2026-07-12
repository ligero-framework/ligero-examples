package com.example.config;

import com.ligero.config.Config;
import com.ligero.test.LigeroTest;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * The base profile of ligero.yml is read through {@link Config} (via
 * ligero-config-yaml) and exposed on the routes.
 */
class ConfigProfilesTest {

    @Test
    void baseProfileValuesAreReadFromYaml() {
        Config config = Config.load(); // no LIGERO_PROFILE -> base profile

        try (LigeroTest test = LigeroTest.create(app -> {
            app.get("/hello", ctx -> ctx.text(config.get("app.greeting", "hi")));
            app.get("/config", ctx -> ctx.json(Map.of(
                "name", config.get("app.name", ""),
                "pool", config.getInt("db.pool", 0))));
        })) {
            assertEquals("Hello from the base profile", test.get("/hello").execute().body());

            String cfg = test.get("/config").execute().body();
            assertTrue(cfg.contains("config-demo"), cfg);
            assertTrue(cfg.contains("10"), cfg); // db.pool from ligero.yml
        }
    }
}
