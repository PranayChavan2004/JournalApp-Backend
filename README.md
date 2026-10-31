# JournalApp — Secure Journaling App with Mood Insights

A production-style **Spring Boot** application that lets users keep a private, authenticated journal, tag every entry with a mood, receive a weekly mood summary by email and see the weather for their city. It ships with its own responsive web interface and is backed by MySQL, Redis, Kafka and JWT-secured Spring Security.

Built as a hands-on deep dive into real-world backend engineering patterns: layered architecture, token-based auth, OAuth2 login, event-driven messaging, caching, and scheduled background jobs.

---

## Features

- **JWT Authentication** — stateless login/signup with `USER` / `ADMIN` roles
- **Google OAuth2 Login** — sign in with Google, new users auto-created
- **Personal Journal CRUD** — entries are private to each user
- **Mood Tracking** — tag entries as happy, sad, angry or anxious, with a 30-day mood ribbon
- **Weekly Sentiment Digest** — Sunday cron job sends each user's dominant mood by email via **Kafka**, with a direct-email fallback
- **Weather Lookup** — third-party Weather API with **Redis** caching
- **Dynamic Config Cache** — app config stored in MySQL and refreshed automatically
- **Built-in Web UI** — responsive single-page interface with light and dark mode
- **Account & Admin Tools** — change password, delete account, list users
- **Swagger API Docs** — interactive docs with bearer-token support
- **Environment Profiles** — separate `dev`, `test` and `prod` configs

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security, JWT (`jjwt`), Google OAuth2 / OpenID Connect |
| Persistence | Spring Data JPA, MySQL |
| Caching | Redis (Spring Data Redis) |
| Messaging | Apache Kafka (Spring Kafka) |
| Mail | Spring Boot Starter Mail (SMTP) |
| Validation | Jakarta Bean Validation |
| API Docs | SpringDoc OpenAPI (Swagger UI) |
| Frontend | HTML, CSS, vanilla JavaScript (ES modules) |
| Build Tool | Maven |
| DevOps | Docker, Docker Compose |
| Utilities | Lombok |

---

## Architecture Overview

```
Browser UI / API Client
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

Google Sign-In:  Browser ──► Google ──► OAuth2LoginSuccessHandler ──► JWT cookie
```

---

## Project Structure

```
JournalApp/
├── mvnw, mvnw.cmd                     # Maven wrapper scripts
├── pom.xml                            # Project dependencies & build config
├── Dockerfile                         # Container image
├── docker-compose.yml                 # App + MySQL + Redis
├── .env.example                       # Environment variable template
│
├── src/
│   ├── main/
│   │   ├── java/com/projectbyPranayChavan/JournalApp/
│   │   │   ├── JournalAppApplication.java     # Main entry point
│   │   │   │
│   │   │   ├── api/response/
│   │   │   │   └── WeatherResponse.java       # External weather API DTO
│   │   │   │
│   │   │   ├── cache/
│   │   │   │   └── AppCache.java              # In-memory config cache
│   │   │   │
│   │   │   ├── config/
│   │   │   │   ├── AdminBootstrap.java        # Creates the first admin on startup
│   │   │   │   ├── HttpClientConfig.java      # RestTemplate with timeouts
│   │   │   │   ├── KafkaTopicConfig.java      # Creates the Kafka topic
│   │   │   │   ├── RedisConfig.java           # Redis bean configuration
│   │   │   │   ├── SpringSecurity.java        # Security filter chain, OAuth2, CORS, headers
│   │   │   │   └── SwaggerConfig.java         # OpenAPI / Swagger setup
│   │   │   │
│   │   │   ├── constants/
│   │   │   │   └── Placeholders.java          # Static placeholder strings
│   │   │   │
│   │   │   ├── controller/
│   │   │   │   ├── AdminController.java       # Admin-only endpoints
│   │   │   │   ├── JournalController.java     # Journal CRUD (auth required)
│   │   │   │   ├── PublicController.java      # Signup, login, logout, health-check
│   │   │   │   └── UserController.java        # Profile, account & weather endpoints
│   │   │   │
│   │   │   ├── dto/                           # Request / response payloads
│   │   │   │   ├── ApiError.java
│   │   │   │   ├── AuthResponse.java
│   │   │   │   ├── JournalEntryRequest.java
│   │   │   │   ├── JournalEntryResponse.java
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── UpdateUserRequest.java
│   │   │   │   ├── UserDTO.java
│   │   │   │   ├── UserResponse.java
│   │   │   │   └── WeatherView.java
│   │   │   │
│   │   │   ├── entities/
│   │   │   │   ├── ConfigJournalAppEntity.java # App config key-value table
│   │   │   │   ├── JournalEntry.java           # Journal entry entity
│   │   │   │   └── User.java                   # User entity
│   │   │   │
│   │   │   ├── enums/
│   │   │   │   ├── AuthProvider.java           # LOCAL / GOOGLE
│   │   │   │   └── Sentiment.java              # HAPPY / SAD / ANGRY / ANXIOUS
│   │   │   │
│   │   │   ├── exception/
│   │   │   │   ├── ApiException.java
│   │   │   │   └── GlobalExceptionHandler.java # Uniform error responses
│   │   │   │
│   │   │   ├── filter/
│   │   │   │   └── JwtFilter.java              # Validates JWT on each request
│   │   │   │
│   │   │   ├── model/
│   │   │   │   └── SentimentData.java          # Kafka message payload
│   │   │   │
│   │   │   ├── repository/
│   │   │   │   ├── ConfigJournalAppRepository.java
│   │   │   │   ├── JournalEntryRepository.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── UserRepositoryImpl.java     # Custom query implementation
│   │   │   │
│   │   │   ├── scheduler/
│   │   │   │   └── UserScheduler.java          # Weekly sentiment cron + cache refresh
│   │   │   │
│   │   │   ├── security/
│   │   │   │   ├── AuthCookieService.java      # HttpOnly JWT cookie handling
│   │   │   │   ├── LoginAttemptService.java    # Login rate limiting
│   │   │   │   ├── OAuth2LoginFailureHandler.java
│   │   │   │   └── OAuth2LoginSuccessHandler.java # Google callback -> JWT
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── EmailService.java           # SMTP email sending
│   │   │   │   ├── JournalEntryService.java    # Journal business logic
│   │   │   │   ├── RedisService.java           # Generic Redis get/set helper
│   │   │   │   ├── SentimentConsumerService.java # Kafka listener
│   │   │   │   ├── UserDetailsServiceImpl.java # Spring Security UserDetailsService
│   │   │   │   ├── UserService.java            # User business logic
│   │   │   │   └── WeatherService.java         # Weather API + Redis cache lookup
│   │   │   │
│   │   │   └── utils/
│   │   │       └── JwtUtils.java               # JWT generation & validation
│   │   │
│   │   └── resources/
│   │       ├── application.yaml               # Base config (shared settings)
│   │       ├── application-dev.yaml           # Dev profile
│   │       ├── application-test.yaml          # Test profile
│   │       ├── application-prod.yaml          # Production profile
│   │       ├── logback-spring.xml             # Logging configuration
│   │       └── static/                        # Web interface
│   │           ├── index.html
│   │           ├── css/app.css
│   │           ├── js/ (api.js, ui.js, app.js)
│   │           └── fonts/
│   │
│   └── test/
│       └── java/com/projectbyPranayChavan/JournalApp/
│           ├── JournalAppApplicationTests.java
│           ├── security/LoginAttemptServiceTest.java
│           └── utils/JwtUtilsTest.java
│
├── .gitignore
└── README.md
```

---

## Author

**Pranay Chavan**

---
