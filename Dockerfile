 # Build stage
 ARG JAVA_VERSION
 FROM openjdk:${JAVA_VERSION}-jdk-slim AS builder

 # Set the working directory
 WORKDIR /app

 # Copy the Maven wrapper and pom.xml file
 COPY .mvn/ .mvn/
 COPY mvnw pom.xml ./

 # Copy the source code
 COPY src ./src

 # Build the application
 RUN ./mvnw clean package -DskipTests

 # Runtime stage
 FROM openjdk:${JAVA_VERSION}-jdk-slim

 # Install wget for healthcheck
 RUN apt-get update && \
     apt-get install -y wget && \
     rm -rf /var/lib/apt/lists/*

 WORKDIR /app

 # Copy the jar file from the builder stage
 COPY --from=builder /app/target/*.jar app.jar

 # Set the port as an environment variable
 ARG APP_PORT
 ENV PORT=$APP_PORT

 # Expose the port
 EXPOSE $PORT

 # Run the jar file
 ENTRYPOINT ["java", "-jar", "/app/app.jar"]