# APPLICATION BUILD
ARG MODULE_ORIGIN=Gateway
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app

# Maven runner
COPY mvnw .
COPY .mvn .mvn
RUN chmod +x mvnw

# Dependency
COPY pom.xml .
RUN ./mvnw dependency:go-offline -U

# Copy the full source code
COPY JM-${MODULE_ORIGIN}/src JM-${MODULE_ORIGIN}/src

# Build the Spring Boot application
RUN ./mvnw clean package -DskipTests

# Application Run
FROM eclipse-temurin:21-jre-alpine AS runner
ARG MODULE_ORIGIN
WORKDIR /app
RUN apk add --no-cache curl

# Copy the built jar from the builder stage
COPY --from=builder /app/JM-${MODULE_ORIGIN}/target/*.jar app.jar

# Expose the default Spring Boot port (you can override in compose)
EXPOSE 8072

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]