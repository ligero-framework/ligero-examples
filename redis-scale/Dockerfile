FROM gradle:8.14-jdk21 AS build
WORKDIR /workspace
COPY .. .
RUN gradle :redis-scale:installDist --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/redis-scale/build/install/redis-scale/ .
EXPOSE 8080
USER 1000
ENTRYPOINT ["bin/redis-scale"]
