# Unified Claims & Reimbursement Management Platform (UCRMP)

**Status:** V1.1 Production Ready

## 📋 Overview
The UCRMP is a cloud-native, microservices-based platform designed to handle claims and reimbursements. It follows a distributed architecture, decoupling the React frontend from the Spring Boot backend services, orchestrated via Kubernetes.

This repository is a Monorepo containing the Frontend, Backend microservices, and Infrastructure configuration.

---

## 🏗️ Architecture

The system is built on the **"Smart Gateway, Dumb Services"** pattern:

1.  **Frontend (Experience Layer):** A Single Page Application (SPA) built with React and TypeScript. It is offline-aware and served via Nginx.
2.  **API Gateway (Security Layer):** The single entry point for all traffic. It handles JWT authentication, routing, and CORS. It injects user identity headers (`X-User-Id`) into downstream requests.
3.  **Microservices (Logic Layer):** Independent Spring Boot services (Auth, Claim) that focus purely on business logic.
4.  **Infrastructure:** The entire system runs on Kubernetes (Kind), utilizing native DNS for service discovery and Nginx Ingress for routing.

### Request Flow
* **User** → **Ingress (Nginx)** → **API Gateway** → **Microservice** → **Database**

---

## 📂 Repository Structure

ucrmp-platform/
├── backend/                   # Java Spring Boot Microservices
│   ├── api-gateway/           # Spring Cloud Gateway (Port 8080)
│   ├── auth-service/          # Authentication & Identity (Port 8081)
│   ├── claim-service/         # Claim Management Logic (Port 8082)
│   └── discovery-service/     # Eureka (Legacy support)
│
├── frontend/                  # React Application
│   ├── src/                   # Source Code
│   ├── Dockerfile             # Nginx Container setup
│   └── nginx.conf             # Static serving config
│
├── k8s/                       # Kubernetes Infrastructure
│   ├── 00-namespace.yaml      # Environment Setup
│   ├── 01-infrastructure/     # Databases (MySQL) & PVCs
│   ├── 02-ingress/            # Nginx Ingress Rules
│   ├── 04-backend/            # Microservice Deployments
│   └── kind-config.yaml       # Local Cluster Configuration
│
└── docker-compose.yml         # Local Development Setup

---

## 🛠️ Technology Stack

| Domain | Technology |
| :--- | :--- |
| **Frontend** | React 18, TypeScript, Vite, TailwindCSS |
| **Backend** | Java 21, Spring Boot 3.5.7, Spring Security |
| **Database** | MySQL 8.0, Flyway (Migration Management) |
| **Infrastructure** | Docker, Kubernetes (Kind), Nginx Ingress |
| **Testing** | JUnit 5, Testcontainers |

---

## 🚀 How to Run

### Prerequisites
* Docker Desktop
* Kubectl
* Java 21 JDK & Node.js 18+ (For local development)

### Option 1: Kubernetes (Recommended)
This runs the full platform simulating a production environment.

1.  **Initialize Cluster:**
    kind create cluster --config kind-config.yaml

2.  **Install Ingress Controller:**
    kubectl apply -f https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml

3.  **Deploy System:**
    # Apply Namespace & Databases
    kubectl apply -f k8s/00-namespace.yaml
    kubectl apply -f k8s/01-infrastructure/
    
    # Wait for databases to be ready, then apply Backend & Ingress
    kubectl apply -f k8s/04-backend/
    kubectl apply -f k8s/02-ingress/

4.  **Access:**
    * **App:** http://localhost:8081

### Option 2: Docker Compose
Quick start for checking connectivity without Kubernetes.

1.  **Run:**
    docker-compose up --build

2.  **Access:**
    * **App:** http://localhost:80

---

## 🔐 Configuration
* **Environment Variables:** All secrets (DB passwords, JWT keys) are managed via `.env` files or Kubernetes Secrets.
* **Database:** `ddl-auto` is disabled. Schema changes are managed via Flyway scripts in `src/main/resources/db/migration`.