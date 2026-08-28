# Implementation Plan: JobTrack

**Branch**: `001-jobtrack-app` | **Date**: 2026-07-28 | **Spec**: [spec.md](spec.md)

**Input**: Feature specification from `/specs/001-jobtrack-app/spec.md`

## Summary

Build a single-user job application tracker as a web application with a Spring Boot 3.x backend and a Vue 3 single-page frontend. The MVP will support authentication, application CRUD, stage transitions, a kanban workflow, follow-up reminders, and statistics while keeping the implementation simple and aligned with the repository constitution.

## Technical Context

**Language/Version**: Java 21 for the backend; TypeScript with Vue 3 for the frontend

**Primary Dependencies**: Spring Boot 3.x, Spring Data JPA, Spring Security, JWT, PostgreSQL, H2, Vue 3, Vite, Axios

**Storage**: PostgreSQL for deployment; H2 in-memory for local development and automated tests

**Testing**: JUnit 5 and Spring Boot Test for backend unit and integration tests; Vitest and Vue Test Utils for focused frontend interaction tests

**Target Platform**: Dockerized web application deployable to Render or Railway

**Project Type**: Web application with separate frontend and backend services

**Performance Goals**: Dashboard and board views should render in under 2 seconds for dozens of applications on a typical laptop

**Constraints**: Single-user v1, no multi-tenant or shared-account behavior, username/password authentication with BCrypt hashing, layered backend architecture, and preservation of core workflow semantics

**Scale/Scope**: Personal use, one authenticated user, a few dozen applications, and a simple deployment target for demos

## Constitution Check

*GATE: Must pass before Phase 0 research. Re-check after Phase 1 design.*

- Spec-first: PASS — the feature has a populated specification with user stories, requirements, and acceptance scenarios.
- Workflow integrity: PASS — the plan preserves the core application workflow of creation, stage movement, follow-up tracking, and statistics.
- Test-first: PASS — the plan explicitly calls for backend tests around stage transitions and statistics calculations before implementation.
- Data integrity: PASS — the plan includes persistence, validation, and domain rules for applications and stages.
- Operability: PASS — the plan includes logging/error handling and a quickstart path for local setup and deployment.

## Project Structure

### Documentation (this feature)

```text
specs/001-jobtrack-app/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/
└── tasks.md
```

### Source Code (repository root)

```text
backend/
├── src/
│   ├── main/java/com/jobtrack/
│   │   ├── auth/
│   │   ├── applications/
│   │   ├── stages/
│   │   ├── common/
│   │   └── config/
│   ├── main/resources/
│   └── test/java/com/jobtrack/
└── Dockerfile

frontend/
├── src/
│   ├── components/
│   │   ├── kanban/
│   │   └── statistics/
│   ├── views/
│   ├── services/
│   └── api/
├── public/
└── Dockerfile
```

**Structure Decision**: Use a split backend/frontend layout to keep the REST API, domain logic, and UI state management independently organized while preserving a simple deployment model.

## Complexity Tracking

No constitution violations require special justification for this feature.
