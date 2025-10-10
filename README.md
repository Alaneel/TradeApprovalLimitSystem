# Trading Approval Limit System

A comprehensive trading system with approval workflows, built using Spring Boot, MongoDB, Redis, and Streamlit.

## 📋 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Prerequisites](#prerequisites)
- [Quick Start](#quick-start)
- [Configuration](#configuration)
- [API Documentation](#api-documentation)
- [Testing](#testing)
- [Deployment](#deployment)
- [Monitoring](#monitoring)
- [Development](#development)
- [Contributing](#contributing)

## ✨ Features

- **Trade Execution**: Execute trades with real-time limit validation
- **Instrument Verification**: Validate instruments using Bloom filters
- **Approval Workflow**: Submit instruments for approval when not in system
- **Caching**: Multi-level caching with Redis and Caffeine
- **Circuit Breaker**: Resilience4j for fault tolerance
- **Health Monitoring**: Actuator endpoints for health checks and metrics
- **API Documentation**: Interactive Swagger/OpenAPI documentation
- **Containerization**: Docker and Docker Compose support
- **CI/CD**: Automated testing and deployment pipelines

## 🏗️ Architecture

```
┌─────────────────┐
│   Streamlit UI  │  (Port 8501)
└────────┬────────┘
         │
         ▼
┌─────────────────┐
│  Spring Boot    │  (Port 8080)
│    Backend      │
└────────┬────────┘
         │
         ├──────────┐
         ▼          ▼
    ┌────────┐  ┌─────────┐
    │MongoDB │  │  Redis  │
    │(27017) │  │ (6379)  │
    └────────┘  └─────────┘
```

### Tech Stack

**Backend:**
- Java 11
- Spring Boot 2.6.3
- MongoDB
- Redis
- Resilience4j (Circuit Breaker)
- Guava (Bloom Filter)

**Frontend:**
- Python 3.9
- Streamlit 1.24.0

**DevOps:**
- Docker & Docker Compose
- GitHub Actions
- JaCoCo (Test Coverage)
- Trivy (Security Scanning)

## 📦 Prerequisites

- Java 11 or higher
- Maven 3.6+
- Python 3.9+
- Docker & Docker Compose (for containerized deployment)
- MongoDB 7.0
- Redis 7.0

## 🚀 Quick Start

### Option 1: Docker Compose (Recommended)

```bash
# Clone the repository
git clone <repository-url>
cd NoMoreMicroservice

# Start all services
docker-compose up -d

# View logs
docker-compose logs -f
```

Access the application:
- Frontend: http://localhost:8501
- Backend API: http://localhost:8080
- API Documentation: http://localhost:8080/swagger-ui.html
- Health Check: http://localhost:8080/actuator/health

### Option 2: Local Development

1. **Start MongoDB:**
   ```bash
   brew services start mongodb-community@7.0
   # or
   docker run -d -p 27017:27017 --name mongodb mongo:7.0
   ```

2. **Start Redis:**
   ```bash
   brew services start redis
   # or
   docker run -d -p 6379:6379 --name redis redis:7-alpine
   ```

3. **Start Backend:**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Start Frontend:**
   ```bash
   source venv/bin/activate
   # or create venv: python -m venv venv && source venv/bin/activate
   pip install -r python_src/requirements.txt
   streamlit run python_src/trading_dashboard.py
   ```

## ⚙️ Configuration

### Environment Variables

Copy `.env.example` to `.env` and configure:

```bash
# Spring Configuration
SPRING_PROFILE=dev
SERVER_PORT=8080

# MongoDB
MONGODB_URI=mongodb://localhost:27017/trading_system

# Redis
REDIS_HOST=localhost
REDIS_PORT=6379

# Python/Streamlit
BACKEND_API_URL=http://localhost:8080
```

### Profiles

- `dev` - Development environment (default)
- `test` - Testing environment
- `prod` - Production environment

Activate a profile:
```bash
# Maven
mvn spring-boot:run -Dspring-boot.run.profiles=prod

# Java
java -jar -Dspring.profiles.active=prod target/trading-system-0.0.1-SNAPSHOT.jar

# Environment variable
export SPRING_PROFILE=prod
```

## 📚 API Documentation

### Interactive API Docs

Once the backend is running, visit:
- Swagger UI: http://localhost:8080/swagger-ui.html
- OpenAPI Spec: http://localhost:8080/v3/api-docs

### Key Endpoints

#### Trading Operations
- `POST /api/trader/verify-instrument` - Verify if instrument is valid
- `POST /api/trader/trade` - Execute a trade
- `GET /api/trader/limit/{counterparty}/{instrumentGroup}` - Get available limit
- `GET /api/trader/trades` - Get trade history
- `GET /api/trader/instruments` - Get all instruments

#### Approval Workflow
- `POST /api/trader/approval-request` - Create approval request
- `GET /api/trader/approval-requests` - List approval requests

#### Health & Monitoring
- `GET /actuator/health` - Health check
- `GET /actuator/metrics` - Application metrics
- `GET /actuator/prometheus` - Prometheus metrics

## 🧪 Testing

### Run Tests

```bash
# Run all tests
mvn test

# Run tests with coverage
mvn clean test jacoco:report

# View coverage report
open target/site/jacoco/index.html
```

### Test Coverage

The project aims for 50%+ code coverage. Coverage reports are generated in:
- `target/site/jacoco/index.html`

### Integration Tests

Integration tests use embedded MongoDB and Redis:

```bash
mvn verify -P integration-tests
```

## 🚢 Deployment

### Building for Production

```bash
# Build JAR
mvn clean package -DskipTests

# Build Docker images
docker build -t trading-backend:latest .
docker build -f Dockerfile.streamlit -t trading-frontend:latest .
```

### Kubernetes Deployment

```bash
# Apply Kubernetes manifests (if available)
kubectl apply -f k8s/
```

### Cloud Deployment

Refer to cloud-specific deployment guides:
- AWS: Use ECS/EKS with RDS DocumentDB and ElastiCache
- Azure: Use AKS with Cosmos DB and Azure Cache for Redis
- GCP: Use GKE with Cloud Datastore and Memorystore

## 📊 Monitoring

### Health Checks

```bash
# Application health
curl http://localhost:8080/actuator/health

# MongoDB health
curl http://localhost:8080/actuator/health/mongo

# Redis health
curl http://localhost:8080/actuator/health/redis
```

### Metrics

Prometheus metrics are exposed at:
```
http://localhost:8080/actuator/prometheus
```

### Logging

Logs are structured and can be aggregated using:
- ELK Stack (Elasticsearch, Logstash, Kibana)
- Splunk
- Datadog

Log levels can be configured in `application.yml`:
```yaml
logging:
  level:
    root: INFO
    com.gic: DEBUG
```

## 🛠️ Development

### Project Structure

```
.
├── src/
│   ├── main/
│   │   ├── java/com/gic/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST controllers
│   │   │   ├── dto/             # Data Transfer Objects
│   │   │   ├── exception/       # Exception handling
│   │   │   ├── health/          # Health indicators
│   │   │   ├── model/           # Domain models
│   │   │   ├── repository/      # Data repositories
│   │   │   ├── service/         # Business logic
│   │   │   └── util/            # Utility classes
│   │   └── resources/
│   │       ├── application.yml           # Main config
│   │       ├── application-dev.yml       # Dev config
│   │       ├── application-prod.yml      # Prod config
│   │       └── application-test.yml      # Test config
│   └── test/                    # Test classes
├── python_src/                  # Streamlit application
├── .github/workflows/           # CI/CD pipelines
├── docker-compose.yml           # Docker Compose config
├── Dockerfile                   # Backend Dockerfile
├── Dockerfile.streamlit         # Frontend Dockerfile
└── pom.xml                      # Maven dependencies

```

### Adding New Features

1. Create feature branch: `git checkout -b feature/your-feature`
2. Implement changes with tests
3. Ensure tests pass: `mvn test`
4. Create pull request using the PR template

### Code Quality

```bash
# Run checkstyle
mvn checkstyle:check

# Run security scan
mvn dependency-check:check
```

## 🤝 Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

See [CONTRIBUTING.md](CONTRIBUTING.md) for detailed guidelines.

## 📝 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 🆘 Support

For issues and questions:
- GitHub Issues: Create an issue in this repository
- Email: support@tradingsystem.com

## 🔄 CI/CD Pipeline

The project uses GitHub Actions for continuous integration:

- **Build & Test**: Runs on every push and PR
- **Code Quality**: Checkstyle and security scans
- **Docker Build**: Builds Docker images on main branch
- **Dependency Updates**: Weekly checks for outdated dependencies

View pipeline status:
[![CI/CD Pipeline](https://github.com/your-org/NoMoreMicroservice/actions/workflows/ci.yml/badge.svg)](https://github.com/your-org/NoMoreMicroservice/actions)

## 🎯 Roadmap

- [ ] Implement authentication with JWT
- [ ] Add WebSocket support for real-time updates
- [ ] Implement audit logging
- [ ] Add rate limiting
- [ ] Multi-tenancy support
- [ ] GraphQL API
- [ ] Mobile application

## 📚 Additional Resources

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Streamlit Documentation](https://docs.streamlit.io)
- [MongoDB Best Practices](https://docs.mongodb.com/manual/administration/production-notes/)
- [Redis Best Practices](https://redis.io/topics/admin)
- [Resilience4j Documentation](https://resilience4j.readme.io/)

---

Built with ❤️ by the Trading System Team
