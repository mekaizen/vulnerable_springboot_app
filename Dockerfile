# Use an official Maven image to build the application
FROM maven:3.8.4-openjdk-11 AS build

# Set the working directory inside the container
WORKDIR /app

# Copy the pom.xml and source code to the working directory
COPY pom.xml .
COPY src ./src

# Build the project
#RUN mvn clean package -DskipTests

# Build the project with added compiler flags for Java 17 compatibility
RUN mvn clean package -DskipTests


# Use an official OpenJDK runtime as a parent image
FROM openjdk:11-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built jar file from the build stage
COPY --from=build /app/target/vulnerable-springboot-app-0.0.1-SNAPSHOT.jar /app/vulnerable-springboot-app.jar

# Expose port 6000 to the outside
EXPOSE 2000

# Run the application
ENTRYPOINT ["java", "-jar", "/app/vulnerable-springboot-app.jar"]
