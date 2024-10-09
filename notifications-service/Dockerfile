 ARG JAVA_VERSION
 FROM openjdk:${JAVA_VERSION}-slim AS builder

 WORKDIR /app
 COPY .mvn/ .mvn/
 COPY mvnw pom.xml ./
 COPY src ./src
 RUN ./mvnw clean package -DskipTests

 FROM openjdk:${JAVA_VERSION}-slim
 RUN apt-get update && apt-get install -y wget && rm -rf /var/lib/apt/lists/*
 WORKDIR /app
 COPY --from=builder /app/target/*.jar app.jar

 ARG APP_PORT
 ENV SERVER_PORT=$APP_PORT
 EXPOSE $APP_PORT

 ENTRYPOINT ["java", "-jar", "/app/app.jar"]