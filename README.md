# JM-Gateway

API Gateway for the Job Manager microservices platform.

## Overview

JM-Gateway is the single entry point for all client requests to the Job Manager platform. It provides request routing, load balancing, authentication, rate limiting, and cross-cutting concerns for all microservices.

## Features

- **Request Routing**: Route requests to appropriate microservices
- **Load Balancing**: Client-side load balancing with Eureka
- **Authentication**: JWT token validation for secured endpoints
- **Rate Limiting**: Prevent API abuse with request throttling
- **CORS Handling**: Cross-Origin Resource Sharing configuration
- **Request/Response Logging**: Centralized logging for monitoring
- **Circuit Breaker**: Fault tolerance with fallback responses
- **Request Filtering**: Pre and post request processing
- **Path Rewriting**: Modify request paths before forwarding

## Tech Stack

- **Java 17+**
- **Spring Boot 3.x**
- **Spring Cloud Gateway**: Reactive gateway framework
- **Spring Cloud Netflix Eureka Client**: Service discovery
- **Spring Security**: Authentication and authorization
- **Resilience4j**: Circuit breaker and rate limiting
- **Redis**: Rate limiting storage (optional)

## Prerequisites

- Java 17 or higher
- Maven 3.8+
- Eureka Server running (JM-Eureka)
- Redis (optional, for distributed rate limiting)
- All microservices registered with Eureka

## Gateway Routes Overview

| Path                    | Service              | Description                |
| ----------------------- | -------------------- | -------------------------- |
| `/api/auth/**`          | Auth Service         | Authentication endpoints   |
| `/api/profiles/**`      | Profile Service      | Company profiles           |
| `/api/subscriptions/**` | Subscription Service | Subscription management    |
| `/api/payments/**`      | Payment Service      | Payment processing         |
| `/api/jobs/**`          | Job Post Service     | Job postings               |
| `/api/discovery/**`     | Discovery Service    | Talent discovery (Premium) |
| `/api/notifications/**` | Notification Service | Notifications              |
| `/api/skills/**`        | Skill Tag Service    | Skill tags                 |
| `/api/media/**`         | Media Service        | File uploads               |

## Public Endpoints (No Auth Required)

```
/api/auth/login
/api/auth/register
/api/public/**
/actuator/health
```
