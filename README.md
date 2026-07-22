# 📔 JournalApp — Secure Journaling Backend with Sentiment Insights

A production-style **Spring Boot REST API** that lets users maintain a private, authenticated journal, receive weekly AI-driven sentiment summaries by email, and check the weather for their city — all backed by MySQL, Redis, Kafka, and JWT-secured Spring Security.

Built as a hands-on deep dive into real-world backend engineering patterns: layered architecture, token-based auth, event-driven messaging, caching, and scheduled background jobs.

---

## 🚀 Features

- **JWT Authentication & Role-Based Authorization**
  Stateless login/signup with Spring Security, `USER` / `ADMIN` roles, and a custom `JwtFilter` that validates bearer tokens on every request.
- **Google OAuth2 Login**
  Sign in with a Google account; new users are auto-provisioned and issued a JWT on successful callback.
- **Personal Journal CRUD**
  Create, read, update, and delete journal entries — scoped strictly to the authenticated user via Spring Security's `SecurityContext` (no user can see another user's entries).
- **Weekly Sentiment Analysis Digest**
  A `@Scheduled` cron job runs every Sunday at 9 AM, aggregates each user's dominant mood from the past 7 days of entries, and publishes the result to a **Kafka** topic (`weekly-sentiments`), which is consumed and emailed to the user — with a direct-email fallback if Kafka is unavailable.
- **Live Weather Lookup**
  Integrates a third-party Weather API, with responses cached in **Redis** (5-minute TTL) to reduce external calls and latency.
- **Dynamic Config Cache**
  App-level configuration (like the weather API endpoint template) is stored in MySQL and loaded into an in-memory cache at startup / on a 10-minute refresh cycle — no redeploy needed to change config.
- **API Documentation**
  Interactive Swagger / OpenAPI UI with bearer-token auth support baked in.
- **Environment-Based Profiles**
  Separate `dev`, `test`, and `prod` configuration profiles for clean environment separation.

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security, JWT (`jjwt`), Google OAuth2 |
| Persistence | Spring Data JPA, MySQL |
| Caching | Redis (Spring Data Redis) |
| Messaging | Apache Kafka (Spring Kafka) |
| Mail | Spring Boot Starter Mail (SMTP) |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Build Tool | Maven |
| Utilities | Lombok, ModelMapper |

---

## 🏗️ Architecture Overview

\```
Client
  │
  ▼
Controller Layer  ──►  JwtFilter (validates token, sets SecurityContext)
  │
  ▼
Service Layer  ──►  UserService / JournalEntryService / WeatherService / EmailService
  │
  ├──► Repository Layer (Spring Data JPA)  ──►  MySQL
  ├──► RedisService  ──►  Redis (weather cache)
  └──► UserScheduler  ──►  Kafka Producer ──► SentimentConsumerService ──► EmailService
\```

**Key packages:**

\```
com.projectbyPranayChavan.JournalApp
├── controller     # REST endpoints (Public, User, Journal, Admin, GoogleAuth)
├── service        # Business logic (User, JournalEntry, Weather, Email, Redis, Kafka consumer)
├── repository      # Spring Data JPA repositories
├── entities        # JPA entities (User, JournalEntry, Config)
├── dto             # Data transfer objects
├── config          # Security, Redis, Swagger configuration
├── filter          # JWT authentication filter
├── utils           # JWT generation/validation utilities
├── scheduler       # Cron jobs (weekly sentiment digest, cache refresh)
├── cache           # In-memory app-level config cache
├── model           # Kafka message payloads
├── enums           # Sentiment enum
└── api/response    # External API response DTOs (Weather)
\```

---

## 📡 API Endpoints

### Public (no auth required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/public/health-check` | Health check |
| POST | `/public/signup` | Register a new user |
| POST | `/public/login` | Authenticate and receive a JWT |
| GET | `/auth/google/callback` | Google OAuth2 login callback |

### User (JWT required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/user` | List all users |
| GET | `/user/{id}` | Get user by ID |
| GET | `/user/{username}` | Get user by username |
| PUT | `/user` | Update the authenticated user's profile |
| DELETE | `/user` | Delete the authenticated user's account |
| GET | `/user/weather` | Get a weather-based greeting for the current user |

### Journal (JWT required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/journal` | Get all journal entries for the authenticated user |
| POST | `/journal` | Create a new journal entry |
| GET | `/journal/id/{myid}` | Get a specific entry by ID |
| PUT | `/journal/{journalId}` | Update an existing entry |
| DELETE | `/journal/{id}` | Delete an entry |

### Admin (`ROLE_ADMIN` required)

| Method | Endpoint | Description |
|---|---|---|
| GET | `/admin/all-users` | List all users |
| POST | `/admin/create-admin-user` | Create a new admin user |
| GET | `/admin/clear-app-cache` | Force-refresh the in-memory config cache |

> Full interactive documentation is available via Swagger UI at `/swagger-ui.html` once the app is running.

---

## ⚙️ Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- MySQL 8+
- Redis (local or cloud)
- Apache Kafka (optional — falls back to direct email if unavailable)

### Environment Variables

This project reads several secrets from the environment rather than hardcoding them. Set the following before running:

| Variable | Purpose |
|---|---|
| `SERVER_PORT` | Application server port |
| `KAFKA_SERVERS` | Kafka bootstrap servers |
| `REDIS_HOST` | Redis host |
| `REDIS_PASSWORD` | Redis password |
| `JAVA_EMAIL_PASSWORD` | SMTP app password for the mail sender |
| `WEATHER_API_KEY` | API key for the weather provider |

### Run Locally

\```bash
# Clone the repo
git clone https://github.com/<your-username>/JournalApp.git
cd JournalApp

# Set the active profile (dev by default)
export SPRING_PROFILES_ACTIVE=dev

# Build and run
./mvnw clean install
./mvnw spring-boot:run
\```

The app starts on the port defined in `application-dev.yaml` under context path `/journal`.

---

## 🧪 Testing

\```bash
./mvnw test
\```

---

## 🗺️ Roadmap / Possible Improvements

- Externalize the JWT signing secret via environment variable instead of a hardcoded constant
- Add refresh-token support (current tokens are short-lived with no renewal flow)
- Replace manual `Optional`/null checks with a global `@ControllerAdvice` exception handler
- Add integration tests for controllers and Kafka consumer flow
- Dockerize the application with a `docker-compose.yml` for MySQL + Redis + Kafka

---

## 👤 Author

**Pranay Chavan**
Project package: `com.projectbyPranayChavan.JournalApp`

---

## 📄 License

This project is open source. Add a `LICENSE` file (MIT recommended) to formally license it before publishing.
