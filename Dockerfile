# APPLICATION BUILD
FROM eclipse-temurin:17-jdk AS builder
WORKDIR /app

# Maven runner
COPY mvnw .
COPY .mvn .mvn

# Dependency
#COPY pom.xml .
COPY pom.xml ./pom.xml
RUN ./mvnw dependency:go-offline -U

# Copy the full source code
COPY src ./src
RUN ./mvnw clean package -DskipTests

# Application Run
FROM eclipse-temurin:17-jdk AS runner
WORKDIR /app

# Copy the built jar from the builder stage
COPY --from=builder /app/target/*.jar app.jar

# Expose the default Spring Boot port (you can override in compose)
EXPOSE 8072

# Run the application
ENTRYPOINT ["java","-jar","/app/app.jar"]