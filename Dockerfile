
FROM gradle:8.14-jdk17 AS build

WORKDIR /app

COPY build.gradle settings.gradle ./
COPY gradle gradle
COPY src src

RUN gradle clean bootJar --no-daemon -x test


FROM gcr.io/distroless/java17-debian12

WORKDIR /app

COPY --from=build /app/build/libs/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]