# ---------
# Stage 1: The "Build" Stage
# ---------
# We use a full Java JDK image (Eclipse Temurin, a popular OpenJDK build) to compile our code.
# The 'as builder' name is a temporary tag for this stage.
FROM eclipse-temurin:21-jdk-jammy AS builder

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven wrapper files
COPY .mvn/ .mvn
COPY mvnw pom.xml ./

# Download all the dependencies
# This is a key optimization: we download deps *before* copying code.
# This way, if our code changes but dependencies don't, Docker reuses this layer.
RUN ./mvnw dependency:go-offline

# Copy the rest of our source code
COPY src ./src

# Build the application, skipping the tests (they should run in CI/CD)
RUN ./mvnw package -DskipTests

# ---------
# Stage 2: The "Run" Stage
# ---------
# We now start from a *fresh*, minimal image that ONLY has the Java Runtime.
# This makes our final image much smaller and more secure.
FROM eclipse-temurin:21-jre-jammy

# Set the working directory
WORKDIR /app

# Copy the application JAR file from the "builder" stage (Stage 1)
# This is the only file we need from the entire build.
COPY --from=builder /app/target/*.jar app.jar

# Expose the port that our application runs on
EXPOSE 8081

# The command to run when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]