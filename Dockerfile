# Use a lightweight JDK base image
FROM eclipse-temurin:17-jre-alpine

# Set the working directory inside the container
WORKDIR /app

# Copy the application JAR
COPY target/URL-encoding-decoding-app.jar app.jar

# Copy all dependencies
COPY target/dependency/*.jar lib/

# Set the entry point to run the application with the dependencies
ENTRYPOINT ["java", "-cp", "app.jar:lib/*", "com.example.Main"]

# Expose the application's port
EXPOSE 8080
