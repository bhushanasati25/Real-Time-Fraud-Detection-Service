# ============================================
# NOTIFICATION SERVICE - DOCKERFILE
# ============================================

FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

# Copy all module poms
COPY common-libs/pom.xml common-libs/
COPY ingestion-service/pom.xml ingestion-service/
COPY fraud-engine/pom.xml fraud-engine/
COPY notification-service/pom.xml notification-service/

# Create empty src directories for modules we won't build
RUN mkdir -p ingestion-service/src/main/java fraud-engine/src/main/java

RUN chmod +x mvnw && ./mvnw dependency:go-offline -B -pl common-libs,notification-service -am || true

COPY common-libs/src common-libs/src
COPY notification-service/src notification-service/src

RUN ./mvnw clean package -pl notification-service -am -DskipTests -B

FROM eclipse-temurin:17-jre
RUN groupadd -g 1001 appgroup && useradd -u 1001 -g appgroup appuser
WORKDIR /app
COPY --from=builder /app/notification-service/target/*.jar app.jar
RUN chown -R appuser:appgroup /app
USER appuser
EXPOSE 8082
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8082/actuator/health || exit 1
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0 -Djava.security.egd=file:/dev/./urandom"
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
