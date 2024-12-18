FROM gradle:8.10-jdk17 AS build
WORKDIR /
COPY build.gradle settings.gradle ./
COPY src ./src
RUN gradle bootJar --build-cache

FROM openjdk:17-jdk-slim

WORKDIR /app
ARG JAR_FILENAME=cloud-keeper.jar
ENV FILENAME=${JAR_FILENAME}
COPY --from=build /build/libs/${JAR_FILENAME} .
EXPOSE 8080
CMD java -jar ${FILENAME}