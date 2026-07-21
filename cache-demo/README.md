# cache-demo — load-through caching

`InMemoryCache` with a 30s TTL. The `X-Cache` header shows HIT vs MISS and
`dbHits` proves the loader only runs on a miss.

## Run
```bash
./gradlew run
```

## Try it
```bash
curl -i localhost:8080/users/7     # X-Cache: MISS, dbHits: 1
curl -i localhost:8080/users/7     # X-Cache: HIT,  dbHits: 1
curl -X DELETE localhost:8080/cache/7
curl -i localhost:8080/users/7     # X-Cache: MISS, dbHits: 2
```
Docs: https://doc.ligeroframework.com/guides/cache
