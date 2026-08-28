# Quickstart: JobTrack

## Prerequisites

- Docker Desktop or Docker Engine
- Java 21
- Node.js 20+
- A terminal with access to the repository

## 1. Start the database

Run PostgreSQL locally with Docker:

```bash
docker run --name jobtrack-postgres -e POSTGRES_DB=jobtrack -e POSTGRES_USER=jobtrack -e POSTGRES_PASSWORD=jobtrack -p 5432:5432 -d postgres:16
```

## 2. Start the backend

```bash
cd backend
./mvnw spring-boot:run
```

Expected result: the backend starts on `http://localhost:8080` and exposes the REST API.

## 3. Start the frontend

```bash
cd frontend
npm install
npm run dev
```

Expected result: the SPA starts on `http://localhost:5173`.

## 4. Validate the main flow

1. Open the frontend in the browser.
2. Register a user with username/password.
3. Create a sample application.
4. Move the application through a few stages.
5. Confirm the board and statistics reflect the new state.

## 5. Run backend tests

```bash
cd backend
./mvnw test
```

Expected result: stage transition and statistics logic tests pass.
