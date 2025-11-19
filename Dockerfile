# ---------- STAGE 1: Build ----------
FROM maven:3.9.6-eclipse-temurin-25 AS build
WORKDIR /app

# Кэш зависимостей
COPY pom.xml .
RUN mvn -B dependency:go-offline

# Копируем проект и билдим
COPY src ./src
RUN mvn -B -DskipTests package

# ---------- STAGE 2: Runtime ----------
FROM eclipse-temurin:25-jre

WORKDIR /app

# Копируем JAR из предыдущего этапа
COPY --from=build /app/target/*.jar app.jar

# Оптимизация: включить контейнерный режим JVM
ENV JAVA_OPTS="-XX:+UseContainerSupport"

EXPOSE 8080

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]