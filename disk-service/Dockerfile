FROM eclipse-temurin:21-jdk AS build
WORKDIR /build

# собираем вместе с sorting-lib - disk-service подключает её через includeBuild('../sorting-lib')
COPY sorting-lib ./sorting-lib
COPY disk-service ./disk-service

WORKDIR /build/disk-service
RUN chmod +x gradlew
RUN ./gradlew --no-daemon clean bootJar

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/disk-service/build/libs/*.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java", "-jar", "app.jar"]
