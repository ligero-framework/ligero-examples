<p align="center">
  <img src="logo.svg" alt="Ligero Examples" width="360">
</p>

Complete example applications for the [Ligero framework](https://github.com/ligero-framework/ligero),
each showing a different architecture style. All of them print their startup
time (`... started in N ms`) so you can measure it yourself.

📖 **Docs:** [ligero-framework.github.io/ligero-docs](https://ligero-framework.github.io/ligero-docs/) — start with the [Learning Path](https://ligero-framework.github.io/ligero-docs/learning-path).

> **Setup:** until Ligero is on Maven Central, publish it locally first:
> `git clone https://github.com/ligero-framework/ligero && cd ligero && ./gradlew publishToMavenLocal`

| Example | Port | Shows |
|---|---|---|
| [`layered-api`](layered-api) | 8080 | **Spring-Boot-style layers**: `controller/` → `service/` → `repository/` + `domain/`. Constructor injection resolved in a composition root; swap `InMemoryProductRepository` ↔ `JdbcProductRepository` (PostgreSQL) with the `DB_URL` env var. Includes Docker Compose with PostgreSQL + seed data. |
| [`layered-beans`](layered-beans) | 8080 | **The same layered app, modular**: a `ProductsModule` (`LigeroModule`) declares the beans and routes of the feature — wiring lives in the module, never in `Application`. Uses the `Beans` container (fail-fast at startup) and mounts **devtools** at `/ligero/dev`: bean graph by stereotype + live per-request traces through the layers. |
| [`commerce-devtools`](commerce-devtools/) | 8080 | **Devtools on a complex graph**: an e-commerce backend — 3 modules (catalog, customers, orders), ~15 beans with cross-module dependencies and a deep `OrderService`. Run it and explore `/ligero/dev`: pick `POST /api/orders` and watch ~21 calls light up the path across services and repositories. |
| [`hexagonal-todo`](hexagonal-todo) | 8081 | **Hexagonal (ports & adapters)**: pure `domain/` (model + ports), `application/` use cases, `adapter/in/http` and `adapter/out/persistence`, wired in `bootstrap/Main`. |
| [`web-templates`](web-templates) | 8082 | **Server-side web app**: Pebble templates with layout inheritance, classic form POST → 303 redirect, static assets from the classpath. |
| [`jpa-todo`](jpa-todo) | 8080 | **JPA / Hibernate on H2**: a `Todo` entity, a `JpaTodoRepository` using the `ligero-jpa` helper (`jpa.tx(...)` / `jpa.read(...)`), layered service/controller wired in a `TodosModule`, full CRUD. Bring-your-own provider (Hibernate) + driver (H2). |
| [`config-profiles`](config-profiles) | 8080/8081 | **Config with YAML + profiles** (`ligero-config-yaml`): `server.port` from `ligero.yml`, app settings via `Config`, `${ENV:-default}` interpolation, and a `dev` overlay. Run `LIGERO_PROFILE=dev` to switch. |
| [`redis-scale`](redis-scale) | 8080 | **Distributed rate-limit + sessions** (`ligero-redis`): a Redis-backed rate limiter (5 / 10 s per IP) and Redis sessions shared across instances. `docker compose up` runs two app copies behind one Redis. |

## Run

```bash
./gradlew :layered-api:run        # or :hexagonal-todo:run / :web-templates:run
./gradlew test                    # e2e tests (ligero-test, ephemeral ports)
```

### layered-api with a real database (Docker)

```bash
cd layered-api
docker compose up --build         # app + PostgreSQL 16 (seeded via db/init.sql)
curl localhost:8080/api/products
```

The app logs `layered-api started in N ms` — compare in-memory vs PostgreSQL
profiles, or against any other framework, by reading that line.

## How dependency injection works here

Two styles, from simplest to recommended:

1. **Manual composition root** (`layered-api`, `hexagonal-todo`): constructor
   injection wired by hand in ~10 explicit lines the compiler verifies. Zero
   framework involvement — always an option.
2. **`Beans` container + modules** (`layered-beans`, the recommended style):
   each feature is a `LigeroModule` declaring its bindings (lambdas — still
   compiler-checked, zero reflection) and its routes. `Modules.install(...)`
   assembles one container from all modules, **fails fast at startup** if a
   binding is missing or cyclic, and exposes everything via `ctx.get(...)`.
   Stereotypes (`@Controller/@Service/@Repository`) are pure metadata that
   power the devtools dashboard.

Run `layered-beans` and open <http://localhost:8080/ligero/dev> to *see* the
dependency graph and watch each request traverse controller → service →
repository with arguments, results and timings.

## License

Apache 2.0
