# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-alpine

# Set the working directory in the container
WORKDIR /app

# Copy the build.gradle, settings.gradle and gradlew files
COPY build.gradle settings.gradle gradlew /app/

# Copy the gradle wrapper folder
COPY gradle /app/gradle

# Copy the rest of the application files
COPY src /app/src

# Give permissions to gradlew
RUN chmod +x gradlew

# Build the application
RUN ./gradlew build --no-daemon

# Expose the port that the application will run on
EXPOSE 8080

# Define the command to run the application
CMD ["java", "-jar", "/app/build/libs/your-app-name.jar"]
