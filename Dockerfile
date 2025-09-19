# Multi-stage build for TeamCity Testing Framework
FROM openjdk:11-jdk-slim AS builder

# Set working directory
WORKDIR /app

# Install Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Copy Maven wrapper and pom.xml
COPY mvnw mvnw.cmd pom.xml ./
COPY .mvn .mvn

# Download dependencies
RUN ./mvnw dependency:go-offline -B

# Copy source code
COPY src ./src

# Build the application
RUN ./mvnw clean compile test-compile -B

# Runtime stage
FROM openjdk:11-jre-slim

# Set working directory
WORKDIR /app

# Install required packages
RUN apt-get update && \
    apt-get install -y \
    curl \
    wget \
    unzip \
    && rm -rf /var/lib/apt/lists/*

# Copy built artifacts from builder stage
COPY --from=builder /app/target ./target
COPY --from=builder /app/src ./src
COPY --from=builder /app/pom.xml ./
COPY --from=builder /app/mvnw ./
COPY --from=builder /app/.mvn ./.mvn

# Copy configuration files
COPY config ./config

# Set permissions
RUN chmod +x mvnw

# Create reports directory
RUN mkdir -p reports

# Expose ports
EXPOSE 8080 8081

# Set environment variables
ENV MAVEN_OPTS="-Xmx1024m"
ENV JAVA_OPTS="-Xmx1024m"

# Default command
CMD ["./mvnw", "clean", "test", "-Dtest=**/*Test"]
