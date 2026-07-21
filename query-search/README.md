# query-search — the HTTP QUERY method

A read driven by a structured filter in the request body, using `app.query(...)`.

## Run

```bash
./gradlew run          # http://localhost:8080
```

## Try it

```bash
# QUERY carries a JSON body, but stays a safe/idempotent read
curl -X QUERY localhost:8080/search \
  -H 'Content-Type: application/json' \
  -d '{"text":"laptop","maxPrice":1000}'
# {"count":1,"results":[{"name":"Office Laptop","category":"computers","price":899}]}

curl -X QUERY localhost:8080/search \
  -H 'Content-Type: application/json' \
  -d '{"category":"accessories"}'
```

Docs: https://doc.ligeroframework.com/guides/query
