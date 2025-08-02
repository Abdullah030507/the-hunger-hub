# Use Maven with JDK 18 for the build stage
FROM maven:3.9.6-openjdk-18 AS build

# Set working directory for build
WORKDIR /build

# Copy entire project into the container
COPY . .

# Move into your project directory where pom.xml is located
WORKDIR /build/bhajiDishStore

# Package the app (skip tests)
RUN mvn clean package -DskipTests

# Use lightweight JDK 18 image to run the app
FROM eclipse-temurin:18-jdk-alpine

# Set working directory for runtime
WORKDIR /app

# Copy the jar file from the build stage
COPY --from=build /build/bhajiDishStore/target/*.jar app.jar

# Command to run the app
CMD ["java", "-jar", "app.jar"]
