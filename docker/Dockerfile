# ============================================================
# Multi-stage Dockerfile — Java/Maven Spring Boot App
# Stage 1: Build with Maven
# Stage 2: Run with minimal JRE (keeps image small)
# ============================================================

# ---------- Stage 1: Build ----------
FROM maven:3.9-eclipse-temurin-17-alpine AS builder

WORKDIR /build

# Copy pom first (layer caching — dependencies won't re-download if pom unchanged)
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copy source and build
COPY app/src ./src
RUN mvn clean package -DskipTests -B

# ---------- Stage 2: Runtime ----------
FROM eclipse-temurin:17-jre-alpine AS runtime

# Security: run as non-root user
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

# Copy only the fat jar from builder
COPY --from=builder /build/target/*.jar app.jar

# Pass version at build time
ARG APP_VERSION=1.0.0
ENV APP_VERSION=${APP_VERSION}

# Spring Boot port
EXPOSE 8080

# Health check — uses the /health endpoint
HEALTHCHECK --interval=30s --timeout=5s --start-period=30s --retries=3 \
    CMD wget -qO- http://localhost:8080/health || exit 1

ENTRYPOINT ["java", "-jar", "app.jar"]
