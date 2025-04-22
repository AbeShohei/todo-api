# todo-api/Dockerfile

# --- Build Stage ---
    FROM maven:3.9-eclipse-temurin-21 AS build
    WORKDIR /app
    COPY pom.xml .
    RUN mvn dependency:go-offline -B
    COPY src ./src
    RUN mvn package -DskipTests
    
    # --- Run Stage ---
    FROM eclipse-temurin:21-jre-jammy
    WORKDIR /app
    # ↓↓↓ JARファイル名は target/ 以下に生成される実際のファイル名に合わせる ↓↓↓
    COPY --from=build /app/target/todo-api-0.0.1-SNAPSHOT.jar app.jar
    # ポートは docker-compose.yml 側でマッピングするので EXPOSE は必須ではない
    # EXPOSE 8080
    ENTRYPOINT ["java", "-jar", "app.jar"]