# Stage 1: Build the application
FROM openjdk:17-jdk-slim as builder

# Set the working directory in the container
WORKDIR /app

# Copy the build.gradle.kts, settings.gradle.kts, and gradlew files
COPY build.gradle.kts settings.gradle.kts gradlew /app/

# Copy the gradle wrapper directory
COPY gradle /app/gradle

# Copy the source code
COPY src /app/src

# Give permissions to gradlew
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build --no-daemon

# Stage 2: Run the application
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /app

# Copy the JAR file from the builder stage
COPY --from=builder /app/build/libs/tree-sitter-ng1-1.0-SNAPSHOT.jar app.jar

# Expose the port that the application will run on
EXPOSE 8080

# Define the command to run the application
CMD ["java", "-jar", "/app/app.jar"]

# Print Java version to verify
RUN java -version