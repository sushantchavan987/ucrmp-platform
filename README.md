Unified Claims & Reimbursement Management Platform (UCRMP)
Project Status: Backend V1.1 (Complete, Hardened & Tested)
Welcome to the UCRMP backend. This repository contains a complete, enterprise-grade microservices system built with Spring Boot, Spring Cloud, and Docker. It is a robust, secure, and scalable foundation for a modern claims management application.
This backend is 100% complete and is composed of four independent, containerized services and two dedicated databases, all orchestrated by a single Docker Compose file.
1. High-Level Architecture (The "Top 1%" Protocol)
This project is not a monolith; it's a "cloud-native" system. The core architecture follows a "Smart Gateway, Dumb Services" pattern.
• 1. API Gateway (The "Smart" Front Door): A single, secure entry point for the entire application. It is the only service that understands security (JWTs). It validates the user's "digital passport" (JWT), extracts their userId and roles, and injects them as secure headers (X-User-Id, X-User-Roles) into the request.
• 2. Business Services (The "Dumb" Workers): Services like claim-service are "dumb" by design. They have no security code. They simply trust the incoming headers from the gateway, making them fast, simple, and easy to test.
• 3. Service Discovery (The "Phone Book"): Services do not have hard-coded URLs. A discovery-service (Eureka) acts as a dynamic "phone book" so services can find each other by name (e.g., lb://CLAIM-SERVICE).
End-to-End Request Flow (Secured Endpoint)
Here is the flow for a user creating a new claim, which demonstrates our entire architecture in action.
sequenceDiagram
    participant F as Frontend (React App)
    participant G as API Gateway (:8080)
    participant E as Discovery Service (Eureka :8761)
    participant C as Claim Service (:8082)
    participant A as Auth Service (:8081)

    %% --- Login Flow ---
    F->>G: POST /api/v1/auth/login
    G->>E: Where is 'AUTH-SERVICE'?
    E-->>G: At 172.x.x.x:8081
    G->>A: POST /api/v1/auth/login
    A-->>G: 200 OK (with JWT "Passport")
    G-->>F: 200 OK (with JWT)

    %% --- Create Claim Flow (Secured) ---
    F->>G: POST /api/v1/claims (with JWT)
    
    %% --- Gateway Security Check ---
    G->>G: **1. Filter: Run AuthenticationFilter**
    G->>G: **2. Validate JWT Signature (OK)**
    G->>G: **3. Extract Claims (User-ID, Roles)**
    G->>G: **4. Mutate Request: Add Headers (X-User-Id, X-User-Roles)**
    
    %% --- Gateway Routing ---
    G->>E: Where is 'CLAIM-SERVICE'?
    E-->>G: At 172.x.x.x:8082
    G->>C: POST /api/v1/claims (with NEW Headers)
    
    %% --- Claim Service Logic ---
    C->>C: **Controller:** Reads 'X-User-Id' header
    C->>C: **Service:** Validates dynamic metadata (e.g., TravelMetadata)
    C->>C: **Repository:** Saves claim to 'claim-db'
    
    C-->>G: 201 Created (New Claim JSON)
    G-->>F: 201 Created (New Claim JSON)

2. Enterprise-Grade Features
This backend is "enterprise-grade" because it implements professional patterns beyond basic functionality.
• 🚀 Dynamic Service Discovery (Eureka): No hard-coded localhost:8081 URLs. The api-gateway finds services dynamically using Eureka, making the system resilient and cloud-ready.
• 🛡️ Centralized "Smart Gateway" Security: Security is handled once at the gateway. This simplifies downstream services, reduces redundant code, and creates a single, hardened security checkpoint.
• 🗃️ Database Version Control (Flyway): We never use ddl-auto=create. Both auth-db and claim-db are 100% managed by versioned SQL migration scripts (e.g., V1__..., V2__...), ensuring a repeatable, auditable, and production-safe database schema.
• 🧪 Comprehensive Automated Testing (Testcontainers): The "Definition of Done" for our services is a green test bar. We have:
o Unit Tests (Mockito): To test business logic in isolation.
o Full Integration Tests (Testcontainers): Our tests automatically start a real MySQL database in a Docker container, run our Flyway migrations, and test the entire application from the HTTP endpoint to the database.
• 📦 Environment-Agnostic Configuration: The entire system is run via docker-compose.yml and a .env file. All services are "dumb" artifacts that are configured at runtime via environment variables (e.g., DB_HOST, JWT_SECRET). This is a "Build Once, Deploy Anywhere" "Top 1%" protocol.
• 🧩 Dynamic Data (JSON Metadata Pattern): The claim-service uses a JSON column and type-safe Java records to validate and store dynamic data (e.g., TravelMetadata vs. MedicalMetadata) without changing the database schema.
• 🔑 Secure Admin Bootstrapping: Solved the "Admin Paradox." The auth-service's DataSeeder automatically creates a "First Admin" user on its first boot, securely reading the credentials from environment variables.
• 🌐 Global CORS Configuration: The api-gateway is configured to correctly handle Cross-Origin Resource Sharing (CORS), allowing our future React app (from localhost:5173) to connect securely.
3. Technology Stack
CategoryTechnologyJavaJava 21, Spring Boot 3.xMicroservicesSpring Cloud Gateway (Reactive), Spring Cloud Netflix EurekaSecuritySpring Security 6, JSON Web Tokens (JWT)DatabaseSpring Data JPA, MySQL 8.0, Flyway (Migrations)DevOpsDocker, Docker Compose, Multi-Stage DockerfilesTestingJUnit 5, Mockito, TestcontainersUtilitiesSLF4J (Logging), Jackson (JSON), jakarta.validation4. Getting Started (Running the Entire Backend)
This project is fully containerized. You only need two prerequisites.
Prerequisites
• Git
• Docker Desktop (Must be installed and running)
1. Clone the Repository
git clone [https://github.com/sushantchavan987/ucrmp-platform.git](https://github.com/sushantchavan987/ucrmp-platform.git)
cd ucrmp-platform

2. Create Your Secret .env File
This project is secure and reads secrets from an environment file (which is ignored by Git).
1. Find the file named .env.example in the root folder.
2. Copy this file and rename the copy to .env.
3. Critical Security Step: Open the .env file and generate a new, secure JWT_SECRET.
o (You can use a tool like openssl rand -base64 64 in your terminal)
4. The .env file is now ready.
3. Run the Entire System
This single command will build all four microservice images and start all six containers (4 services + 2 databases) in the correct order.
docker-compose up --build


