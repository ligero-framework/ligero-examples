# Framework comparison — results

Same products CRUD app (`GET/POST /products`, in-memory store) built in
each framework and launched identically (`installDist` -> start script,
no JVM tuning). Measured on this machine:

- openjdk version "21.0.10" 2026-01-20
- CPUs: 4, RAM: 16075 MB
- Startup = median of 4 cold JVM launches (process exec -> first HTTP 200).
- Throughput = `GET /products`, 50 connections, 8s, after warmup.
- RSS = resident memory (`/proc/<pid>/status` VmRSS) right after the load run.
- Date: 2026-07-07

| Framework | Startup (ms) | RSS (MB) | Throughput (req/s) | p50 (ms) | p99 (ms) | Dist libs (MB) |
|---|--:|--:|--:|--:|--:|--:|
| Ligero (JDK engine) | 429 | 371 | 13049 | 2.76 | 13.64 | 3 |
| Ligero (Jetty engine) | 592 | 262 | 10044 | 3.70 | 15.14 | 5 |
| Spring Boot (MVC/Tomcat) | 1952 | 309 | 5809 | 6.68 | 29.24 | 20 |
| Javalin (Jetty) | 624 | 273 | 10937 | 3.06 | 19.45 | 8 |

## Reading the numbers

After the TCP_NODELAY fix on the JDK engine, Ligero's **zero-dependency**
default engine is the fastest option here — a clean sweep:

- **Throughput:** Ligero (JDK engine) tops the table (~13k req/s), ahead of
  Javalin and Jetty-backed Ligero, and ~2.2× Spring Boot MVC.
- **Startup:** ~0.43 s — ~4.5× faster than Spring Boot.
- **Footprint:** 3 MB of dependency jars vs 20 MB for Spring Boot.
- The Jetty engine remains available (one dependency swap) for teams that
  want Jetty's HTTP/2, WebSockets or its tuning knobs.

> The earlier "JDK engine is modest under load" caveat is gone: it was
> `com.sun.net.httpserver` leaving Nagle's algorithm on (~40 ms per keep-alive
> response). The engine now sets `sun.net.httpserver.nodelay` by default.

## Verdict — which should you pick?

- **Best all-round** → **Ligero (JDK engine)**: fastest startup, smallest
  dependency tree, top throughput, and the framework features (DI, modules,
  devtools) micro-frameworks don't ship.
- **Need HTTP/2 or WebSockets** → **Ligero (Jetty engine)**: same app, one
  dependency swap.
- **Biggest ecosystem** → **Spring Boot** (not what this measures; slowest to
  start and heaviest here).

One line: *Ligero gives you a micro-framework's startup and footprint with
top-tier throughput on zero server dependencies — plus DI, modules and
devtools built in.*

> Reproduce: `cd comparison && ./run.sh` (needs Ligero in mavenLocal:
> `./gradlew publishToMavenLocal` in the framework repo).
