# Tasks: JobTrack

**Input**: Design documents from `/specs/001-jobtrack-app/`

**Prerequisites**: plan.md, spec.md, research.md, data-model.md, contracts/

## Phase 1: Setup (Shared Infrastructure)

**Purpose**: Create the initial backend/frontend project structure and toolchain.

- [X] T001 Create backend and frontend project structure in backend/ and frontend/
- [X] T002 Initialize Spring Boot 3.x and Java 21 backend dependencies in backend/pom.xml
- [X] T003 [P] Initialize Vue 3 and Vite frontend dependencies in frontend/package.json
- [X] T004 [P] Configure linting, formatting, and test tooling in backend/pom.xml and frontend/package.json

---

## Phase 2: Foundational (Blocking Prerequisites)

**Purpose**: Establish shared domain, persistence, security, and API infrastructure before story work begins.

- [X] T005 Create database configuration and environment profiles in backend/src/main/resources/application.yml
- [X] T006 Implement authentication and JWT security flow in backend/src/main/java/com/jobtrack/auth/
- [X] T007 [P] Implement shared domain entities, repositories, and validation in backend/src/main/java/com/jobtrack/
- [X] T008 [P] Set up API routing, DTOs, and global error handling in backend/src/main/java/com/jobtrack/common/
- [X] T009 Configure logging and observability hooks in backend/src/main/java/com/jobtrack/common/

**Checkpoint**: Foundation ready - user story implementation can now begin.

---

## Phase 3: User Story 1 - Create and maintain an application record (Priority: P1) 🎯 MVP

**Goal**: Allow a signed-in user to create, view, edit, archive, and delete application records.

**Independent Test**: A user can create a new application and immediately see it in the list with the expected details after refresh.

### Tests for User Story 1

- [X] T010 [P] [US1] Add backend integration tests for application create/edit flows in backend/src/test/java/com/jobtrack/applications/ApplicationApiTest.java
- [X] T011 [P] [US1] Add frontend interaction tests for the create/edit workflow in frontend/src/views/__tests__/ApplicationsView.test.ts

### Implementation for User Story 1

- [X] T012 [P] [US1] Implement Application and StageDefinition persistence models in backend/src/main/java/com/jobtrack/applications/ and backend/src/main/java/com/jobtrack/stages/
- [X] T013 [US1] Implement application service and validation rules in backend/src/main/java/com/jobtrack/applications/ApplicationService.java
- [X] T014 [US1] Implement application CRUD API endpoints in backend/src/main/java/com/jobtrack/applications/ApplicationController.java
- [X] T015 [US1] Implement application list and form UI in frontend/src/views/ApplicationListView.vue and frontend/src/components/application/ApplicationForm.vue
- [X] T016 [US1] Wire frontend API client for application CRUD in frontend/src/api/applications.ts

**Checkpoint**: User Story 1 should be fully functional and independently testable.

---

## Phase 4: User Story 2 - Move applications through the hiring funnel (Priority: P1)

**Goal**: Let users move applications between kanban stages and preserve stage metadata and notes.

**Independent Test**: A user can change an application stage and see the updated board and counts without losing the record details.

### Tests for User Story 2

- [ ] T017 [P] [US2] Add backend stage-transition tests in backend/src/test/java/com/jobtrack/stages/StageTransitionTest.java
- [ ] T018 [P] [US2] Add frontend drag-and-drop interaction tests in frontend/src/components/kanban/__tests__/KanbanBoard.test.ts

### Implementation for User Story 2

- [ ] T019 [P] [US2] Implement stage catalog and repository support in backend/src/main/java/com/jobtrack/stages/
- [ ] T020 [US2] Implement stage transition rules and terminal-stage handling in backend/src/main/java/com/jobtrack/stages/StageService.java
- [ ] T021 [US2] Implement the application stage update endpoint in backend/src/main/java/com/jobtrack/applications/ApplicationController.java
- [ ] T022 [US2] Build the kanban board UI and drag-and-drop handlers in frontend/src/components/kanban/KanbanBoard.vue
- [ ] T023 [US2] Wire stage update requests in frontend/src/services/kanban.ts

**Checkpoint**: User Stories 1 and 2 should both work independently.

---

## Phase 5: User Story 3 - Track follow-up and conversation context (Priority: P2)

**Goal**: Let users attach notes and next follow-up dates to each application and surface overdue reminders.

**Independent Test**: A user can add notes and a future follow-up date, and later see the reminder state reflected in the UI.

### Tests for User Story 3

