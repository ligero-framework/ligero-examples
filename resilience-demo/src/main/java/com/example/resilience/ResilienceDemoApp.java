package com.example.resilience;

import com.ligero.Ligero;
import com.ligero.resilience.CircuitBreaker;
import com.ligero.resilience.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Hardening a flaky "upstream" call: Retry with exponential backoff wrapped in a
 * CircuitBreaker. GET /call retries; GET /status shows the breaker state.
 */
public final class ResilienceDemoApp {

    /** A dependency that fails ~60% of the time. */
    private static String flakyUpstream() {
        if (ThreadLocalRandom.current().nextInt(10) < 6) {
            throw new RuntimeException("upstream failed");
        }
        return "ok";
    }

    public static void main(String[] args) throws Exception {
        Ligero app = Ligero.create(8080);
        CircuitBreaker breaker = new CircuitBreaker(3, Duration.ofSeconds(10));

        app.get("/call", ctx -> {
            String result = breaker.call(() ->
                Retry.of(3, Duration.ofMillis(50)).exponential().call(ResilienceDemoApp::flakyUpstream));
            ctx.json(Map.of("result", result));
        });

        app.get("/status", ctx -> ctx.json(Map.of("circuit", breaker.state().name())));

        app.start();
    }
}
