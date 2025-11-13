# ---------
# Stage 1: The "Build" Stage
# ---------
# Use a full JDK to build the app
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# Copy build files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download dependencies (for layer caching)
RUN ./mvnw dependency:go-offline

# Copy source code and build
COPY src ./src
RUN ./mvnw package -DskipTests

# ---------
# Stage 2: The "Run" Stage
# ---------
# Use a minimal JRE for a small, secure image
FROM eclipse-temurin:21-jre-jammy
WORKDIR /app

# Copy the built .jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the gateway's port
EXPOSE 8080

# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]