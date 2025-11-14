================================================================================
                 Unified Claims & Reimbursement Management Platform
                             (UCRMP Backend V1.1)
================================================================================

Project Status: Complete, Hardened & Tested

Welcome to the UCRMP backend. This repository contains a complete, enterprise-grade
microservices system built with Spring Boot, Spring Cloud, and Docker. It is a robust,
secure, and scalable foundation for a modern claims management application. The backend
is composed of four independent containerized services and two dedicated databases,
all orchestrated via a single Docker Compose file.

--------------------------------------------------------------------------------
                        HIGH-LEVEL ARCHITECTURE
--------------------------------------------------------------------------------
This project is "cloud-native" and follows the "Smart Gateway, Dumb Services" pattern.

   +------------------+       +-------------------+
   |                  |       |                   |
   |   API Gateway     | <---> |  Discovery Service|
   | ("Smart Front Door")       | ("Phone Book")   |
   |                  |       |                   |
   +------------------+       +-------------------+
           |
           v
   +------------------+
   |                  |
   |  Claim Service    |
   | ("Dumb Worker")   |
   |                  |
   +------------------+
           |
           v
       claim-db (MySQL)

Notes:
1. API Gateway validates JWTs, extracts userId/roles, and injects headers (X-User-Id, X-User-Roles).
2. Business Services are "dumb" — they trust headers and focus solely on core business logic.
3. Discovery Service (Eureka) dynamically resolves service locations — no hardcoded URLs.

--------------------------------------------------------------------------------
                        END-TO-END REQUEST FLOW
--------------------------------------------------------------------------------

1. User Login (Obtain JWT "Passport")
+----------------------+          +--------------------+        +------------------+
| Frontend (React App) |  POST    | API Gateway (:8080)|  POST  | Auth Service (:8081)
+----------------------+ /auth/login +--------------------+--------> +------------------+
                                    |                   |        |
                                    | Validate JWT, add headers       |
                                    |                   |
                                    +-------------------+
Response: JWT with custom claims (userId, roles)

2. Create Claim (Secured)
Frontend sends POST /api/v1/claims with JWT
Flow:

   +----------------------+        
   | Frontend (React App) | POST /claims
   +----------------------+        
              |
              v
   +----------------------+        
   |   API Gateway        | 1. Run AuthenticationFilter
   |   (:8080)            | 2. Validate JWT
   |                      | 3. Extract userId/roles
   |                      | 4. Add headers: X-User-Id, X-User-Roles
   +----------------------+
              |
              v
   +----------------------+        
   | Discovery Service    | Resolve CLAIM-SERVICE location
   | (Eureka :8761)      |
   +----------------------+
              |
              v
   +----------------------+        
   |  Claim Service (:8082)|
   |  Controller reads     |
   |  X-User-Id header     |
   |  Service validates    |
   |  metadata             |
   |  Repository saves     |
   |  claim to claim-db    |
   +----------------------+
              |
              v
Response: 201 Created (New Claim JSON)

--------------------------------------------------------------------------------
                        ENTERPRISE-GRADE FEATURES
--------------------------------------------------------------------------------
- 🚀 Dynamic Service Discovery: No hard-coded URLs. Uses Eureka for resilience.
- 🛡️ Centralized Smart Gateway Security: All JWT validation happens at the gateway.
- 🗃️ Database Version Control (Flyway): Versioned migrations for predictable DB setup.
- 🧪 Automated Testing:
    * Unit Tests (Mockito)
    * Integration Tests (Testcontainers) with real MySQL & Flyway migrations.
- 📦 Environment-Agnostic: Config via .env and docker-compose.yml (Build Once, Deploy Anywhere).
- 🧩 Dynamic Data (JSON Metadata Pattern): Flexible JSON columns for metadata without schema changes.
- 🔑 Secure Admin Bootstrapping: Creates "First Admin" on first boot securely via env variables.
- 🌐 Global CORS Configuration: API Gateway handles CORS for frontend connection.

--------------------------------------------------------------------------------
                           TECHNOLOGY STACK
--------------------------------------------------------------------------------
Java          : Java 21, Spring Boot 3.x
Microservices : Spring Cloud Gateway (Reactive), Spring Cloud Netflix Eureka
Security      : Spring Security 6, JWT
Database      : Spring Data JPA, MySQL 8.0, Flyway (Migrations)
DevOps        : Docker, Docker Compose, Multi-stage Dockerfiles
Testing       : JUnit 5, Mockito, Testcontainers
Utilities     : SLF4J (Logging), Jackson (JSON), jakarta.validation

--------------------------------------------------------------------------------
                      GETTING STARTED (FULL BACKEND)
--------------------------------------------------------------------------------
Prerequisites:
- Git
- Docker Desktop

Clone Repository:
git clone https://github.com/sushantchavan987/ucrmp-platform.git
cd ucrmp-platform

Setup Secrets (.env file):
1. Copy .env.example -> .env
2. Open .env and generate secure JWT_SECRET
   Example: openssl rand -base64 64

Run Entire System:
docker-compose up --build

This will build all 4 microservices and start all 6 containers (4 services + 2 databases).

================================================================================
                          END OF DOCUMENT
================================================================================
