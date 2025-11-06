# ---------
# Stage 1: The "Build" Stage
# ---------
# We use a full Java JDK image to compile our code.
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download all the dependencies (great for caching)
RUN ./mvnw dependency:go-offline

# Copy the rest of our source code
COPY src ./src

# Build the application, skipping the tests
RUN ./mvnw package -DskipTests

# ---------
# Stage 2: The "Run" Stage
# ---------
# We start from a minimal JRE image for a small, secure final container.
FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

# Copy the application JAR file from the "builder" stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the port for the claim-service
EXPOSE 8082

# The command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]