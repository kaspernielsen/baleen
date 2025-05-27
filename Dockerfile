FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app

# Create a non-root user
RUN addgroup -S spring && adduser -S spring -G spring

# Copy the built artifact and keystore files
COPY --chown=spring:spring /target/*.jar app.jar
COPY --chown=spring:spring /src/main/resources/secom/mcp-baleen-test-keystore.p12 /app/keystore.p12
COPY --chown=spring:spring /src/main/resources/secom/mcp-truststore.jks /app/mcp-truststore.jks
COPY --chown=spring:spring /src/main/resources/secom/truststore.p12 /app/truststore.p12

# Switch to non-root user
USER spring:spring

# Environment variables - profile will be set at runtime
ENV JAVA_OPTS="-Xmx1g -Xms512m -XX:+UseG1GC"
ENV SPRING_PROFILES_ACTIVE=docker

# Expose HTTP and HTTPS ports
EXPOSE 8080 8443

# Start the application with error output
ENTRYPOINT ["sh", "-c", "echo 'Starting Baleen application...' && java $JAVA_OPTS -jar app.jar || (echo 'Application failed to start' && exit 1)"]