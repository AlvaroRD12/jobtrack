# JobTrack

A personal job application tracker with a kanban board, stage tracking,
follow-up reminders, and a statistics dashboard. Built with Spring Boot +
JPA on the backend and Vue 3 on the frontend, developed using
[Spec-Driven Development](https://github.com/github/spec-kit) with
GitHub Spec Kit.

See [`PROCESS.md`](./PROCESS.md) for a detailed account of how this was
built — including the real bugs found along the way and how each one was
diagnosed and fixed.

## Live demo

- App: https://jobtrack-frontend-n60i.onrender.com
- Backend health check: https://jobtrack-77np.onrender.com/actuator/health

The backend runs on Render's free tier and spins down after 15 minutes of
inactivity — the first request may take 30-60 seconds to respond while it
wakes up.

Try it with the demo account:
- Username: `demo`
- Password: `demo1234`

There's no registration form in the UI yet (see [Known limitations](#known-limitations)),
so this shared demo account is the quickest way to explore the app
without using the API directly.

## Features

- **Application tracking**: create, edit, archive, and delete job
  applications, each scoped to its owner — no user can see or modify
  another user's data.
- **Kanban board**: move applications between stages (Applied → In
  progress → Interview → Offer / Rejected / Withdrawn) via drag and drop.
- **Follow-up reminders**: attach notes and a next follow-up date to any
  application, with automatic overdue highlighting.
- **Statistics dashboard**: funnel counts by stage, application activity
  over time, and stage-to-stage conversion rates.
- **JWT-based authentication**: username/password login with hashed
  passwords and signed tokens.

## Tech stack

- **Backend**: Spring Boot 3, Spring Data JPA, Spring Security, JJWT,
  H2 (in-memory, for local development).
- **Frontend**: Vue 3 (Composition API), Vue Router, Axios, Vite.
- **Testing**: JUnit 5 / Spring Boot Test on the backend, Vitest / Vue
  Test Utils on the frontend.

## Getting started

### Prerequisites

- Java 21
- Maven
- Node.js 20+

### Backend

```bash
cd backend
mvn spring-boot:run
```

The API starts on `http://localhost:8080`. It uses an in-memory H2
database, so data resets on every restart.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

The app starts on `http://localhost:5173`. In development, API requests
to `/api/*` are proxied to the backend.

### Using the app

1. Register a user via `POST /api/auth/register` (no registration form
   exists in the UI yet — see [Known limitations](#known-limitations)).
2. Log in through the UI with those credentials.
3. Use the **Applications** page to create and manage applications, and
   the **Dashboard** page for statistics.

## Running tests

```bash
# Backend
cd backend
mvn clean test

# Frontend
cd frontend
npm test
```

## Project structure

```
backend/
  src/main/java/com/jobtrack/
    auth/           # Authentication, JWT, user management
    applications/    # Application CRUD and ownership rules
    stages/         # Stage catalog and transitions
    statistics/     # Funnel, activity, and conversion aggregation
    common/         # Shared response wrapper, exception handling
frontend/
  src/
    api/            # HTTP clients per domain
    components/     # Kanban board, statistics panel
    views/          # Page-level components
    router/         # Route definitions
specs/001-jobtrack-app/
  spec.md           # Feature specification and user stories
  plan.md           # Technical plan and architecture
  data-model.md     # Entity definitions and relationships
  tasks.md          # Task breakdown by user story
```

## Known limitations

- No registration form in the UI yet — new users must be created via the
  `/api/auth/register` endpoint directly.
- Minor visual polish still pending (see `PROCESS.md` for the running
  list of non-blocking issues found during development).
- `npm audit` on the frontend reports 5 vulnerabilities (1 critical, 1
  high, 3 moderate) in `vite`/`vitest`/`esbuild`. These are dev-only
  dependencies never included in the production build (`dist/`), and the
  specific advisories require either the dev server to be exposed
  externally or the Vitest UI (`vitest --ui`, not used in this project)
  to be running — neither applies here. Fixing them requires a major
  version bump (`vite@8`, `vitest@4`) with breaking changes, deliberately
  deferred rather than applied under time pressure.

## License

MIT — see [`LICENSE`](./LICENSE).