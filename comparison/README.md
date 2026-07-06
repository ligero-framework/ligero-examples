# Framework comparison

The **same** products CRUD app — `GET /products`, `GET /products/{id}`,
`POST /products` over an in-memory store — implemented in four setups and
measured identically, so the differences are the frameworks, not the app.

| Directory | Stack |
|---|---|
| [`ligero-app`](ligero-app) | Ligero on the default **JDK engine** (`com.sun.net.httpserver`, zero server deps) |
| [`ligero-jetty-app`](ligero-jetty-app) | The same Ligero code on the **Jetty engine** (one dependency swap) |
| [`springboot-app`](springboot-app) | Spring Boot 3.4, Web MVC on embedded Tomcat |
| [`javalin-app`](javalin-app) | Javalin 6 on Jetty |

`ligero-app` and `ligero-jetty-app` share byte-for-byte the same `App.java`
— only the `runtimeOnly` server dependency differs. That is the point of the
`ServerEngine` SPI: pick the engine without touching your code.

## Run it

```bash
# 1. publish Ligero locally (from the framework repo)
#    ./gradlew publishToMavenLocal
# 2. build + measure everything (writes results.md)
cd comparison
./run.sh
# knobs:
RUNS=7 CONC=100 DUR=15 ./run.sh
```

`run.sh` builds each app with `installDist`, launches the produced start
script (same JVM flags for all), and records:

- **Startup** — median of `RUNS` cold JVM launches, from process exec to the
  first `HTTP 200` on `/products`.
- **RSS** — resident memory (`/proc/<pid>/status`) right after the load run.
- **Throughput / p50 / p99** — from [`bench/Load.java`](bench/Load.java), a
  zero-dependency virtual-thread load driver, after a warmup.
- **Dist libs** — size of the app's dependency jars.

The measurement is deliberately simple and honest: one app at a time, warmup
before load, medians over cold starts. See [`results.md`](results.md) for a
sample run on a 4-core machine, and the docs
[Benchmarks page](https://github.com/ligero-framework/ligero-docs) for the
narrative. Absolute numbers depend on your hardware — reproduce them locally.
