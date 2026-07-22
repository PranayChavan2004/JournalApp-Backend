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

## 👤 Author

**Pranay Chavan**

---

