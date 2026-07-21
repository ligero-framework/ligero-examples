package com.example.auth;

import com.ligero.Ligero;
import com.ligero.auth.Jwt;
import com.ligero.http.UnauthorizedException;
import com.ligero.json.JacksonBodyMapper;
import com.ligero.spi.BodyMapper;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.time.Duration;
import java.util.List;
import java.util.Map;

/**
 * RS256 JWTs: the app generates an RSA key pair on startup, issues signed tokens
 * at POST /login, and verifies them at GET /protected with the public key
 * (exactly how a resource server validates an IdP's tokens).
 */
public final class AuthDemoApp {

    public static void main(String[] args) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair keys = generator.generateKeyPair();

        BodyMapper mapper = new JacksonBodyMapper();
        Jwt jwt = Jwt.rs256(keys.getPrivate(), keys.getPublic(), mapper);

        Ligero app = Ligero.create(8080);

        app.post("/login", ctx -> {
            String token = jwt.sign(
                Map.of("sub", "ada", "roles", List.of("admin")), Duration.ofHours(1));
            ctx.json(Map.of("token", token));
        });

        app.get("/protected", ctx -> {
            String header = ctx.header("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                throw new UnauthorizedException("Missing bearer token");
            }
            Map<String, Object> claims = jwt.verify(header.substring("Bearer ".length()));
            ctx.json(Map.of("sub", claims.get("sub"), "roles", claims.get("roles")));
        });

        app.start();
    }
}
