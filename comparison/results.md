# Framework comparison — results

Same products CRUD app (`GET/POST /products`, in-memory store) built in
each framework and launched identically (`installDist` -> start script,
no JVM tuning). Measured on this machine:

- openjdk version "21.0.10" 2026-01-20
- CPUs: 4, RAM: 16075 MB
- Startup = median of 5 cold JVM launches (process exec -> first HTTP 200).
- Throughput = `GET /products`, 50 connections, 8s, after warmup.
- RSS = resident memory (`/proc/<pid>/status` VmRSS) right after the load run.
- Date: 2026-07-06

| Framework | Startup (ms) | RSS (MB) | Throughput (req/s) | p50 (ms) | p99 (ms) | Dist libs (MB) |
|---|--:|--:|--:|--:|--:|--:|
| Ligero (JDK engine) | 436 | 246 | 1030 | 44.08 | 58.92 | 3 |
| Ligero (Jetty engine) | 615 | 256 | 9669 | 3.74 | 15.96 | 5 |
| Spring Boot (MVC/Tomcat) | 2143 | 324 | 5590 | 6.77 | 28.16 | 20 |
| Javalin (Jetty) | 679 | 328 | 11907 | 2.67 | 15.79 | 8 |

## Reading the numbers

- **Startup & footprint:** Ligero starts fastest (≈0.4 s on the JDK engine,
  ≈5× faster than Spring Boot) and ships the smallest dependency tree (3 MB of
  libs vs 20 MB for Spring Boot).
- **Throughput — pick your engine:** on the default **zero-dependency JDK
  engine** (`com.sun.net.httpserver`) throughput is modest — that server caps
  concurrency and becomes the bottleneck under 50 connections. Fine for
  internal APIs and low-to-medium traffic, where instant startup and a tiny
  footprint win. For high throughput, switch to the **Jetty engine** — a
  *one-line dependency change, no code change* (that is the `ServerEngine`
  SPI) — and Ligero reaches ~9.7k req/s at a p99 of ~16 ms, right alongside
  Javalin and above Spring Boot MVC, while still starting faster and lighter.
- Honest by construction: identical app, identical launch, five cold starts,
  warmup before load. Numbers vary run to run and machine to machine —
  reproduce them yourself.

> Reproduce: `cd comparison && ./run.sh` (needs Ligero in mavenLocal:
> `./gradlew publishToMavenLocal` in the framework repo).
