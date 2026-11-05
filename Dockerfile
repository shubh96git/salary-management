# One image containing the API and the UI (see docs/decisions/0001).

# 1. Build the Angular app.
FROM node:22-alpine AS frontend
WORKDIR /build
COPY frontend/package.json frontend/package-lock.json ./
RUN npm ci
COPY frontend/ ./
RUN npx ng build

# 2. Build the Spring Boot jar with the UI as static content.
FROM eclipse-temurin:21-jdk AS backend
WORKDIR /build
COPY backend/.mvn .mvn
COPY backend/mvnw backend/pom.xml ./
RUN ./mvnw -B -q dependency:go-offline
COPY backend/src src
COPY --from=frontend /build/dist/frontend/browser src/main/resources/static
# Tests need a MySQL database and run before pushing, not inside the image build.
RUN ./mvnw -B -q package -DskipTests

# 3. Run on a slim Java runtime.
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=backend /build/target/salary-management-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
