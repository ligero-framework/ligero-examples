# events-demo — application events + lifecycle

Publishing a typed `UserCreated` event fans out to two subscribers (mailer,
audit). `app.onStart` runs a startup hook.

## Run
```bash
./gradlew run
```

## Try it
```bash
curl -X POST localhost:8080/users -H 'Content-Type: application/json' -d '{"name":"Ada"}'
curl localhost:8080/log
# {"events":["email sent to Ada","audit: user 'Ada' created"]}
```
Docs: https://doc.ligeroframework.com/guides/events
