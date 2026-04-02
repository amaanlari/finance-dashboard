# Stage 1: Build the application using Maven
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set the working directory
WORKDIR /app

# Copy the Maven project files
COPY pom.xml .
COPY src ./src

# Build the project and create the executable JAR, skipping tests
RUN mvn clean package -DskipTests

# Stage 2: Create the final, smaller image for running the application
FROM eclipse-temurin:21.0.10_7-jdk-jammy

# Set the working directory
WORKDIR /app

# Copy the executable JAR from the build stage
COPY --from=build /app/target/finance-dashboard-0.0.1-SNAPSHOT.jar app.jar

# Expose the port the app runs on
EXPOSE 8080

# Command to run the application
ENTRYPOINT ["java", "-jar", "app.jar"]
