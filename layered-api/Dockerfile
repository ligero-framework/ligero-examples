FROM gradle:8.14-jdk21 AS build
WORKDIR /workspace
COPY .. .
RUN gradle :layered-api:installDist --no-daemon

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /workspace/layered-api/build/install/layered-api/ .
EXPOSE 8080
USER 1000
ENTRYPOINT ["bin/layered-api"]
