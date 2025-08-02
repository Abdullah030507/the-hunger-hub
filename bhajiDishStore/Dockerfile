# Start from a Maven image with JDK 17
FROM maven:3.9.6-eclipse-temurin-17 AS build

# Set working directory
WORKDIR /build

# Copy source code into container
COPY . .

# Move into your project directory (where pom.xml is)
WORKDIR /build/bhajiDishStore

# Package the app
RUN mvn clean package -DskipTests

# Use lightweight JRE image to run the app
FROM eclipse-temurin:17-jre-alpine

# Set working directory in final image
WORKDIR /app

# Copy JAR from build stage
COPY --from=build /build/bhajiDishStore/target/*.jar app.jar

# Run the jar
CMD ["java", "-jar", "app.jar"]
