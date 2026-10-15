# --- Build-Phase: kompiliert die App und baut das ausfuehrbare JAR ---
FROM eclipse-temurin:25-jdk AS build
WORKDIR /app
COPY . .
RUN ./gradlew bootJar --no-daemon

# --- Lauf-Phase: schlankes finales Image nur mit JRE und dem JAR ---
FROM eclipse-temurin:25-jre
WORKDIR /app
COPY --from=build /app/build/libs/*.jar app.jar
ENTRYPOINT ["sh", "-c", "SERVER_PORT=${PORT:-8080} java -jar /app/app.jar"]