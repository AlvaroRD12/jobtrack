# Feature Specification: JobTrack

**Feature Branch**: `001-jobtrack-app`

**Created**: 2026-07-28

**Status**: Draft

**Input**: User description: "JobTrack is a personal web application for tracking job applications..."

## Clarifications

### Session 2026-07-28

- Q: Which authentication approach should v1 use for JobTrack? → A: Username/password

## User Scenarios & Testing *(mandatory)*

### User Story 1 - Create and maintain an application record (Priority: P1)

A job seeker can create a centralized record for each application with the company, position, source, and application date so that all active opportunities are visible in one place.

**Why this priority**: This is the foundation of the product and enables every later workflow.

**Independent Test**: A user can create a new application and see it listed immediately with the expected details.

**Acceptance Scenarios**:

1. **Given** a signed-in user is viewing the dashboard, **When** they create a new application with company, position, source, and date, **Then** the application appears in the list with those values preserved.
2. **Given** an application already exists, **When** the user edits its basic details, **Then** the updated values are shown in the record and retained after refresh.

---

### User Story 2 - Move applications through the hiring funnel (Priority: P1)

A user can move each application between stages so they can understand progress and see where each opportunity stands at a glance.

**Why this priority**: Stage movement is the core workflow and the basis for the funnel view and statistics.

**Independent Test**: A user can change an application stage and see the updated board and counts.

**Acceptance Scenarios**:

1. **Given** an application exists in the Applied stage, **When** the user moves it to Interview, **Then** it is displayed under Interview and its stage is updated consistently in the data.
2. **Given** a user is on the kanban view, **When** they drag an application to a different stage, **Then** the application appears in the new column without losing notes or metadata.

---

### User Story 3 - Track follow-up and conversation context (Priority: P2)

A user can add notes and set a next follow-up date for each application so important details and reminders do not get lost.

**Why this priority**: This reduces missed opportunities and improves the usefulness of the tracking workflow.

**Independent Test**: A user can add a note and follow-up date to an application and later review them.

**Acceptance Scenarios**:

1. **Given** an application has been created, **When** the user adds notes and a future follow-up date, **Then** the notes and reminder are attached to that application.
2. **Given** a follow-up date has passed, **When** the user opens the dashboard, **Then** the application is clearly highlighted as overdue for follow-up.

---

### User Story 4 - Review progress with board and statistics (Priority: P2)

A user can see a visual overview of their pipeline and review summary statistics that help them understand search momentum and response quality.

**Why this priority**: This turns raw records into actionable insight and makes the tool useful beyond simple tracking.

**Independent Test**: A user can open the dashboard and review stage counts and simple statistics without needing a manual report.

**Acceptance Scenarios**:

1. **Given** multiple applications exist across several stages, **When** the user opens the dashboard, **Then** the board shows each application grouped by stage and the funnel summary reflects the current counts.
2. **Given** applications have response history across time, **When** the user opens the statistics view, **Then** the system displays at least the requested chart categories without data errors.

---

### Edge Cases

- What happens when a user attempts to move an application to an invalid or unknown stage?
- How does the system handle an application with no follow-up date or a reminder date in the past?
- What happens when a user archives or deletes an application that is still visible in the board?
- How does the system behave when a user has many applications and the dashboard must stay responsive?

## Requirements *(mandatory)*

### Functional Requirements

- **FR-001**: The system MUST allow users to create a new application with company, position, source, application date, and optional notes.
- **FR-002**: The system MUST allow users to edit, archive, and delete an existing application.
- **FR-003**: The system MUST support a configurable set of stages, including at minimum Applied, In progress, Interview, Offer, Rejected, and Withdrawn.
- **FR-004**: The system MUST provide a kanban-style board that allows drag-and-drop movement between stages.
- **FR-005**: The system MUST allow users to add and update notes and a next follow-up date for each application.
- **FR-006**: The system MUST highlight applications whose next follow-up date has already passed.
- **FR-007**: The system MUST provide a statistics view with a funnel view, activity over time, and conversion-rate information.
- **FR-008**: The system MUST support simple username/password authentication for a single user account and protect application data behind sign-in.
- **FR-009**: The system MUST persist application data in a database rather than only in browser storage.
- **FR-010**: The system MUST support a clear way to mark an application outcome as Offer, Rejected, or Withdrawn.

### Key Entities *(include if feature involves data)*

- **Application**: The core record for a job opportunity, including company, position, source, application date, current stage, outcome, notes, and follow-up information.
- **Stage**: A named pipeline step used to group applications and define their progression through the hiring process.
- **Follow-up Reminder**: A date-based reminder attached to an application so users can track when the next action is due.
- **User**: The authenticated person who owns and manages the application records.

## Success Criteria *(mandatory)*

### Measurable Outcomes

- **SC-001**: A user can create, update, and move an application through the workflow without errors in a single session.
- **SC-002**: The dashboard presents the board and summary statistics for dozens of applications in under 2 seconds on a typical device.
- **SC-003**: Core business logic for stage transitions and statistics calculations passes automated regression tests.
- **SC-004**: A new contributor can follow the README setup instructions and run the application locally in under 5 minutes.
- **SC-005**: The project has a publicly accessible demo link that can be shared for review or interviews.

## Constitution Alignment *(mandatory)*

- **Spec-First Delivery**: This specification defines the feature scope, user stories, and acceptance criteria before implementation begins.
- **Workflow Integrity**: The requirements preserve the core job-tracking flow of recording applications, moving them through stages, and reviewing summary insights.
- **Test-First and Regression Safety**: The success criteria and requirements explicitly call for automated tests around stage transitions and statistics.
- **Data Integrity and Integration**: Application state, follow-up dates, and outcomes are treated as persisted domain data that must remain consistent across views and workflows.
- **Operability and Simplicity**: The scope avoids multi-user, inbox integration, and mobile-app complexity in v1, keeping the first release focused and maintainable.

## Assumptions

- The initial release is intended for a single user and does not require multi-user collaboration or shared access.
- Reminders are based on the next follow-up date and are surfaced as overdue when that date has passed.
- Authentication will use a simple username/password sign-in mechanism sufficient for protecting a personal dashboard.
- The deployment target is a public demo environment and not a production-grade enterprise platform.
