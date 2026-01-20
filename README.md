# Unified Claims & Reimbursement Management Platform (UCRMP)

**Status:** V1.2 Stable (Production Ready)
**Last Updated:** January 2026

## 📋 Overview
The UCRMP is a cloud-native, microservices-based platform designed to handle claims and reimbursements. It follows a distributed architecture, decoupling the React frontend from the Spring Boot backend services, orchestrated via Kubernetes.

**Recent Major Update (V1.2):** * **Security Hardening:** Transistioned to **HS512** algorithm for JWT signing with a cryptographically secure 512-bit alphanumeric key.
* **GitOps Secrets:** Implemented **Bitnami Sealed Secrets** to manage sensitive credentials securely in Git.
* **Frontend Stability:** Resolved routing loops and token persistence issues for a seamless login experience.

---

## 🏗️ Architecture

The system is built on the **"Smart Gateway, Dumb Services"** pattern:

1.  **Frontend (Experience Layer):** A Single Page Application (SPA) built with React, TypeScript, and Zustand for state management. It features robust error handling, auto-token detection, and protected route guards.
2.  **API Gateway (Security Layer):** The single entry point for all traffic. It validates JWT tokens (HS512), handles CORS, and routes traffic. It injects user identity headers (`X-User-Id`) into downstream requests.
3.  **Microservices (Logic Layer):** Independent Spring Boot services (Auth, Claim) that focus purely on business logic.
4.  **Infrastructure:** The entire system runs on Kubernetes (Kind), utilizing native DNS for service discovery, Nginx Ingress for routing, and Sealed Secrets for credential management.

### Request Flow
* **User** → **Ingress (Nginx)** → **API Gateway** (Auth Check) → **Microservice** → **Database**

---

## 📂 Repository Structure

ucrmp-platform/
├── backend/                  # Java Spring Boot Microservices
│   ├── api-gateway/          # Spring Cloud Gateway (Port 8080)
│   ├── auth-service/         # Authentication (HS512 JWT) (Port 8081)
│   ├── claim-service/        # Claim Business Logic (Port 8082)
│   └── discovery-service/    # Eureka (Legacy support)
│
├── frontend/                 # React Application
│   ├── src/                  # Source Code (Zustand Stores, Components)
│   ├── Dockerfile            # Nginx Container setup
│   └── nginx.conf            # Static serving config
│
├── helm/                     # Helm Charts & Secrets
│   └── ucrmp/
│       ├── templates/
│       │   └── sealed-secrets.yaml # Encrypted Production Secrets
│       └── values.yaml       # Deployment Config
│
├── k8s/                      # Kubernetes Infrastructure (Manifests)
│   ├── 00-namespace.yaml     # Environment Setup
│   ├── 01-infrastructure/    # Databases (MySQL) & PVCs
│   ├── 02-ingress/           # Nginx Ingress Rules
│   └── 04-backend/           # Microservice Deployments
│
└── kind-config.yaml          # Local Cluster Configuration

---

## 🛠️ Technology Stack

| Domain | Technology |
| :--- | :--- |
| **Frontend** | React 18, TypeScript, Vite, TailwindCSS, Zustand, Axios |
| **Backend** | Java 21, Spring Boot 3.5.7, Spring Security |
| **Security** | JWT (HS512), Bitnami Sealed Secrets |
| **Database** | MySQL 8.0, Flyway (Migration Management) |
| **Infrastructure** | Docker, Kubernetes (Kind), Nginx Ingress |
| **Testing** | JUnit 5, Testcontainers |

---

## 🚀 How to Run

### Prerequisites
* Docker Desktop
* Kubectl
* Java 21 JDK & Node.js 18+ (For local development)
* **Kubeseal** (For managing secrets)

### Option 1: Kubernetes (Recommended)
This runs the full platform simulating a production environment.

1.  **Initialize Cluster:**
    ```bash
    kind create cluster --config kind-config.yaml
    ```

2.  **Install Ingress Controller:**
    ```bash
    kubectl apply -f [https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml](https://raw.githubusercontent.com/kubernetes/ingress-nginx/main/deploy/static/provider/kind/deploy.yaml)
    ```

3.  **Deploy System & Secrets:**
    ```bash
    # Apply Namespace & Databases
    kubectl apply -f k8s/00-namespace.yaml
    kubectl apply -f k8s/01-infrastructure/
    
    # Apply Sealed Secrets (CRITICAL for Auth)
    kubectl apply -f helm/ucrmp/templates/sealed-secrets.yaml

    # Wait for databases to be ready, then apply Backend & Ingress
    kubectl apply -f k8s/04-backend/
    kubectl apply -f k8s/02-ingress/
    ```

4.  **Access:**
    * **App:** http://localhost:8081

### Option 2: Docker Compose
Quick start for checking connectivity without Kubernetes.

1.  **Run:**
    ```bash
    docker-compose up --build
    ```

2.  **Access:**
    * **App:** http://localhost:80

---

## 🔐 Configuration & Secrets
* **JWT Secret:** The platform uses a 512-bit alphanumeric key for HS512 signing.
* **Secret Management:** Secrets are encrypted using `kubeseal` and stored in `helm/ucrmp/templates/sealed-secrets.yaml`.
    * *To rotate secrets:* Edit the raw secret locally, run `kubeseal` with the cluster certificate, and push the updated YAML.
* **Database:** `ddl-auto` is disabled. Schema changes are managed via Flyway scripts in `src/main/resources/db/migration`.