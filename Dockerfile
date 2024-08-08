# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the build.gradle.kts, settings.gradle.kts, and gradlew files from the src directory
COPY src/build.gradle.kts src/settings.gradle.kts src/gradlew /app/

# Copy the gradle wrapper directory
COPY src/gradle /app/gradle

# Copy the source code
COPY src/src /app/src

# Give permissions to gradlew
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build --no-daemon

# Expose the port that the application will run on
EXPOSE 8080

# Define the command to run the application
CMD ["java", "-jar", "/app/build/libs/your-app-name.jar"]
