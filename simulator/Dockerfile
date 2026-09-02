FROM maven:3.9-eclipse-temurin-21 AS build

WORKDIR /build

COPY pom.xml .
COPY src ./src

RUN mvn -B -DskipTests package \
    && cp target/farmwise-device-simulator-0.0.1-SNAPSHOT.jar /build/app.jar

FROM eclipse-temurin:21-jre

WORKDIR /app

COPY --from=build /build/app.jar app.jar
COPY devices.json devices.json

ENTRYPOINT ["java", "-jar", "/app/app.jar", "/app/devices.json"]
