# --- Этап сборки: собираем jar внутри контейнера, чтобы не зависеть от локальной среды ---
FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

# Сначала копируем только то, что нужно для загрузки зависимостей (кэш слоёв Docker)
COPY gradlew settings.gradle build.gradle ./
COPY gradle ./gradle
RUN chmod +x gradlew && ./gradlew --no-daemon dependencies > /dev/null 2>&1 || true

COPY src ./src
RUN ./gradlew --no-daemon clean bootJar

# --- Этап запуска: только JRE и готовый jar ---
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
