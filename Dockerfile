# ------------ Build Stage ------------
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory for the build
WORKDIR /app

# Copy all project files
COPY . .

# Build the project and skip tests
RUN mvn clean package -DskipTests

# ------------ Run Stage ------------
FROM eclipse-temurin:17-jdk-alpine

# Set working directory for running the app
WORKDIR /app

# Copy built JAR from build stage
COPY --from=build /app/target/*.jar app.jar

# Expose port (change 8080 if your app uses a different port)
EXPOSE 8080

# Run the application
CMD ["java", "-jar", "app.jar"]
