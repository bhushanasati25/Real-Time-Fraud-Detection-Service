<div align="center">

# 🛡️ Real-Time Fraud Detection System

### Enterprise-Grade Event-Driven Fraud Detection Platform

**👨‍💻 Developed by [Bhushan Asati](https://github.com/bhushanasati25)**

[![Java](https://img.shields.io/badge/Java-17-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white)](https://spring.io/projects/spring-boot)
[![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-7.5-231F20?style=for-the-badge&logo=apache-kafka&logoColor=white)](https://kafka.apache.org/)
[![Python](https://img.shields.io/badge/Python-3.11-3776AB?style=for-the-badge&logo=python&logoColor=white)](https://www.python.org/)
[![FastAPI](https://img.shields.io/badge/FastAPI-0.104-009688?style=for-the-badge&logo=fastapi&logoColor=white)](https://fastapi.tiangolo.com/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis&logoColor=white)](https://redis.io/)
[![Docker](https://img.shields.io/badge/Docker-Ready-2496ED?style=for-the-badge&logo=docker&logoColor=white)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow?style=for-the-badge)](LICENSE)

---

**A production-ready, microservices-based fraud detection system that combines rule-based engines with machine learning models to detect fraudulent transactions in real-time.**

[Features](#-features) •
[Architecture](#-architecture) •
[Quick Start](#-quick-start) •
[API Documentation](#-api-documentation) •
[Configuration](#%EF%B8%8F-configuration)

</div>

---

## 📋 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Technology Stack](#-technology-stack)
- [Project Structure](#-project-structure)
- [Quick Start](#-quick-start)
- [API Documentation](#-api-documentation)
- [Fraud Detection Rules](#-fraud-detection-rules)
- [Machine Learning Model](#-machine-learning-model)
- [Configuration](#%EF%B8%8F-configuration)
- [Monitoring & Observability](#-monitoring--observability)
- [Testing](#-testing)
- [Troubleshooting](#-troubleshooting)
- [Contributing](#-contributing)
- [Author](#-author)
- [License](#-license)

---

## ✨ Features

<table>
<tr>
<td width="50%">

### 🚀 Core Capabilities

- **Real-Time Processing** - Sub-second transaction analysis
- **Hybrid Detection** - Rule engine + ML model scoring
- **Event-Driven Architecture** - Kafka-based async processing
- **Microservices Design** - Independently deployable services
- **High Availability** - Scalable and fault-tolerant

</td>
<td width="50%">

### 🔧 Technical Features

- **RESTful APIs** - OpenAPI/Swagger documented
- **Database Persistence** - PostgreSQL with optimized indexes
- **Caching Layer** - Redis for velocity checks
- **Containerized** - Docker & Docker Compose ready
- **Health Monitoring** - Spring Actuator endpoints

</td>
</tr>
</table>

### Key Highlights

| Feature | Description |
|---------|-------------|
| 🎯 **Multi-Rule Engine** | Configurable rules for amount thresholds, velocity checks, location anomalies, and time-based patterns |
| 🤖 **ML Integration** | Python-based ML model with fraud probability scoring and feature importance analysis |
| 📊 **Real-Time Alerts** | Instant fraud notifications via Email, Webhook, and SMS channels |
| 🔄 **Async Processing** | Non-blocking transaction ingestion with Kafka message queuing |
| 📈 **Audit Trail** | Complete transaction history with fraud scores and triggered rules |

---

## 🏗 Architecture

<div align="center">

### High-Level System Architecture

</div>

> **Enterprise-grade microservices architecture** combining rule-based fraud detection with machine learning for real-time transaction analysis.

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'primaryColor': '#4A90A4', 'primaryTextColor': '#fff', 'primaryBorderColor': '#2C5F6E', 'lineColor': '#5C6BC0', 'secondaryColor': '#E8F5E9', 'tertiaryColor': '#FFF3E0', 'background': '#FAFAFA', 'mainBkg': '#FFFFFF', 'secondBkg': '#F5F5F5', 'border1': '#E0E0E0', 'border2': '#BDBDBD', 'fontFamily': 'arial'}}}%%

flowchart LR
    subgraph EXTERNAL["  🌐 EXTERNAL  "]
        direction TB
        CLIENT["� Client<br/>━━━━━━━━━<br/>REST API"]
    end

    subgraph INGESTION["  � INGESTION LAYER  "]
        direction TB
        API["� Ingestion Service<br/>━━━━━━━━━━━━━━<br/>📍 Port 8080<br/>Spring Boot 3.2"]
    end

    subgraph STREAMING["  📨 EVENT STREAMING  "]
        direction TB
        KAFKA_IN[["� Kafka Topic<br/>transaction-events"]]
        KAFKA_OUT[["� Kafka Topic<br/>fraud-alerts"]]
    end

    subgraph CORE["  ⚙️ CORE PROCESSING  "]
        direction TB
        ENGINE["🛡️ Fraud Engine<br/>━━━━━━━━━━━━<br/>📍 Port 8081<br/>Spring Boot 3.2"]
        
        RULES["📋 Rule Engine<br/>━━━━━━━━━━<br/>• Amount Rules<br/>• Velocity Check<br/>• Location<br/>• Time-based"]
        
        ENGINE --- RULES
    end

    subgraph ML["  � ML LAYER  "]
        direction TB
        ML_SVC["🐍 ML Service<br/>━━━━━━━━━━<br/>📍 Port 8000<br/>FastAPI"]
    end

    subgraph ALERTS["  📢 NOTIFICATIONS  "]
        direction TB
        NOTIF["🔔 Notification Service<br/>━━━━━━━━━━━━━━━━<br/>📍 Port 8082<br/>Spring Boot 3.2"]
        CHANNELS["📧 Email  •  🔗 Webhook  •  📱 SMS"]
        NOTIF --- CHANNELS
    end

    subgraph DATA["  💾 DATA LAYER  "]
        direction LR
        PG[("🐘 PostgreSQL<br/>Port 5433")]
        RD[("⚡ Redis<br/>Port 6379")]
    end

    subgraph MONITOR["  📊 MONITORING  "]
        direction TB
        UI["📺 Kafka UI<br/>Port 8090"]
    end

    CLIENT ==> API
    API ==> KAFKA_IN
    KAFKA_IN ==> ENGINE
    ENGINE <--> ML_SVC
    ENGINE ==> KAFKA_OUT
    KAFKA_OUT ==> NOTIF
    ENGINE <--> PG
    ENGINE <--> RD
    KAFKA_IN -.-> UI
    KAFKA_OUT -.-> UI

    style CLIENT fill:#E3F2FD,stroke:#1565C0,stroke-width:2px,color:#0D47A1
    style API fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px,color:#1B5E20
    style KAFKA_IN fill:#FFF3E0,stroke:#EF6C00,stroke-width:2px,color:#E65100
    style KAFKA_OUT fill:#FFF3E0,stroke:#EF6C00,stroke-width:2px,color:#E65100
    style ENGINE fill:#F3E5F5,stroke:#7B1FA2,stroke-width:2px,color:#4A148C
    style RULES fill:#EDE7F6,stroke:#5E35B1,stroke-width:2px,color:#311B92
    style ML_SVC fill:#FCE4EC,stroke:#C2185B,stroke-width:2px,color:#880E4F
    style NOTIF fill:#E0F2F1,stroke:#00796B,stroke-width:2px,color:#004D40
    style CHANNELS fill:#B2DFDB,stroke:#00695C,stroke-width:1px,color:#004D40
    style PG fill:#E1F5FE,stroke:#0277BD,stroke-width:2px,color:#01579B
    style RD fill:#FFEBEE,stroke:#C62828,stroke-width:2px,color:#B71C1C
    style UI fill:#ECEFF1,stroke:#546E7A,stroke-width:2px,color:#37474F
```

---

<div align="center">

### 📐 Detailed Component Architecture

</div>

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'fontSize': '14px'}}}%%

flowchart TB
    subgraph CLIENTS["🌐 CLIENT APPLICATIONS"]
        WEB["🖥️ Web App"]
        MOBILE["📱 Mobile App"]
        PARTNER["🤝 Partner API"]
    end

    subgraph LB["⚖️ LOAD BALANCER"]
        NGINX["Nginx / ALB"]
    end

    subgraph SERVICES["🔧 MICROSERVICES"]
        subgraph SVC1["INGESTION SERVICE :8080"]
            direction TB
            CTRL["TransactionController"]
            VALID["ValidationService"]
            KAFKA_PROD["KafkaProducerService"]
            CTRL --> VALID --> KAFKA_PROD
        end

        subgraph SVC2["FRAUD ENGINE :8081"]
            direction TB
            LISTENER["KafkaListener"]
            PROCESSOR["TransactionProcessor"]
            RULE_CHAIN["RuleChain"]
            ML_CLIENT["MLModelClient"]
            ALERT_PUB["AlertPublisher"]
            LISTENER --> PROCESSOR
            PROCESSOR --> RULE_CHAIN
            PROCESSOR --> ML_CLIENT
            PROCESSOR --> ALERT_PUB
        end

        subgraph SVC3["ML MODEL SERVICE :8000"]
            direction TB
            FASTAPI["FastAPI Server"]
            MODEL["FraudDetectionModel"]
            FEATURES["FeatureExtractor"]
            FASTAPI --> FEATURES --> MODEL
        end

        subgraph SVC4["NOTIFICATION SERVICE :8082"]
            direction TB
            ALERT_LISTENER["FraudAlertListener"]
            EMAIL_SVC["EmailService"]
            WEBHOOK_SVC["WebhookService"]
            SMS_SVC["SmsService"]
            ALERT_LISTENER --> EMAIL_SVC
            ALERT_LISTENER --> WEBHOOK_SVC
            ALERT_LISTENER --> SMS_SVC
        end
    end

    subgraph MESSAGING["📨 APACHE KAFKA CLUSTER"]
        direction LR
        ZK["🔷 Zookeeper<br/>:2181"]
        BROKER["🔶 Kafka Broker<br/>:9092"]
        TOPIC1["📋 transaction-events"]
        TOPIC2["📋 fraud-alerts"]
        ZK --- BROKER
        BROKER --- TOPIC1
        BROKER --- TOPIC2
    end

    subgraph PERSISTENCE["💾 DATA STORES"]
        direction LR
        subgraph PG_CLUSTER["PostgreSQL :5433"]
            PG_MASTER[("🐘 Primary")]
        end
        subgraph REDIS_CLUSTER["Redis :6379"]
            REDIS_MASTER[("⚡ Cache")]
        end
    end

    WEB & MOBILE & PARTNER --> NGINX
    NGINX --> SVC1
    KAFKA_PROD --> TOPIC1
    TOPIC1 --> LISTENER
    ML_CLIENT <--> FASTAPI
    ALERT_PUB --> TOPIC2
    TOPIC2 --> ALERT_LISTENER
    PROCESSOR --> PG_MASTER
    PROCESSOR --> REDIS_MASTER

    classDef serviceBox fill:#E8F5E9,stroke:#2E7D32,stroke-width:2px
    classDef kafkaBox fill:#FFF3E0,stroke:#EF6C00,stroke-width:2px
    classDef dataBox fill:#E3F2FD,stroke:#1565C0,stroke-width:2px

    class SVC1,SVC2,SVC3,SVC4 serviceBox
    class MESSAGING kafkaBox
    class PERSISTENCE dataBox
```

---

<div align="center">

### 🗄️ Database Schema

</div>

```mermaid
erDiagram
    TRANSACTIONS ||--o{ FRAUD_ALERTS : generates
    TRANSACTIONS }o--|| USER_PROFILES : belongs_to
    RULES_CONFIG ||--o{ FRAUD_ALERTS : triggers
    AUDIT_LOG }o--|| TRANSACTIONS : logs

    TRANSACTIONS {
        bigint id PK
        varchar transaction_id UK
        decimal amount
        varchar currency
        varchar user_id FK
        varchar merchant_id
        varchar merchant_name
        varchar location
        varchar ip_address
        varchar channel
        varchar status
        boolean is_fraud
        decimal fraud_score
        text fraud_reason
        array rules_triggered
        int processing_time_ms
        timestamp created_at
        timestamp processed_at
    }

    FRAUD_ALERTS {
        bigint id PK
        varchar alert_id UK
        varchar transaction_id FK
        varchar alert_type
        varchar severity
        decimal fraud_score
        array rules_triggered
        text description
        text recommended_action
        varchar status
        timestamp created_at
        timestamp resolved_at
    }

    USER_PROFILES {
        bigint id PK
        varchar user_id UK
        varchar email
        varchar phone
        varchar country
        decimal typical_amount
        varchar last_known_ip
        varchar last_known_location
        timestamp last_transaction_at
        int transaction_count_24h
        decimal total_amount_24h
        decimal risk_score
        timestamp created_at
        timestamp updated_at
    }

    RULES_CONFIG {
        bigint id PK
        varchar rule_id UK
        varchar rule_name
        varchar rule_type
        text description
        decimal threshold_value
        varchar severity
        boolean is_active
        int priority
        timestamp created_at
    }

    AUDIT_LOG {
        bigint id PK
        varchar entity_type
        varchar entity_id
        varchar action
        jsonb old_value
        jsonb new_value
        varchar performed_by
        timestamp created_at
    }
```

---

<div align="center">

### 🐳 Docker Deployment Architecture

</div>

```mermaid
%%{init: {'theme': 'base'}}%%

flowchart TB
    subgraph DOCKER["� DOCKER COMPOSE ENVIRONMENT"]
        direction TB
        
        subgraph NETWORK["🌐 fraud-detection-network (bridge)"]
            direction LR
            
            subgraph INFRA["Infrastructure Services"]
                direction TB
                ZK["📦 zookeeper<br/>━━━━━━━━━<br/>confluentinc/cp-zookeeper:7.5<br/>📍 :2181"]
                KAFKA["📦 kafka<br/>━━━━━━━━━<br/>confluentinc/cp-kafka:7.5<br/>📍 :9092, :29092"]
                PG["📦 postgres<br/>━━━━━━━━━<br/>postgres:16-alpine<br/>📍 :5433→5432"]
                REDIS["📦 redis<br/>━━━━━━━━━<br/>redis:7-alpine<br/>📍 :6379"]
                
                ZK --> KAFKA
            end
            
            subgraph APPS["Application Services"]
                direction TB
                ING["📦 ingestion-service<br/>━━━━━━━━━━━━━━<br/>Spring Boot JAR<br/>📍 :8080"]
                FE["📦 fraud-engine<br/>━━━━━━━━━━━━━━<br/>Spring Boot JAR<br/>📍 :8081"]
                NS["📦 notification-service<br/>━━━━━━━━━━━━━━<br/>Spring Boot JAR<br/>📍 :8082"]
                ML["📦 ml-model-service<br/>━━━━━━━━━━━━━━<br/>Python FastAPI<br/>📍 :8000"]
            end
            
            subgraph TOOLS["Monitoring Tools"]
                direction TB
                KUI["📦 kafka-ui<br/>━━━━━━━━━<br/>provectuslabs/kafka-ui<br/>📍 :8090→8080"]
            end
        end
        
        subgraph VOLUMES["� Persistent Volumes"]
            direction LR
            V1["zookeeper-data"]
            V2["kafka-data"]
            V3["postgres-data"]
            V4["redis-data"]
            V5["ml-model-data"]
        end
    end
    
    KAFKA --> ING
    KAFKA --> FE
    KAFKA --> NS
    PG --> FE
    REDIS --> FE
    FE --> ML
    KAFKA --> KUI

    style ZK fill:#E8F5E9,stroke:#2E7D32
    style KAFKA fill:#FFF3E0,stroke:#EF6C00
    style PG fill:#E3F2FD,stroke:#1565C0
    style REDIS fill:#FFEBEE,stroke:#C62828
    style ING fill:#E8F5E9,stroke:#2E7D32
    style FE fill:#F3E5F5,stroke:#7B1FA2
    style NS fill:#E0F2F1,stroke:#00796B
    style ML fill:#FCE4EC,stroke:#C2185B
    style KUI fill:#ECEFF1,stroke:#546E7A
```

---

<div align="center">

### 🔄 Transaction Processing Flow

</div>

```mermaid
%%{init: {'theme': 'base', 'themeVariables': { 'actorLineColor': '#5C6BC0', 'signalColor': '#5C6BC0'}}}%%

sequenceDiagram
    autonumber
    
    box rgb(227, 242, 253) Client Layer
        participant C as � Client
    end
    
    box rgb(232, 245, 233) API Gateway
        participant I as 📥 Ingestion<br/>Service
    end
    
    box rgb(255, 243, 224) Event Streaming
        participant K as 🔶 Apache<br/>Kafka
    end
    
    box rgb(243, 229, 245) Processing Layer
        participant F as 🛡️ Fraud<br/>Engine
        participant R as 📋 Rule<br/>Engine
    end
    
    box rgb(252, 228, 236) ML Layer
        participant M as 🧠 ML Model<br/>Service
    end
    
    box rgb(225, 245, 254) Data Layer
        participant D as 🐘 PostgreSQL
        participant RD as ⚡ Redis
    end
    
    box rgb(224, 242, 241) Notification Layer
        participant N as 📢 Notification<br/>Service
    end

    Note over C,N: 🚀 TRANSACTION PROCESSING PIPELINE

    rect rgb(232, 245, 233)
        Note right of C: Step 1: Transaction Submission
        C->>+I: POST /api/v1/transactions
        I->>I: Validate payload
        I->>K: Publish to transaction-events
        I-->>-C: 202 Accepted + Receipt
    end

    rect rgb(243, 229, 245)
        Note right of K: Step 2: Fraud Analysis
        K->>+F: Consume transaction event
        F->>+RD: Check velocity cache
        RD-->>-F: User transaction history
        F->>+R: Execute rule chain
        R->>R: Amount threshold check
        R->>R: Velocity check
        R->>R: Location anomaly check
        R->>R: Time-based check
        R-->>-F: Rule scores + triggered rules
    end

    rect rgb(252, 228, 236)
        Note right of F: Step 3: ML Prediction
        F->>+M: POST /predict (features)
        M->>M: Feature extraction
        M->>M: Model inference
        M-->>-F: Fraud probability + confidence
        F->>F: Combine scores (60% rules + 40% ML)
        F->>F: Determine risk level
    end

    rect rgb(225, 245, 254)
        Note right of F: Step 4: Persistence
        F->>+D: INSERT transaction + result
        D-->>-F: Confirmed
        F->>RD: Update velocity cache
    end

    rect rgb(224, 242, 241)
        Note right of F: Step 5: Alert Generation
        alt Fraud Detected
            F->>K: Publish to fraud-alerts
            K->>+N: Consume fraud alert
            par Send Notifications
                N->>N: Send Email
                N->>N: Call Webhook
                N->>N: Send SMS
            end
            N-->>-F: Notifications sent
        end
    end

    Note over C,N: ✅ PROCESSING COMPLETE (~50-200ms)
```

---

<div align="center">

### 📊 Service Communication Matrix

</div>

| Source Service | Target Service | Protocol | Port | Topic/Endpoint |
|:--------------|:---------------|:---------|:-----|:---------------|
| Client | Ingestion Service | HTTP/REST | 8080 | `/api/v1/transactions` |
| Ingestion Service | Kafka | TCP | 9092 | `transaction-events` |
| Kafka | Fraud Engine | TCP | 9092 | `transaction-events` |
| Fraud Engine | ML Service | HTTP/REST | 8000 | `/predict` |
| Fraud Engine | PostgreSQL | TCP | 5432 | JDBC |
| Fraud Engine | Redis | TCP | 6379 | Redis Protocol |
| Fraud Engine | Kafka | TCP | 9092 | `fraud-alerts` |
| Kafka | Notification Service | TCP | 9092 | `fraud-alerts` |

---

<div align="center">

### 🎯 Processing Pipeline Overview

</div>

```mermaid
%%{init: {'theme': 'base'}}%%

flowchart LR
    subgraph INPUT["📥 INPUT"]
        A["Transaction<br/>Request"]
    end

    subgraph VALIDATE["✅ VALIDATE"]
        B["Schema<br/>Validation"]
    end

    subgraph ENRICH["📊 ENRICH"]
        C["Add Metadata<br/>& Timestamp"]
    end

    subgraph ANALYZE["🔍 ANALYZE"]
        D["Rule<br/>Engine"]
        E["ML<br/>Model"]
    end

    subgraph DECIDE["⚖️ DECIDE"]
        F["Risk<br/>Scoring"]
    end

    subgraph ACTION["🎯 ACTION"]
        G["✅ APPROVE"]
        H["⚠️ REVIEW"]
        I["🚫 BLOCK"]
    end

    A --> B --> C --> D & E
    D & E --> F
    F --> G & H & I

    style A fill:#E3F2FD,stroke:#1976D2,stroke-width:2px
    style B fill:#E8F5E9,stroke:#388E3C,stroke-width:2px
    style C fill:#FFF3E0,stroke:#F57C00,stroke-width:2px
    style D fill:#F3E5F5,stroke:#7B1FA2,stroke-width:2px
    style E fill:#FCE4EC,stroke:#C2185B,stroke-width:2px
    style F fill:#E0F7FA,stroke:#0097A7,stroke-width:2px
    style G fill:#C8E6C9,stroke:#43A047,stroke-width:3px
    style H fill:#FFF9C4,stroke:#FBC02D,stroke-width:3px
    style I fill:#FFCDD2,stroke:#E53935,stroke-width:3px
```

## 🛠 Technology Stack

<div align="center">

### Core Technologies

</div>

<table>
<tr>
<td align="center" width="20%">
<img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/java/java-original.svg" width="48" height="48" alt="Java"/>
<br><b>Backend Services</b>
<br><br>
<img src="https://img.shields.io/badge/Java-17-ED8B00?style=flat-square&logo=openjdk&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Spring_Boot-3.2-6DB33F?style=flat-square&logo=spring-boot&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Spring_Kafka-3.1-6DB33F?style=flat-square&logo=spring&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Spring_Data_JPA-3.2-6DB33F?style=flat-square&logo=spring&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Hibernate-6.4-59666C?style=flat-square&logo=hibernate&logoColor=white"/>
</td>
<td align="center" width="20%">
<img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/python/python-original.svg" width="48" height="48" alt="Python"/>
<br><b>ML Service</b>
<br><br>
<img src="https://img.shields.io/badge/Python-3.11-3776AB?style=flat-square&logo=python&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/FastAPI-0.104-009688?style=flat-square&logo=fastapi&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Scikit--learn-1.3-F7931E?style=flat-square&logo=scikit-learn&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/NumPy-1.26-013243?style=flat-square&logo=numpy&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Pydantic-2.5-E92063?style=flat-square&logo=pydantic&logoColor=white"/>
</td>
<td align="center" width="20%">
<img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/apachekafka/apachekafka-original.svg" width="48" height="48" alt="Kafka"/>
<br><b>Message Broker</b>
<br><br>
<img src="https://img.shields.io/badge/Apache_Kafka-7.5-231F20?style=flat-square&logo=apache-kafka&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Zookeeper-3.8-D22128?style=flat-square&logo=apache&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Kafka_UI-Latest-000000?style=flat-square&logo=apache-kafka&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Confluent-7.5-173361?style=flat-square"/>
</td>
<td align="center" width="20%">
<img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/postgresql/postgresql-original.svg" width="48" height="48" alt="PostgreSQL"/>
<br><b>Data Storage</b>
<br><br>
<img src="https://img.shields.io/badge/PostgreSQL-16-4169E1?style=flat-square&logo=postgresql&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Redis-7-DC382D?style=flat-square&logo=redis&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/JDBC-42.7-4169E1?style=flat-square&logo=postgresql&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/HikariCP-5.1-00CED1?style=flat-square"/>
</td>
<td align="center" width="20%">
<img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/docker/docker-original.svg" width="48" height="48" alt="Docker"/>
<br><b>DevOps</b>
<br><br>
<img src="https://img.shields.io/badge/Docker-24.0-2496ED?style=flat-square&logo=docker&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Docker_Compose-2.24-2496ED?style=flat-square&logo=docker&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/Maven-3.9-C71A36?style=flat-square&logo=apache-maven&logoColor=white"/>
<br>
<img src="https://img.shields.io/badge/GitHub_Actions-CI/CD-2088FF?style=flat-square&logo=github-actions&logoColor=white"/>
</td>
</tr>
</table>

<div align="center">

### 📦 Dependencies & Versions

</div>

| Category | Component | Version | Purpose | License |
|:--------:|:----------|:-------:|:--------|:-------:|
| 🟢 **Core** | Spring Boot | `3.2.1` | Application framework | Apache 2.0 |
| 🟢 **Core** | Spring Kafka | `3.1.1` | Kafka messaging integration | Apache 2.0 |
| 🟢 **Core** | Spring Data JPA | `3.2.1` | Database abstraction layer | Apache 2.0 |
| 🔵 **Database** | PostgreSQL Driver | `42.7.1` | JDBC database connectivity | BSD-2-Clause |
| 🔵 **Database** | HikariCP | `5.1.0` | High-performance connection pool | Apache 2.0 |
| 🔴 **Cache** | Jedis | `5.1.0` | Redis Java client | MIT |
| 🟣 **API** | SpringDoc OpenAPI | `2.3.0` | API documentation (Swagger) | Apache 2.0 |
| 🟣 **API** | Jackson | `2.16.1` | JSON serialization | Apache 2.0 |
| 🟡 **Tooling** | Lombok | `1.18.30` | Boilerplate code reduction | MIT |
| 🟡 **Tooling** | MapStruct | `1.5.5` | Type-safe DTO mapping | Apache 2.0 |
| 📊 **Metrics** | Micrometer | `1.12.1` | Application metrics & monitoring | Apache 2.0 |
| 🐍 **Python** | FastAPI | `0.104+` | Async REST API framework | MIT |
| 🐍 **Python** | Scikit-learn | `1.3+` | Machine learning library | BSD-3-Clause |
| 🐍 **Python** | Pydantic | `2.5+` | Data validation | MIT |

<div align="center">

### 🏷️ Version Compatibility Matrix

</div>

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        TECHNOLOGY VERSIONS                               │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                          │
│   Java ══════════════════════╗                                          │
│        17 LTS                ║                                          │
│                              ║                                          │
│   Spring Boot ═══════════════╬═══════════════════ Spring Ecosystem      │
│           3.2.1              ║        │                                 │
│                              ║        ├── Spring Kafka 3.1.1            │
│   Python ════════════════════╣        ├── Spring Data JPA 3.2.1         │
│       3.11                   ║        └── Spring Security 6.2.1         │
│                              ║                                          │
│   Apache Kafka ══════════════╣                                          │
│          7.5.0 (Confluent)   ║                                          │
│                              ║                                          │
│   PostgreSQL ════════════════╣                                          │
│          16.x                ║                                          │
│                              ║                                          │
│   Redis ═════════════════════╝                                          │
│      7.x                                                                 │
│                                                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 📁 Project Structure

```
Real-Time-Fraud-Detection-Service/
├── 📄 docker-compose.yml           # Container orchestration
├── 📄 pom.xml                      # Parent Maven POM (multi-module)
├── 📄 mvnw                         # Maven wrapper
├── 📄 README.md                    # Project documentation
│
├── 📁 init-scripts/                # Database initialization
│   └── 01-init.sql                 # Schema, tables, indexes, seed data
│
├── 📁 common-libs/                 # Shared library module
│   └── src/main/java/com/fraud/common/
│       ├── dto/                    # Data Transfer Objects
│       │   ├── TransactionEvent.java
│       │   ├── FraudAlert.java
│       │   ├── FraudResult.java
│       │   ├── MLScoreRequest.java
│       │   └── MLScoreResponse.java
│       ├── exception/              # Custom exceptions
│       ├── utils/                  # Utility classes
│       │   ├── JsonUtils.java
│       │   └── DateTimeUtils.java
│       └── constants/              # Constants & enums
│           └── KafkaConstants.java
│
├── 📁 ingestion-service/           # Transaction API Gateway
│   ├── Dockerfile
│   └── src/main/java/com/fraud/ingestion/
│       ├── controller/             # REST controllers
│       │   └── TransactionController.java
│       ├── service/                # Business logic
│       │   └── KafkaProducerService.java
│       └── config/                 # Configuration classes
│
├── 📁 fraud-engine/                # Core Fraud Detection
│   ├── Dockerfile
│   └── src/main/java/com/fraud/engine/
│       ├── rules/                  # Rule implementations
│       │   ├── Rule.java           # Rule interface
│       │   ├── RuleChain.java      # Rule orchestrator
│       │   ├── AmountThresholdRule.java
│       │   ├── VelocityRule.java
│       │   ├── LocationAnomalyRule.java
│       │   └── TimeAnomalyRule.java
│       ├── processor/              # Transaction processor
│       │   └── TransactionProcessor.java
│       ├── model/                  # ML client
│       │   └── MLModelClient.java
│       ├── entity/                 # JPA entities
│       │   ├── Transaction.java
│       │   └── UserProfile.java
│       ├── repository/             # Data access layer
│       └── listener/               # Kafka consumers
│
├── 📁 notification-service/        # Alert Notification
│   ├── Dockerfile
│   └── src/main/java/com/fraud/notification/
│       ├── listener/               # Kafka consumers
│       │   └── FraudAlertListener.java
│       └── service/                # Notification channels
│           ├── EmailService.java
│           ├── WebhookService.java
│           └── SmsService.java
│
└── 📁 ml-model-service/            # Python ML Service
    ├── Dockerfile
    ├── requirements.txt            # Python dependencies
    ├── app.py                      # FastAPI application
    ├── model.py                    # Fraud detection model
    └── train_model.py              # Model training script
```

---

## 🚀 Quick Start

### Prerequisites

Ensure you have the following installed:

| Tool | Version | Download |
|------|---------|----------|
| Docker | 20.10+ | [Get Docker](https://docs.docker.com/get-docker/) |
| Docker Compose | 2.0+ | Included with Docker Desktop |
| Git | Latest | [Get Git](https://git-scm.com/) |

> **Note:** Java 17+ and Maven 3.8+ are only required for local development without Docker.

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/bhushanasati25/Real-Time-Fraud-Detection-Service.git
   cd Real-Time-Fraud-Detection-Service
   ```

2. **Start all services with Docker Compose**
   ```bash
   docker-compose up -d --build
   ```

3. **Verify all services are running**
   ```bash
   docker-compose ps
   ```

   Expected output:
   ```
   NAME                   STATUS          PORTS
   fraud-engine           Up (healthy)    0.0.0.0:8081->8081/tcp
   ingestion-service      Up (healthy)    0.0.0.0:8080->8080/tcp
   kafka                  Up (healthy)    0.0.0.0:9092->9092/tcp
   kafka-ui               Up              0.0.0.0:8090->8080/tcp
   ml-model-service       Up (healthy)    0.0.0.0:8000->8000/tcp
   notification-service   Up (healthy)    0.0.0.0:8082->8082/tcp
   postgres               Up (healthy)    0.0.0.0:5433->5432/tcp
   redis                  Up (healthy)    0.0.0.0:6379->6379/tcp
   zookeeper              Up (healthy)    0.0.0.0:2181->2181/tcp
   ```

4. **Test the API**
   ```bash
   curl -X POST http://localhost:8080/api/v1/transactions \
     -H "Content-Type: application/json" \
     -d '{
       "transactionId": "TXN-TEST-001",
       "amount": 15000.00,
       "userId": "USER123",
       "merchantId": "MERCH456",
       "merchantName": "Electronics Store",
       "location": "New York",
       "ipAddress": "192.168.1.1",
       "channel": "ONLINE",
       "timestamp": "2024-12-18T21:00:00.000Z"
     }'
   ```

   Expected response:
   ```json
   {
     "success": true,
     "statusCode": 202,
     "message": "Transaction accepted for fraud detection processing",
     "data": {
       "transactionId": "TXN-TEST-001",
       "amount": 15000.00,
       ...
     }
   }
   ```

### Stopping the Services

```bash
# Stop all services
docker-compose down

# Stop and remove volumes (clean slate)
docker-compose down -v
```

---

## 📚 API Documentation

### Service Endpoints

| Service | Port | Swagger UI | Health Check |
|---------|------|------------|--------------|
| Ingestion Service | 8080 | [/swagger-ui.html](http://localhost:8080/swagger-ui.html) | [/actuator/health](http://localhost:8080/actuator/health) |
| Fraud Engine | 8081 | - | [/actuator/health](http://localhost:8081/actuator/health) |
| Notification Service | 8082 | - | [/actuator/health](http://localhost:8082/actuator/health) |
| ML Model Service | 8000 | [/docs](http://localhost:8000/docs) | [/health](http://localhost:8000/health) |
| Kafka UI | 8090 | [Dashboard](http://localhost:8090) | - |

### Ingestion Service API

#### Submit Transaction

```http
POST /api/v1/transactions
Content-Type: application/json
```

**Request Body:**

```json
{
  "transactionId": "TXN-2024-001",
  "amount": 1500.00,
  "currency": "USD",
  "userId": "USER-12345",
  "merchantId": "MERCH-789",
  "merchantName": "Amazon",
  "merchantCategory": "RETAIL",
  "location": "New York, USA",
  "latitude": 40.7128,
  "longitude": -74.0060,
  "ipAddress": "192.168.1.100",
  "deviceId": "DEVICE-ABC123",
  "cardType": "VISA",
  "cardLastFour": "4242",
  "transactionType": "PURCHASE",
  "channel": "ONLINE",
  "timestamp": "2024-12-18T15:30:00.000Z"
}
```

**Response (202 Accepted):**

```json
{
  "success": true,
  "statusCode": 202,
  "message": "Transaction accepted for fraud detection processing",
  "data": {
    "transactionId": "TXN-2024-001",
    "amount": 1500.00,
    "receivedAt": "2024-12-18T15:30:05.123Z",
    "sourceSystem": "ingestion-service"
  },
  "timestamp": "2024-12-18T15:30:05.123Z"
}
```

#### Submit Batch Transactions

```http
POST /api/v1/transactions/batch
Content-Type: application/json
```

#### Async Transaction Submission

```http
POST /api/v1/transactions/async
Content-Type: application/json
```

### ML Model Service API

#### Get Fraud Prediction

```http
POST /predict
Content-Type: application/json
```

**Request Body:**

```json
{
  "transactionId": "TXN-2024-001",
  "amount": 15000.00,
  "hourOfDay": 3,
  "dayOfWeek": 2,
  "isWeekend": false,
  "isNightTime": true,
  "transactionCountLast24h": 5,
  "totalAmountLast24h": 25000.00,
  "isNewDevice": true,
  "isNewLocation": true,
  "isNewMerchant": false
}
```

**Response:**

```json
{
  "transactionId": "TXN-2024-001",
  "fraudProbability": 0.7823,
  "prediction": "FRAUD",
  "isFraud": true,
  "confidence": 0.8456,
  "modelName": "FraudDetector",
  "modelVersion": "1.0.0",
  "processingTimeMs": 12,
  "threshold": 0.5,
  "topFeatures": [
    {"featureName": "amount", "importance": 0.35, "value": 15000.0},
    {"featureName": "is_night_time", "importance": 0.25, "value": 1.0}
  ]
}
```

---

## 🔍 Fraud Detection Rules

The system implements a configurable rule engine with the following built-in rules:

### Rule Definitions

| Rule ID | Name | Type | Condition | Score | Severity |
|---------|------|------|-----------|-------|----------|
| `RULE_001` | High Amount Threshold | AMOUNT | Amount > $10,000 | 0.5 | HIGH |
| `RULE_002` | Very High Amount | AMOUNT | Amount > $50,000 | 0.8 | CRITICAL |
| `RULE_003` | Velocity - Count | VELOCITY | >10 transactions/hour | 0.6 | MEDIUM |
| `RULE_004` | Velocity - Amount | VELOCITY | 24h total > $25,000 | 0.7 | HIGH |
| `RULE_005` | Location Anomaly | LOCATION | Different from last known | 0.4 | MEDIUM |
| `RULE_006` | IP Address Change | IP | Significant IP change | 0.3 | LOW |
| `RULE_007` | Night-time Transaction | TIME | Between 1 AM - 5 AM | 0.2 | LOW |
| `RULE_008` | International Transaction | LOCATION | Cross-border transaction | 0.3 | LOW |

### Scoring Mechanism

The final fraud score is calculated by combining rule-based scores with the ML model probability:

```
Final Score = (Rule Score × 0.6) + (ML Probability × 0.4)
```

### Risk Level Classification

| Score Range | Risk Level | Action |
|-------------|------------|--------|
| 0.00 - 0.30 | LOW | Approve |
| 0.31 - 0.50 | MEDIUM | Review |
| 0.51 - 0.70 | HIGH | Flag & Review |
| 0.71 - 1.00 | CRITICAL | Block & Alert |

---

## 🤖 Machine Learning Model

### Model Overview

The ML service uses a **Random Forest Classifier** trained on synthetic fraud data with the following characteristics:

| Property | Value |
|----------|-------|
| Algorithm | Random Forest |
| Features | 10 engineered features |
| Training Data | Synthetic fraud patterns |
| Threshold | 0.5 (configurable) |
| Inference Time | < 15ms |

### Features Used

```python
features = [
    'amount',                    # Transaction amount
    'hour_of_day',              # Hour (0-23)
    'day_of_week',              # Day (1-7)
    'is_weekend',               # Weekend flag
    'is_night_time',            # Night-time flag
    'transaction_count_last_24h', # Velocity
    'total_amount_last_24h',    # Amount velocity
    'is_new_device',            # New device flag
    'is_new_location',          # New location flag
    'is_new_merchant'           # New merchant flag
]
```

### Model Training

To retrain the model with new data:

```bash
cd ml-model-service
python train_model.py --data path/to/training_data.csv
```

---

## ⚙️ Configuration

### Environment Variables

#### Ingestion Service

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker address |
| `SERVER_PORT` | `8080` | Service port |

#### Fraud Engine

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9092` | Kafka broker address |
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/fraud_detection` | PostgreSQL URL |
| `SPRING_DATASOURCE_USERNAME` | `fraud_admin` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `fraud_secret_2024` | Database password |
| `SPRING_REDIS_HOST` | `localhost` | Redis host |
| `SPRING_REDIS_PORT` | `6379` | Redis port |
| `ML_SERVICE_URL` | `http://localhost:8000` | ML service URL |

#### ML Model Service

| Variable | Default | Description |
|----------|---------|-------------|
| `MODEL_PATH` | `/app/model/fraud_model.pkl` | Model file path |
| `PORT` | `8000` | Service port |
| `LOG_LEVEL` | `INFO` | Logging level |

### Kafka Topics

| Topic | Partitions | Description |
|-------|------------|-------------|
| `transaction-events` | 3 | Incoming transactions |
| `fraud-alerts` | 3 | Fraud detection alerts |

### Database Configuration

PostgreSQL connection details:

```yaml
Host: localhost (or postgres in Docker)
Port: 5433 (mapped from 5432)
Database: fraud_detection
Username: fraud_admin
Password: fraud_secret_2024
```

---

## 📊 Monitoring & Observability

### Health Endpoints

All Spring services expose actuator endpoints:

```bash
# Liveness probe
curl http://localhost:8080/actuator/health/liveness

# Readiness probe
curl http://localhost:8080/actuator/health/readiness

# Full health details
curl http://localhost:8080/actuator/health
```

### Kafka UI Dashboard

Access the Kafka UI at [http://localhost:8090](http://localhost:8090) to:

- View topic configurations
- Monitor consumer groups
- Inspect messages
- Track partition offsets

### Prometheus Metrics

Metrics are exposed at `/actuator/prometheus`:

```bash
curl http://localhost:8080/actuator/prometheus
```

### Log Aggregation

View logs for any service:

```bash
# All services
docker-compose logs -f

# Specific service
docker-compose logs -f fraud-engine

# Last 100 lines
docker-compose logs --tail=100 fraud-engine
```

---

## 🧪 Testing

### Unit Tests

```bash
# Run all unit tests
./mvnw test

# Run tests for specific module
./mvnw test -pl fraud-engine
```

### Integration Tests

```bash
# Run integration tests
./mvnw verify -P integration-tests
```

### API Testing with cURL

#### Test Normal Transaction

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-NORMAL-001",
    "amount": 50.00,
    "userId": "USER001",
    "merchantId": "MERCH001",
    "channel": "ONLINE"
  }'
```

#### Test High-Value Transaction (Should Trigger Fraud Alert)

```bash
curl -X POST http://localhost:8080/api/v1/transactions \
  -H "Content-Type: application/json" \
  -d '{
    "transactionId": "TXN-HIGH-001",
    "amount": 75000.00,
    "userId": "USER001",
    "merchantId": "MERCH001",
    "channel": "ONLINE",
    "timestamp": "2024-12-19T03:30:00.000Z"
  }'
```

### Load Testing

Use Apache JMeter or k6 for load testing:

```bash
# Example k6 script
k6 run load-test.js
```

---

## 🔧 Troubleshooting

### Common Issues

<details>
<summary><b>Docker disk space error</b></summary>

```bash
# Clean up Docker system
docker system prune -af --volumes
```
</details>

<details>
<summary><b>Kafka connection refused</b></summary>

```bash
# Restart Kafka and dependent services
docker-compose restart kafka
docker-compose restart ingestion-service fraud-engine notification-service
```
</details>

<details>
<summary><b>Database connection failed</b></summary>

```bash
# Check PostgreSQL container
docker logs postgres

# Verify database is ready
docker exec postgres pg_isready -U fraud_admin
```
</details>

<details>
<summary><b>ML Service not responding</b></summary>

```bash
# Check ML service logs
docker logs ml-model-service

# Verify model is loaded
curl http://localhost:8000/health
```
</details>

### Debug Mode

Enable debug logging:

```bash
# Set environment variable
export SPRING_PROFILES_ACTIVE=dev
docker-compose up -d
```

---

## 🤝 Contributing

We welcome contributions! Please follow these steps:

1. **Fork the repository**
2. **Create a feature branch**
   ```bash
   git checkout -b feature/amazing-feature
   ```
3. **Commit your changes**
   ```bash
   git commit -m 'Add amazing feature'
   ```
4. **Push to the branch**
   ```bash
   git push origin feature/amazing-feature
   ```
5. **Open a Pull Request**

### Coding Standards

- Java: Follow Google Java Style Guide
- Python: Follow PEP 8
- Commits: Use conventional commit messages

---

## �‍💻 Author

<div align="center">

### **Bhushan Asati**

*Software Engineer*

</div>

| | |
|:--|:--|
| 🔗 **GitHub** | [@bhushanasati25](https://github.com/bhushanasati25) |
| 📂 **Repository** | [Real-Time-Fraud-Detection-Service](https://github.com/bhushanasati25/Real-Time-Fraud-Detection-Service) |

---

## �📜 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

<div align="center">

### ⭐ Star this repository if you find it helpful!

**Developed with ❤️ by [Bhushan Asati](https://github.com/bhushanasati25)**

[![GitHub](https://img.shields.io/badge/GitHub-bhushanasati25-181717?style=for-the-badge&logo=github)](https://github.com/bhushanasati25)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-Connect-0A66C2?style=for-the-badge&logo=linkedin)](https://linkedin.com/in/bhushanasati25)

[Report Bug](https://github.com/bhushanasati25/Real-Time-Fraud-Detection-Service/issues) •
[Request Feature](https://github.com/bhushanasati25/Real-Time-Fraud-Detection-Service/issues)

</div>
