# resilience-demo — retry, timeout, circuit breaker

A flaky upstream call wrapped in `Retry.exponential()` inside a `CircuitBreaker`.

## Run
```bash
./gradlew run
```

## Try it
```bash
curl localhost:8080/call       # retries the flaky call; usually {"result":"ok"}
curl localhost:8080/status     # {"circuit":"CLOSED"} — or OPEN after repeated failures
```
Docs: https://doc.ligeroframework.com/guides/resilience
