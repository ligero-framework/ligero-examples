# scheduler-demo — background jobs

`ligero-scheduler`: a repeating tick, a one-off warm-up and a daily task, each on
its own virtual thread. Closed on shutdown via `app.onStop`.

## Run
```bash
./gradlew run           # watch the console for "tick N"; http://localhost:8080
```

## Try it
```bash
curl localhost:8080/ticks    # {"ticks":3}
```
Docs: https://doc.ligeroframework.com/guides/scheduler
