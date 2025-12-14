# 🍎 Fruit API MySQL - Level 2

REST API for managing fruit stock with providers using MySQL database.

## 📋 Table of Contents

- [Description](#-description)
- [Requirements](#-requirements)
- [Technologies](#-technologies)
- [Project Structure](#-project-structure)
- [Setup & Installation](#-setup--installation)
- [Running the Application](#-running-the-application)
- [API Endpoints](#-api-endpoints)
- [Testing](#-testing)
- [Docker](#-docker)
- [Database Schema](#-database-schema)
- [Development Process](#-development-process)
- [Assignment Details](#-assignment-details)

---

## 📝 Description

This project is a **Spring Boot REST API** for managing a fruit inventory system with providers. It allows you to:

- **Manage Providers**: Create, read, update, and delete fruit suppliers
- **Manage Fruits**: Track fruit stock with associated providers
- **Filter by Provider**: Query fruits supplied by specific providers
- **Full CRUD Operations**: Complete Create, Read, Update, Delete functionality

The application follows **best practices** including:
- ✅ TDD (Test-Driven Development) Outside-In approach
- ✅ Clean Architecture with MVC pattern
- ✅ DTOs and validation with Bean Validation
- ✅ Global exception handling
- ✅ Database relationships with JPA
- ✅ Docker containerization
- ✅ Environment variable configuration

---

## 🎯 Requirements

### Functional Requirements

#### Providers
- Register new providers with name and country
- No duplicate provider names allowed
- List all registered providers
- Update provider information
- Delete providers (only if they have no associated fruits)

#### Fruits
- Add fruits with name, weight (kg), and provider
- All fruits must have an associated provider
- Filter fruits by provider
- List all fruits
- Get fruit details by ID
- Update fruit information (including changing provider)
- Delete fruits

### Non-Functional Requirements
- Proper HTTP status codes (200, 201, 204, 400, 404, 409)
- Input validation with error messages
- Global exception handling
- RESTful API design
- MySQL database in production
- H2 in-memory database for testing
- Comprehensive test coverage (79+ tests)

---

## 🛠️ Technologies

### Backend
- **Java 21** (LTS)
- **Spring Boot 3.4.1**
- **Spring Web** (REST Controllers)
- **Spring Data JPA** (ORM)
- **Hibernate** (JPA Implementation)

### Database
- **MySQL 8.0** (Production)
- **H2 Database** (Testing)

### Validation & Mapping
- **Bean Validation** (Jakarta Validation)
- **Lombok** (Reduce boilerplate)

### Testing
- **JUnit 5** (Test framework)
- **Mockito** (Mocking)
- **MockMvc** (Controller testing)
- **AssertJ** (Assertions)

### DevOps
- **Docker** (Containerization)
- **Docker Compose** (Multi-container orchestration)
- **Maven** (Build tool)

---

## 📁 Project Structure
```
fruit-api-mysql/
├── src/
│   ├── main/
│   │   ├── java/cat/itacademy/s04/t02/n02/fruit/
│   │   │   ├── controller/
│   │   │   │   ├── FruitController.java
│   │   │   │   └── ProviderController.java
│   │   │   ├── service/
│   │   │   │   ├── FruitService.java
│   │   │   │   ├── FruitServiceImpl.java
│   │   │   │   ├── ProviderService.java
│   │   │   │   └── ProviderServiceImpl.java
│   │   │   ├── repository/
│   │   │   │   ├── FruitRepository.java
│   │   │   │   └── ProviderRepository.java
│   │   │   ├── model/
│   │   │   │   ├── Fruit.java
│   │   │   │   └── Provider.java
│   │   │   ├── dto/
│   │   │   │   ├── FruitRequestDTO.java
│   │   │   │   ├── FruitResponseDTO.java
│   │   │   │   ├── ProviderRequestDTO.java
│   │   │   │   └── ProviderResponseDTO.java
│   │   │   ├── mapper/
│   │   │   │   ├── FruitMapper.java
│   │   │   │   └── ProviderMapper.java
│   │   │   ├── exception/
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   ├── ResourceConflictException.java
│   │   │   │   ├── ErrorResponse.java
│   │   │   │   └── GlobalExceptionHandler.java
│   │   │   └── FruitApiMysqlApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-prod.properties
│   └── test/
│       ├── java/cat/itacademy/s04/t02/n02/fruit/
│       │   ├── controller/
│       │   │   ├── FruitControllerTest.java
│       │   │   └── ProviderControllerTest.java
│       │   ├── service/
│       │   │   ├── FruitServiceTest.java
│       │   │   └── ProviderServiceTest.java
│       │   ├── integration/
│       │   │   ├── FruitIntegrationTest.java
│       │   │   └── ProviderIntegrationTest.java
│       │   └── FruitApiMysqlApplicationTests.java
│       └── resources/
│           └── application-test.properties
├── .mvn/
├── Dockerfile
├── docker-compose.yml
├── .dockerignore
├── .env.example
├── .gitignore
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

## ⚙️ Setup & Installation

### Prerequisites

- **Java 21** or higher
- **Maven 3.8+** (or use included Maven Wrapper)
- **Docker** & **Docker Compose**
- **Git**

### 1. Clone the Repository
```bash
git clone <repository-url>
cd fruit-api-mysql
```

### 2. Configure Environment Variables

Copy the `.env.example` file to `.env`:
```bash
cp .env.example .env
```

Edit `.env` with your credentials:
```env
MYSQL_ROOT_PASSWORD=rootpassword
MYSQL_DATABASE=fruitdb
MYSQL_USER=fruituser
MYSQL_PASSWORD=fruitpass

DB_URL=jdbc:mysql://localhost:3306/fruitdb
DB_USERNAME=fruituser
DB_PASSWORD=fruitpass
```

### 3. Start MySQL with Docker
```bash
docker-compose up -d mysql
```

Verify MySQL is running:
```bash
docker-compose ps
```

---

## 🚀 Running the Application

### Option 1: Run with Maven (Development)
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`

### Option 2: Run with Docker Compose (Production)

Build and start all services:
```bash
docker-compose up --build -d
```

View logs:
```bash
docker-compose logs -f app
```

Stop services:
```bash
docker-compose down
```

### Option 3: Run JAR directly
```bash
./mvnw clean package -DskipTests
java -jar target/fruit-api-mysql-0.0.1-SNAPSHOT.jar
```

---

## 🌐 API Endpoints

### Health Check
```http
GET /actuator/health
```

**Response:** `200 OK`
```json
{
  "status": "UP"
}
```

---

### Providers

#### Create Provider
```http
POST /providers
Content-Type: application/json

{
  "name": "Fruits Inc",
  "country": "Spain"
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Fruits Inc",
  "country": "Spain"
}
```

#### Get All Providers
```http
GET /providers
```

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "Fruits Inc",
    "country": "Spain"
  }
]
```

#### Get Provider by ID
```http
GET /providers/{id}
```

**Response:** `200 OK` or `404 Not Found`

#### Update Provider
```http
PUT /providers/{id}
Content-Type: application/json

{
  "name": "Updated Fruits Inc",
  "country": "Italy"
}
```

**Response:** `200 OK` or `404 Not Found` or `409 Conflict`

#### Delete Provider
```http
DELETE /providers/{id}
```

**Response:** `204 No Content` or `404 Not Found` or `409 Conflict`

---

### Fruits

#### Create Fruit
```http
POST /fruits
Content-Type: application/json

{
  "name": "Apple",
  "weightInKilos": 10,
  "providerId": 1
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "Apple",
  "weightInKilos": 10,
  "provider": {
    "id": 1,
    "name": "Fruits Inc",
    "country": "Spain"
  }
}
```

#### Get All Fruits
```http
GET /fruits/all
```

**Response:** `200 OK`

#### Get Fruits by Provider
```http
GET /fruits?providerId=1
```

**Response:** `200 OK` or `404 Not Found`

#### Get Fruit by ID
```http
GET /fruits/{id}
```

**Response:** `200 OK` or `404 Not Found`

#### Update Fruit
```http
PUT /fruits/{id}
Content-Type: application/json

{
  "name": "Updated Apple",
  "weightInKilos": 15,
  "providerId": 2
}
```

**Response:** `200 OK` or `404 Not Found`

#### Delete Fruit
```http
DELETE /fruits/{id}
```

**Response:** `204 No Content` or `404 Not Found`

---

## 🧪 Testing

### Run All Tests
```bash
./mvnw test
```

### Run Tests with Coverage (JaCoCo)
```bash
./mvnw clean test jacoco:report
```

View coverage report:
```bash
open target/site/jacoco/index.html
```

### Test Statistics

- **Total Tests:** 91
- **Controller Tests:** 36
- **Service Unit Tests:** 26
- **Integration Tests:** 33
- **Coverage:** >90%

### Test Types

- **Unit Tests:** Mockito for isolated testing
- **Controller Tests:** MockMvc with @WebMvcTest
- **Integration Tests:** @SpringBootTest with H2 database

---

## 🐳 Docker

### Build Docker Image
```bash
docker build -t fruit-api-mysql:latest .
```

### Multi-Stage Build

The Dockerfile uses a **multi-stage build** for optimization:

1. **Build Stage:** Compiles the application with Maven (JDK 21)
2. **Runtime Stage:** Runs the application with minimal JRE 21

**Benefits:**
- Smaller image size (~250MB vs ~600MB)
- Better security (no build tools in production)
- Faster deployment

### Docker Compose Services
```yaml
services:
  mysql:     # MySQL 8.0 database
  app:       # Spring Boot application
```

**Networks:** `fruit-network` (bridge)  
**Volumes:** `mysql_data` (persistent storage)

### Useful Docker Commands
```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop services
docker-compose down

# Remove volumes (⚠️ deletes data)
docker-compose down -v

# Rebuild images
docker-compose up --build
```

---

## 🗄️ Database Schema

### Tables

#### `providers`
```sql
CREATE TABLE providers (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL UNIQUE,
  country VARCHAR(255) NOT NULL
);
```

#### `fruits`
```sql
CREATE TABLE fruits (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) NOT NULL,
  weight_in_kilos INT NOT NULL,
  provider_id BIGINT NOT NULL,
  FOREIGN KEY (provider_id) REFERENCES providers(id)
);
```

### Relationship

- **Provider → Fruit:** One-to-Many (1:N)
- **Fruit → Provider:** Many-to-One (N:1)
- Cascade delete: Deleting a provider with fruits returns `409 Conflict`

---

## 🔄 Development Process

### TDD Outside-In Approach

For each feature, we followed this cycle:

1. **RED:** Write failing acceptance test (Controller)
2. **GREEN:** Implement minimal code to pass (Controller + mocked Service)
3. **RED:** Write failing unit test (Service)
4. **GREEN:** Implement Service logic
5. **REFACTOR:** Clean up code
6. **INTEGRATION:** Write end-to-end test
7. **COMMIT:** Save working feature

### Example Workflow
```
Controller Test (MockMvc) ❌
    ↓
Controller Implementation ✅
    ↓
Service Test (Mockito) ❌
    ↓
Service Implementation ✅
    ↓
Repository & Entity
    ↓
Integration Test (@SpringBootTest) ✅
    ↓
Commit 🎉
```

---

## 📚 Assignment Details

### Course Information

- **Course:** IT Academy - Spring Framework
- **Sprint:** S4 - API REST with Spring Boot
- **Level:** Level 2 - MySQL Integration
- **Group:** cat.itacademy.s04.t02.n02
- **Artifact:** fruit-api-mysql

### Learning Objectives

✅ Create REST APIs with Spring Boot  
✅ Persist data with Spring Data JPA  
✅ Apply HTTP verbs and status codes correctly  
✅ Implement dynamic routes with Path and Query Params  
✅ Write automated tests with TDD  
✅ Handle exceptions globally with @ControllerAdvice  
✅ Structure projects with MVC pattern  
✅ Create entity relationships with JPA  
✅ Use DTOs and validate input data  
✅ Containerize applications with Docker  
✅ Configure databases with environment variables

### User Stories Completed

#### Providers (4 stories)
1. ✅ Register a new provider
2. ✅ List all providers
3. ✅ Update provider information
4. ✅ Delete a provider

#### Fruits (6 stories)
5. ✅ Add fruit with provider
6. ✅ Filter fruits by provider
7. ✅ List all fruits
8. ✅ Get specific fruit by ID
9. ✅ Update fruit information
10. ✅ Delete a fruit
