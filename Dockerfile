# Use an official Maven image to build the application
FROM maven:3.8.4-openjdk-17 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code to the working directory
COPY pom.xml .
COPY src ./src

# Build the project
RUN mvn clean package -DskipTests

# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/vulnerable-springboot-app.jar /app/vulnerable-springboot-app.jar

# Expose port 6000 to the outside
EXPOSE 6000

# Run the application
ENTRYPOINT ["java", "-jar", "/app/vulnerable-springboot-app.jar"]
