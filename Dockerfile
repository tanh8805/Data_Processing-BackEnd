FROM eclipse-temurin:17-jdk-alpine AS builder
LABEL authors="tuananh"

WORKDIR /app

COPY . .

RUN ./gradlew build -x test

FROM eclipse-temurin:17-jre-alpine as runner

WORKDIR /app

COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]