# auth-demo — RS256 JWT

Generates an RSA key pair on startup, issues signed tokens and verifies them
with the public key (how a resource server validates an IdP's tokens).

## Run
```bash
./gradlew run
```

## Try it
```bash
TOKEN=$(curl -s -X POST localhost:8080/login | sed 's/.*"token":"//;s/".*//')
curl -s localhost:8080/protected -H "Authorization: Bearer $TOKEN"
# {"sub":"ada","roles":["admin"]}
curl -i localhost:8080/protected      # 401 without a token
```
Docs: https://doc.ligeroframework.com/guides/auth-asymmetric
