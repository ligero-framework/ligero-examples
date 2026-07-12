package com.example.redis;

import com.ligero.test.LigeroTest;

import org.junit.jupiter.api.Test;

import redis.clients.jedis.JedisPool;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

/**
 * Exercises the Redis-backed rate limiter (5 requests / 10 s per IP). Requires a
 * reachable Redis — the test is skipped when none is available (CI provides one
 * as a service). The rate limit is keyed by IP, so it is observable without
 * cookie handling.
 */
class RedisScaleTest {

    private static final String HOST = System.getenv().getOrDefault("REDIS_HOST", "localhost");
    private static final int PORT =
        Integer.parseInt(System.getenv().getOrDefault("REDIS_PORT", "6379"));

    private static boolean redisReachable() {
        try (JedisPool pool = new JedisPool(HOST, PORT); var jedis = pool.getResource()) {
            return "PONG".equalsIgnoreCase(jedis.ping());
        } catch (Exception e) {
            return false;
        }
    }

    @Test
    void rateLimitKicksInAfterFiveRequests() {
        assumeTrue(redisReachable(), "Redis not available — skipping");

        try (JedisPool pool = new JedisPool(HOST, PORT);
             LigeroTest test = LigeroTest.start(Application.create(pool))) {

            assertEquals(200, test.get("/").execute().status());

            // hammer past the 5 / 10 s window; at least one request must be limited
            boolean sawLimit = false;
            for (int i = 0; i < 8; i++) {
                if (test.get("/visits").execute().status() == 429) {
                    sawLimit = true;
                }
            }
            assertTrue(sawLimit, "expected a 429 once the rate limit was exceeded");
        }
    }
}