- [ ] T024 [P] [US3] Add backend reminder and notes tests in backend/src/test/java/com/jobtrack/applications/ApplicationReminderTest.java
- [ ] T025 [P] [US3] Add frontend reminder UI tests in frontend/src/components/application/__tests__/ApplicationDetails.test.ts

### Implementation for User Story 3

- [ ] T026 [P] [US3] Extend application persistence and DTOs for notes and follow-up fields in backend/src/main/java/com/jobtrack/applications/
- [ ] T027 [US3] Implement reminder computation and overdue flag logic in backend/src/main/java/com/jobtrack/applications/ApplicationService.java
- [ ] T028 [US3] Expose notes and follow-up update handling through the application API in backend/src/main/java/com/jobtrack/applications/ApplicationController.java
- [ ] T029 [US3] Add notes and follow-up editor UI with overdue highlighting in frontend/src/components/application/ApplicationDetails.vue

**Checkpoint**: User Story 3 should be independently functional.

---

## Phase 6: User Story 4 - Review progress with board and statistics (Priority: P2)

**Goal**: Provide dashboard board summaries and statistics for funnel, activity, and conversion trends.

**Independent Test**: A user can open the dashboard and review stage counts and summary statistics without data errors.

### Tests for User Story 4

- [ ] T030 [P] [US4] Add backend statistics aggregation tests in backend/src/test/java/com/jobtrack/statistics/StatisticsServiceTest.java
- [ ] T031 [P] [US4] Add frontend statistics panel tests in frontend/src/components/statistics/__tests__/StatisticsPanel.test.ts

### Implementation for User Story 4

- [ ] T032 [P] [US4] Create statistics service and DTOs in backend/src/main/java/com/jobtrack/statistics/
- [ ] T033 [US4] Implement funnel, activity trend, and conversion aggregation in backend/src/main/java/com/jobtrack/statistics/StatisticsService.java
- [ ] T034 [US4] Implement the statistics API endpoint in backend/src/main/java/com/jobtrack/statistics/StatisticsController.java
- [ ] T035 [US4] Build dashboard statistics and summary UI in frontend/src/components/statistics/StatisticsPanel.vue and frontend/src/views/DashboardView.vue

**Checkpoint**: All user stories should now be independently functional.

---

## Phase 7: Polish & Cross-Cutting Concerns

**Purpose**: Finalize documentation, validation, and deployment readiness.

- [ ] T036 [P] Update README and feature quickstart guidance in README.md and specs/001-jobtrack-app/quickstart.md
- [ ] T037 [P] Refine error handling, logging, and resilience across backend services in backend/src/main/java/com/jobtrack/
- [ ] T038 Run end-to-end validation and smoke checks for the local workflow in backend/ and frontend/

---

## Dependencies & Execution Order

### Phase Dependencies

- **Setup (Phase 1)**: No dependencies - can start immediately
- **Foundational (Phase 2)**: Depends on Setup completion and blocks all user stories
- **User Stories (Phases 3-6)**: Depend on Foundational completion and can proceed in priority order
- **Polish (Phase 7)**: Depends on all desired user stories being complete

### User Story Dependencies

- **User Story 1 (P1)**: Can start after Foundational and has no dependency on other stories
- **User Story 2 (P1)**: Can start after Foundational and may integrate with US1 data contracts
- **User Story 3 (P2)**: Can start after Foundational and uses the application record created in US1
- **User Story 4 (P2)**: Can start after US1 and US2 are available and can consume shared application and stage data

### Parallel Opportunities

- Setup tasks T002-T004 can proceed in parallel
- Foundational tasks T007-T008 can proceed in parallel
- Tests for each story can be written in parallel with the implementation work for that story
- User Story 1, 2, 3, and 4 can be implemented in parallel by different contributors once the foundation is complete

## Parallel Example: User Story 1

```bash
# Backend and frontend work for the create/edit story can proceed in parallel:
# - Implement persistence and service logic in backend/src/main/java/com/jobtrack/applications/
# - Build list and form UI in frontend/src/views/ApplicationListView.vue
```

## Implementation Strategy

### MVP First (User Story 1 Only)

1. Complete Phase 1: Setup
2. Complete Phase 2: Foundational
3. Complete Phase 3: User Story 1
4. Validate the create/edit flow independently before expanding scope

### Incremental Delivery

1. Complete Setup + Foundational to establish the shared platform
2. Deliver User Story 1 for the MVP workflow
3. Add User Story 2 for stage movement
4. Add User Story 3 for follow-up context
5. Add User Story 4 for dashboard statistics
