package com.example.redis;

import com.ligero.Ligero;
import com.ligero.auth.Session;
import com.ligero.auth.SessionMiddleware;
import com.ligero.middleware.RateLimitMiddleware;
import com.ligero.middleware.RequestLoggingMiddleware;
import com.ligero.redis.RedisRateLimiterStore;
import com.ligero.redis.RedisSessionStore;

import redis.clients.jedis.JedisPool;

import java.time.Duration;
import java.util.Map;

/**
 * Rate limiting and sessions backed by Redis, so limits and sessions are
 * shared across every instance behind a load balancer.
 *
 * <p>Run several copies (ports 8080, 8081, …) against the same Redis and see
 * the rate limit and the session apply across all of them. Needs a Redis —
 * {@code docker compose up} starts one alongside the app.</p>
 */
public final class Application {

    public static void main(String[] args) throws Exception {
        String redisHost = System.getenv().getOrDefault("REDIS_HOST", "localhost");
        int redisPort = Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));
        JedisPool pool = new JedisPool(redisHost, redisPort);

        Ligero app = create(pool);
        app.start();
        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
        Runtime.getRuntime().addShutdownHook(new Thread(pool::close));
        System.out.println("redis-scale running -> http://localhost:" + app.port() + " (redis " + redisHost + ":" + redisPort + ")");
    }

    public static Ligero create(JedisPool pool) {
        Ligero app = Ligero.create(8080);
        app.use(new RequestLoggingMiddleware());

        // Cluster-wide: max 5 requests per 10 seconds per client IP.
        app.use(RateLimitMiddleware.of(
            RedisRateLimiterStore.usingJedis(pool, 5, Duration.ofSeconds(10)),
            ctx -> ctx.remoteAddress()));

        // Cluster-wide sessions (shared across instances, survive restarts).
        String secret = System.getenv().getOrDefault("SESSION_SECRET", "change-me-change-me-change-me-32b");
        app.use(SessionMiddleware.of(secret, RedisSessionStore.usingJedis(pool, Duration.ofHours(1))));

        // A visit counter kept in the session — proves sessions are shared.
        app.get("/visits", ctx -> {
            Session session = ctx.attribute(SessionMiddleware.ATTRIBUTE);
            int visits = session.get("visits") == null ? 0 : Integer.parseInt(session.get("visits"));
            session.set("visits", String.valueOf(++visits));
            ctx.json(Map.of("visits", visits));
        });

        app.get("/", ctx -> ctx.text("try /visits (session) and hammer it to hit the rate limit (5 / 10s)"));
        return app;
    }
}
