#!/bin/bash

SERVICES=(
  "identity-service"
  "identity-access-service"
  "mfa-service"
  "data-privacy-consent-service"
  "waf-policy-service"
  "audit-correlation-service"
  "billing-service"
  "payment-service"
  "payments-adapter-service"
  "pricing-service"
  "policy-engine-service"
  "insurer-adapter-service"
  "notification-service"
  "reporting-read-model-service"
  "tenant-org-service"
  "user-profile-service"
  "geo-location-service"
  "database-indexing-service"
  "database-management-service"
  "idempotency-service"
  "integration-adapters-service"
  "logging-aggregation-service"
  "metrics-telemetry-service"
  "request-routing-service"
  "service-health-monitor-service"
)

for SERVICE in "${SERVICES[@]}"; do
  SERVICE_NAME=$(echo "$SERVICE" | sed 's/-service$/ service/' | sed 's/-/ /g' | sed 's/\b\(.\)/\u\1/g')
  SERVICE_KEBAB="$SERVICE"
  SERVICE_CAMEL=$(echo "$SERVICE" | sed 's/-/ /g' | awk '{for(i=1;i<=NF;i++) $i=toupper(substr($i,1,1)) substr($i,2)}1' | sed 's/ //g')
  
  echo "Creating workflows for $SERVICE_KEBAB..."
  
  # Create build.yml
  cat > "$SERVICE/.github/workflows/build.yml" << BUILDEOF
name: Build ${SERVICE_NAME} Service

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Build with Maven
        run: mvn -B clean compile --file pom.xml

      - name: Run unit tests
        run: mvn -B test --file pom.xml

      - name: Generate test report
        if: always()
        uses: dorny/test-reporter@v1
        with:
          name: Maven Tests
          path: target/surefire-reports/*.xml
          reporter: java-junit
          fail-on-error: true
BUILDEOF

  # Create test.yml
  cat > "$SERVICE/.github/workflows/test.yml" << TESTEOF
name: Test ${SERVICE_NAME} Service

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

jobs:
  test:
    runs-on: ubuntu-latest

    services:
      mongodb:
        image: mongo:6
        ports:
          - 27017:27017
      redis:
        image: redis:7-alpine
        ports:
          - 6379:6379
      kafka:
        image: bitnami/kafka:3
        ports:
          - 9092:9092
        env:
          KAFKA_CFG_KAFKA_LISTENERS: PLAINTEXT://:9092
          KAFKA_CFG_AUTO_CREATE_TOPICS_ENABLE: true

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Run tests
        run: mvn -B verify --file pom.xml

      - name: Generate coverage report
        run: mvn jacoco:report

      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          file: target/site/jacoco/jacoco.xml
TESTEOF

  # Create deploy.yml
  cat > "$SERVICE/.github/workflows/deploy.yml" << DEPLOYEOF
name: Deploy ${SERVICE_NAME} Service

on:
  push:
    branches: [ main ]
    paths:
      - 'src/**'
      - 'pom.xml'
      - 'Dockerfile'

jobs:
  deploy:
    runs-on: ubuntu-latest

    steps:
      - name: Checkout code
        uses: actions/checkout@v3

      - name: Set up JDK 21
        uses: actions/setup-java@v3
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven

      - name: Build package
        run: mvn -B clean package -DskipTests

      - name: Build Docker image
        run: |
          docker build -t gogidix/${SERVICE_KEBAB}:\${{ github.sha }} .
          docker tag gogidix/${SERVICE_KEBAB}:\${{ github.sha }} gogidix/${SERVICE_KEBAB}:latest

      - name: Login to Railway
        run: railway login --token \${{ secrets.RAILWAY_TOKEN }}

      - name: Deploy to Railway
        run: railway up --service ${SERVICE_KEBAB}
DEPLOYEOF

  echo "Created workflows for $SERVICE_KEBAB"
done

echo "All workflows created successfully!"
