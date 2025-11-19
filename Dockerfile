# ---------- STAGE 1: Build ----------
FROM 4.0.0-rc-5-amazoncorretto-25-debian-trixie AS build
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