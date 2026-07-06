# Ligero Examples

Complete example applications for the [Ligero framework](https://github.com/ligero-framework/ligero),
each showing a different architecture style. All of them print their startup
time (`... started in N ms`) so you can measure it yourself.

> **Setup:** until Ligero is on Maven Central, publish it locally first:
> `git clone https://github.com/ligero-framework/ligero && cd ligero && ./gradlew publishToMavenLocal`

| Example | Port | Shows |
|---|---|---|
| [`layered-api`](layered-api) | 8080 | **Spring-Boot-style layers**: `controller/` → `service/` → `repository/` + `domain/`. Constructor injection resolved in a composition root; swap `InMemoryProductRepository` ↔ `JdbcProductRepository` (PostgreSQL) with the `DB_URL` env var. Includes Docker Compose with PostgreSQL + seed data. |
| [`hexagonal-todo`](hexagonal-todo) | 8081 | **Hexagonal (ports & adapters)**: pure `domain/` (model + ports), `application/` use cases, `adapter/in/http` and `adapter/out/persistence`, wired in `bootstrap/Main`. |
| [`web-templates`](web-templates) | 8082 | **Server-side web app**: Pebble templates with layout inheritance, classic form POST → 303 redirect, static assets from the classpath. |

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

Ligero has **no DI container by design**. Dependencies are:

1. **Constructor injection, wired in a composition root** (`Application.create()` /
   `bootstrap/Main`): the object graph is ~10 explicit lines the compiler verifies.
2. **`app.register(Type.class, impl)` / `ctx.get(Type.class)`** for services that
   middleware or standalone handlers need at request time.

Both are shown in `layered-api`; the hexagonal example shows how ports keep the
domain free of any framework import.

## License

Apache 2.0
