FROM maven:3.9-eclipse-temurin-21-alpine AS build
WORKDIR /app

COPY pom.xml .
COPY mvnw .
COPY .mvn .mvn
RUN mvn -B dependency:go-offline

COPY src src
RUN mvn -B clean package -DskipTests

RUN jlink \
    --add-modules java.base,java.compiler,java.logging,java.sql,java.naming,java.desktop,java.management,java.instrument,java.net.http,java.xml,java.security.jgss,jdk.crypto.ec,jdk.unsupported \
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --compress=2 \
    --output /custom-jre

FROM alpine:3.20
WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring -u 1001
COPY --from=build /custom-jre /opt/java/openjdk
COPY --from=build /app/target/*.jar app.jar

ENV JAVA_HOME=/opt/java/openjdk
ENV PATH="${JAVA_HOME}/bin:${PATH}"

USER spring
EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
