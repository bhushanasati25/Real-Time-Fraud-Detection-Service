# ============================================
# INGESTION SERVICE - DOCKERFILE
# Multi-stage build for optimized image
# ============================================

# Stage 1: Build
FROM eclipse-temurin:17-jdk AS builder

WORKDIR /app

# Copy Maven wrapper and pom files
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy all module poms
COPY common-libs/pom.xml common-libs/
COPY ingestion-service/pom.xml ingestion-service/
COPY fraud-engine/pom.xml fraud-engine/
COPY notification-service/pom.xml notification-service/

# Create empty src directories for modules we won't build
RUN mkdir -p fraud-engine/src/main/java notification-service/src/main/java

# Download dependencies (cached layer)
RUN chmod +x mvnw && \
    ./mvnw dependency:go-offline -B -pl common-libs,ingestion-service -am || true

# Copy source code for what we need
COPY common-libs/src common-libs/src
COPY ingestion-service/src ingestion-service/src

# Build the application
RUN ./mvnw clean package -pl ingestion-service -am -DskipTests -B

# Stage 2: Runtime
FROM eclipse-temurin:17-jre

# Add non-root user for security
RUN groupadd -g 1001 appgroup && \
    useradd -u 1001 -g appgroup appuser

WORKDIR /app

# Copy the built artifact
COPY --from=builder /app/ingestion-service/target/*.jar app.jar

# Set ownership
RUN chown -R appuser:appgroup /app

# Switch to non-root user
USER appuser

# Expose port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/actuator/health || exit 1

# JVM options for containerized environment
ENV JAVA_OPTS="-XX:+UseContainerSupport \
    -XX:MaxRAMPercentage=75.0 \
    -XX:InitialRAMPercentage=50.0 \
    -Djava.security.egd=file:/dev/./urandom"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
