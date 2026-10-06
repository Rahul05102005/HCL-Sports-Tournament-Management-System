# SportX – Smart Sports Tournament Management Platform
**HCL Java Full Stack Hackathon Master Capstone Project**

SportX is a comprehensive, production-quality sports tournament management platform designed specifically to demonstrate every technical competency outlined in the HCL assessment rubrics. The system covers Core Java 21 features, ACID database design, Spring Boot 3.3 REST architecture, JWT RBAC security with ownership validation, and a React + TypeScript frontend with Redux Toolkit and Tailwind CSS.

---

## 🏆 HCL Rubrics & Competency Coverage Matrix

| Area | Competencies Demonstrated | Implementation Location |
| :--- | :--- | :--- |
| **Core Java 21** | OOP, Inheritance, Sealed Classes, Records, Generics, Streams, Optionals, Modern Date/Time | [`core-java/src/main/java/com/sportx/core`](file:///c:/Users/prahu/OneDrive/Desktop/Sports%20tournament/core-java/src/main/java/com/sportx/core) |
| **Database Design** | 3NF Normalization, Constraints, Foreign Keys, Triggers | [`database/schema.sql`](file:///c:/Users/prahu/OneDrive/Desktop/Sports%20tournament/database/schema.sql) |
| **SQL Mastery** | Joins, Aggregation, HAVING, Subqueries, 10 Business Queries | [`database/queries.sql`](file:///c:/Users/prahu/OneDrive/Desktop/Sports%20tournament/database/queries.sql) |
| **Performance & ACID** | Multi-table Transactions, Rollback demo, Indexes & EXPLAIN Plan | [`database/transactions.sql`](file:///c:/Users/prahu/OneDrive/Desktop/Sports%20tournament/database/transactions.sql), [`database/EXPLAIN.md`](file:///c:/Users/prahu/OneDrive/Desktop/Sports%20tournament/database/EXPLAIN.md) |
| **Spring Boot 3.3** | REST Controllers, JPA/Hibernate, `@Transactional`, Bean Validation | [`backend/src/main/java/com/sportx/backend`](file:///c:/Users/prahu/OneDrive/Desktop/Sports%20tournament/backend/src/main/java/com/sportx/backend) |
| **Security & RBAC** | JWT Auth, Refresh Tokens, Role-Based Access Control, SpEL Ownership Checks | [`backend/src/main/java/com/sportx/backend/security`](file:///c:/Users/prahu/OneDrive/Desktop/Sports%20tournament/backend/src/main/java/com/sportx/backend/security) |
| **React + TypeScript** | React 18, Redux Toolkit, React Router, React Hook Form, Zod, Tailwind CSS | [`frontend/src`](file:///c:/Users/prahu/OneDrive/Desktop/Sports%20tournament/frontend/src) |
| **Live Pulse Engine** | Tournament Pulse dashboard, Live match score ticker, Standing points engine | [`frontend/src/pages/DashboardPage.tsx`](file:///c:/Users/prahu/OneDrive/Desktop/Sports%20tournament/frontend/src/pages/DashboardPage.tsx) |

---

## 🚀 Quick Start Guide

### Prerequisites
- **JDK 21** (Configured in `C:\Users\prahu\.tools\jdk-21.0.6+7`)
- **Apache Maven 3.9+** (Configured in `C:\Users\prahu\.tools\apache-maven-3.9.6`)
- **Node.js 20+ & npm 10+** (Available on PATH)

---

### 1. Run the Spring Boot Backend (Port 8080)

```powershell
cd "c:\Users\prahu\OneDrive\Desktop\Sports tournament\backend"
$env:JAVA_HOME = "C:\Users\prahu\.tools\jdk-21.0.6+7"
$env:M2_HOME = "C:\Users\prahu\.tools\apache-maven-3.9.6"
$env:Path = "$env:JAVA_HOME\bin;$env:M2_HOME\bin;" + $env:Path

mvn spring-boot:run
```

The backend starts with the **`dev`** profile using an in-memory H2 database with automatic schema creation and Indian collegiate demo data seeding.
- **REST API Base URL**: `http://localhost:8080/api/v1`
- **Swagger UI / OpenAPI Documentation**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON Spec**: `http://localhost:8080/v3/api-docs`
- **H2 Web Console**: `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:mem:sportxdb`, User: `sa`, Password: *empty*)

---

### 2. Run the React Frontend (Port 5173)

```powershell
cd "c:\Users\prahu\OneDrive\Desktop\Sports tournament\frontend"
npm run dev
```

Open `http://localhost:5173` in your browser.

---

## 🔑 Demo User Credentials

The database is pre-seeded with accounts covering all RBAC roles. You can also use the **One-Click Role Demo Sign-in** buttons on the Login page:

| Role | Username | Password | Permissions |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `password123` | Full access across tournaments, teams, fixtures, and scores |
| **ORGANIZER** | `organizer1` | `password123` | Create tournaments, generate smart round-robin fixtures, manage teams |
| **REFEREE** | `referee1` | `password123` | Record live score events (goals, cards) and finalize matches |
| **PLAYER** | `player1` | `password123` | Register collegiate teams and view personal squad eligibility |

---

## 🏟️ Pre-Seeded Real-World Tournament Data

- **Tournament**: Tamil Nadu Inter-Engineering Super Cup 2026 (Football)
- **Format**: Round Robin with automatic standing points (Win: 3, Draw: 1, Loss: 0)
- **Participating Colleges & Teams**:
  - `BIT Thunderbolts` – Bannari Amman Institute of Technology, Sathyamangalam
  - `PSG Strikers` – PSG College of Technology, Coimbatore
  - `CIT Spartans` – Coimbatore Institute of Technology
  - `KEC Warriors` – Kongu Engineering College, Perundurai
- **Live Match**: PSG Strikers vs KEC Warriors (Currently in 72nd minute)
- **Completed Match**: BIT Thunderbolts 2 - 1 CIT Spartans (Standings updated)

---

## 🧪 Running Automated Test Suites

### 1. Core Java Unit Tests (JUnit 5)
```powershell
cd "c:\Users\prahu\OneDrive\Desktop\Sports tournament\core-java"
mvn test
```
*Executes 9 unit tests verifying sealed classes, record calculations, generic repositories, squad size validations, and smart conflict detection.*

### 2. Spring Boot Integration Tests (MockMvc & JPA)
```powershell
cd "c:\Users\prahu\OneDrive\Desktop\Sports tournament\backend"
mvn test
```
*Executes 6 tests verifying JWT authentication, bad credentials rejection, new user registration, live matches retrieval, and ACID match finalization.*

### 3. Frontend TypeScript & Production Build
```powershell
cd "c:\Users\prahu\OneDrive\Desktop\Sports tournament\frontend"
npm run build
```
*Validates type safety with `tsc` and generates minified production bundle in `dist/`.*

---

## 📁 Repository Structure

```
Sports tournament/
├── core-java/               # Pure Java 21 Domain Implementation
│   ├── src/main/java/com/sportx/core/
│   │   ├── domain/          # OOP hierarchy (Person, Player, Referee, Team, Fixture, etc.)
│   │   ├── model/           # Records & Sealed Classes (MatchResult, StandingRecord)
│   │   ├── repository/      # Generic Repository<T, ID> & InMemoryRepository
│   │   ├── service/         # SchedulingService (Smart conflict detection)
│   │   └── exceptions/      # Custom domain exceptions
│   └── src/test/java/       # JUnit 5 test suite
│
├── database/                # Relational Database Artifacts
│   ├── schema.sql           # 3NF DDL schema with constraints & foreign keys
│   ├── seed.sql             # Realistic Indian collegiate seed dataset (20+ rows per table)
│   ├── queries.sql          # 10 business analytical SQL queries
│   ├── view.sql             # tournament_summary_view definition
│   ├── indexes.sql          # Performance index definitions & justifications
│   ├── transactions.sql     # Multi-table ACID transaction & rollback demo
│   └── EXPLAIN.md           # Execution plan benchmarks & tradeoff analysis
│
├── backend/                 # Spring Boot 3.3.4 + Java 21 REST API
│   ├── src/main/java/com/sportx/backend/
│   │   ├── config/          # SecurityConfig, OpenApiConfig, DataSeeder
│   │   ├── security/        # JwtTokenProvider, JwtAuthFilter, SecurityEvaluator
│   │   ├── entity/          # 12 JPA entities (User, Tournament, Team, Match, etc.)
│   │   ├── repository/      # Spring Data JPA repositories with custom JPQL
│   │   ├── dto/             # Request/Response payloads with Bean Validation
│   │   ├── service/         # AuthService, TournamentService, MatchService, StandingService
│   │   ├── controller/      # Auth, Tournament, Team, Match, Standings REST endpoints
│   │   └── exception/       # GlobalExceptionHandler with RFC-compliant error schemas
│   └── src/test/java/       # MockMvc and Service test suites
│
└── frontend/                # React 18 + TypeScript + Tailwind CSS SPA
    ├── src/
    │   ├── api/             # Centralized Axios client with JWT refresh interceptor
    │   ├── store/           # Redux Toolkit store (authSlice, tournamentSlice, matchSlice)
    │   ├── components/      # Navbar, LiveTicker, ScoreModal, FinalizeModal, etc.
    │   ├── pages/           # DashboardPage, MatchesPage, StandingsPage, TeamsPage, Auth
    │   └── types/           # TypeScript interfaces matching backend models
    └── package.json         # React 18, Redux Toolkit, Lucide, Zod dependencies
```
