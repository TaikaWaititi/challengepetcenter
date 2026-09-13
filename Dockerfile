FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw && ./mvnw -B dependency:go-offline

COPY src src
RUN ./mvnw -B clean package -DskipTests

FROM eclipse-temurin:21-jdk-alpine AS jre
RUN jlink \
    --add-modules java.base,java.compiler,java.desktop,java.instrument,java.logging,java.management,java.naming,java.net.http,java.security.jgss,java.security.sasl,java.sql,java.transaction.xa,java.xml,jdk.crypto.ec,jdk.unsupported \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /opt/petcenter-jre

FROM alpine:3.20
WORKDIR /app

RUN apk add --no-cache netcat-openbsd \
    && addgroup -S spring \
    && adduser -S spring -G spring -u 1001
COPY --from=jre /opt/petcenter-jre /opt/petcenter-jre
COPY --from=build /app/target/*.jar app.jar
COPY scripts/docker-entrypoint.sh /app/docker-entrypoint.sh
RUN sed -i 's/\r$//' /app/docker-entrypoint.sh \
    && chmod +x /app/docker-entrypoint.sh

ENV PATH="/opt/petcenter-jre/bin:${PATH}"
USER spring
EXPOSE 8080

ENTRYPOINT ["/app/docker-entrypoint.sh"]
