# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

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

# Expose the port that the application will run on
EXPOSE 8080

# Copy the JAR file to the app directory
COPY build/libs/tree-sitter-ng1-1.0-SNAPSHOT.jar app.jar

# Define the command to run the application
CMD ["java", "-jar", "/app/app.jar"]

# Print Java version to verify
RUN java -version
