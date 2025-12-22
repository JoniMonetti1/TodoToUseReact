# build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /app

#copying pom to leverage cache for dependencies
COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline

#copy src and build
COPY src ./src
RUN mvn -q -DskipTests package

#run stage
FROM eclipse-temurin:21-jre
WORKDIR /app

#create non-root user and group
RUN groupadd -r app && useradd -r -g app -d /app -s /sbin/nologin app

#copy the build jar from the build stage
COPY --from=build --chown=app:app /app/target/app.jar app.jar

EXPOSE 8080

#basic jvm setting for containers
ENV JAVA_TOOL_OPTIONS="-XX:MaxRAMPercentage=75.0 -XX:+UseContainerSupport"

USER app

ENTRYPOINT ["java", "-jar",  "app.jar"]