# Research: JobTrack Implementation Decisions

## Decision: Use Spring Boot 3.x with Spring Data JPA as the backend foundation

**Rationale**: The specification calls for a database-backed web application, and Spring Boot provides a strong fit for layered REST services, JPA persistence, and Spring Security integration.

**Alternatives considered**: A Node.js/Express service or a serverless approach. Those would work, but they would add more framework diversity for a project that already targets Spring Boot and Java-based backend patterns.

## Decision: Use a separate Vue 3 SPA for the frontend

**Rationale**: The product requires a kanban board, dashboard statistics, and responsive workflows that are easier to structure in a dedicated SPA with clear view and service separation.

**Alternatives considered**: A server-rendered UI or a single monolithic frontend. A dedicated SPA is a better fit for the drag-and-drop board and API-driven experience.

## Decision: Use JWT-based authentication with username/password and BCrypt hashing

**Rationale**: This aligns with the clarification decision and supports a simple, secure login flow without introducing OAuth complexity in v1.

**Alternatives considered**: Session cookies or no authentication. JWT is a practical choice for a separate frontend/backend architecture and works well with API-based clients.

## Decision: Keep domain logic in the service layer, not in controllers

**Rationale**: The constitution requires data integrity and clean layering, so the controller layer should accept requests and delegate to services that enforce stage transitions, reminder evaluation, and statistics rules.

**Alternatives considered**: Performing all rules directly in the controllers. That would be harder to test and would increase coupling.

## Decision: Use PostgreSQL for deployment and H2 for local development/testing

**Rationale**: PostgreSQL matches the deployment target and production needs, while H2 provides a lightweight development and test profile without extra setup.

**Alternatives considered**: Using only H2 for all environments. That would be simpler but less representative of the deployed database behavior.
