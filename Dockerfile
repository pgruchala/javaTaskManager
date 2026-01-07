# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY . .
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre

WORKDIR /app

# Copy the JAR from builder
COPY --from=builder /app/target/javaTaskManager-1.0-SNAPSHOT.jar app.jar
COPY --from=builder /app/opentelemetry-javaagent.jar javaagent.jar

# Create uploads directory
RUN mkdir -p /app/uploads

# Expose port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-javaagent:./javaagent.jar","-jar", "app.jar"]
