# -------------------------------------------------------------------
# STAGE 1: BUILDER (Cache & Compile)
# -------------------------------------------------------------------
FROM eclipse-temurin:21-jdk-jammy AS builder
WORKDIR /app

# 1. Cache Dependencies
COPY .mvn/ .mvn
COPY mvnw pom.xml ./
# Ensure mvnw is executable
RUN chmod +x mvnw
RUN ./mvnw dependency:go-offline

# 2. Compile Code
COPY src ./src
RUN ./mvnw package -DskipTests

# -------------------------------------------------------------------
# STAGE 2: RUNNER (Secure & Optimized)
# -------------------------------------------------------------------
FROM eclipse-temurin:21-jre-jammy

# 1. Create a Non-Root User (Security Best Practice)
RUN groupadd -r spring && useradd -r -g spring spring

# 2. Set working directory and ownership
WORKDIR /app
RUN chown spring:spring /app

# 3. Copy JAR from builder
COPY --from=builder --chown=spring:spring /app/target/*.jar app.jar

# 4. Switch to Non-Root User
USER spring:spring

# 5. JVM Memory Optimization (Prevents OOMKilled in K8s)
# -XX:MaxRAMPercentage=75.0 : Uses 75% of available container memory
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75.0 -XX:+ExitOnOutOfMemoryError -Djava.security.egd=file:/dev/./urandom"

# 6. Expose Port (Documentation Only - UPDATE THIS PER SERVICE)
# Gateway: 8080 | Auth: 8081 | Claim: 8082 | Discovery: 8761
EXPOSE 8082

# 7. Start App
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]