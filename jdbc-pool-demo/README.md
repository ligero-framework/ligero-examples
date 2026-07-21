# jdbc-pool-demo — HikariCP connection pool

`DataSources.pooled(...)` builds a HikariCP-backed DataSource driving a small
Task CRUD; `/pool-stats` exposes the live pool gauges.

## Run
```bash
./gradlew run
```

## Try it
```bash
curl localhost:8080/tasks
curl -X POST localhost:8080/tasks -H 'Content-Type: application/json' -d '{"title":"Ship 0.7.0"}'
curl localhost:8080/pool-stats     # {"active":0,"idle":1,"total":1}
```
Docs: https://doc.ligeroframework.com/guides/jdbc-pooling
