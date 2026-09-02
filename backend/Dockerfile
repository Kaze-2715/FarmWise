# syntax=docker/dockerfile:1

FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -DskipTests package \
    && cp target/*SNAPSHOT.jar /build/app.jar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/app.jar app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]