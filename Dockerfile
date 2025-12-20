# ---------- STAGE 1: Build ----------
FROM maven:4.0.0-rc-5-amazoncorretto-25-debian-trixie AS build

WORKDIR /app

# Кэш зависимостей (максимально эффективно)
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# Копируем исходники и собираем JAR
COPY src ./src
RUN mvn -B -q -DskipTests package

# ---------- STAGE 2: Runtime ----------
FROM eclipse-temurin:25-jre

WORKDIR /app

# Копируем fat jar
COPY --from=build /app/target/*.jar app.jar

# JVM-настройки для Kubernetes
ENV JAVA_OPTS="\
-XX:+UseContainerSupport \
-XX:MaxRAMPercentage=75.0 \
-XX:InitialRAMPercentage=25.0 \
-XX:+ExitOnOutOfMemoryError \
-Djava.security.egd=file:/dev/urandom \
"

# Spring Boot best-practices
ENV SPRING_PROFILES_ACTIVE=prod

EXPOSE 8080

# Корректный PID 1 (важно для Kubernetes)
ENTRYPOINT ["sh", "-c", "exec java $JAVA_OPTS -jar /app/app.jar"]