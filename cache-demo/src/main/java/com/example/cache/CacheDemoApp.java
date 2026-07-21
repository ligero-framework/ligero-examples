package com.example.cache;

import com.ligero.Ligero;
import com.ligero.cache.Cache;
import com.ligero.cache.InMemoryCache;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Load-through caching with InMemoryCache: GET /users/{id} serves from a 30s
 * cache and reports whether it was a hit or a miss (X-Cache header). The
 * "database" counter shows the loader only runs on a miss.
 */
public final class CacheDemoApp {

    public static void main(String[] args) throws Exception {
        Ligero app = Ligero.create(8080);
        Cache<String, String> cache = new InMemoryCache<>();
        AtomicInteger dbHits = new AtomicInteger();

        app.get("/users/{id}", ctx -> {
            String id = ctx.pathParam("id");
            boolean[] miss = {false};
            String name = cache.get(id, Duration.ofSeconds(30), key -> {
                miss[0] = true;
                dbHits.incrementAndGet();
                return "User " + key;               // pretend this hit the database
            });
            ctx.header("X-Cache", miss[0] ? "MISS" : "HIT")
               .json(Map.of("id", id, "name", name, "dbHits", dbHits.get()));
        });

        app.delete("/cache/{id}", ctx -> {
            cache.evict(ctx.pathParam("id"));
            ctx.status(204).res().end();
        });

        app.start();
    }
}
