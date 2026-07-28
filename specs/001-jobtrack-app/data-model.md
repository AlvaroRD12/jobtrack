# Data Model: JobTrack

## Entities

### User

Represents the authenticated owner of the application tracker.

- `id`: UUID or long surrogate key
- `username`: unique login name
- `passwordHash`: BCrypt-hashed password
- `createdAt`: timestamp
- `updatedAt`: timestamp

**Validation rules**:
- Username must be present and unique.
- Password hash must be generated with BCrypt before persistence.

### Application

Represents a single job application record.

- `id`: UUID or long surrogate key
- `userId`: foreign key to `User`
- `company`: required string
- `position`: required string
- `source`: optional string
- `applicationDate`: required date
- `currentStageId`: foreign key to `StageDefinition`
- `notes`: optional text
- `nextFollowUpDate`: optional date
- `archived`: boolean flag
- `createdAt`: timestamp
- `updatedAt`: timestamp

**Validation rules**:
- Company and position are required.
- Application date is required.
- Current stage must belong to a defined stage list.
- If the current stage is terminal (`Offer`, `Rejected`, or `Withdrawn`), the application outcome is derived from that stage; no separate persistent `outcome` field is stored.

### StageDefinition

Represents the configured pipeline stages shown in the kanban board.

- `id`: surrogate key
- `name`: unique stage name such as `Applied`, `In progress`, `Interview`, `Offer`, `Rejected`, `Withdrawn`
- `order`: display order
- `isTerminal`: boolean

**Validation rules**:
- Stage names must be unique.
- Order must be non-negative and stable.

### FollowUpReminder

A lightweight reminder concept associated with an application.

- `applicationId`: foreign key to `Application`
- `nextFollowUpDate`: date
- `isOverdue`: derived from the current date and reminder date

**Implementation note**: For v1, this can be modeled as a derived field on `Application` or as a small reminder entity depending on the chosen persistence approach. The important requirement is that overdue state stays consistent with the application data.

## Relationships

- One `User` has many `Application` records.
- One `Application` belongs to one `StageDefinition` at a time.
- One `Application` can have zero or one reminder state.

## State transitions

- Applications may move from one stage to another through the kanban workflow.
- Terminal stages such as `Offer`, `Rejected`, and `Withdrawn` should be treated as final outcomes for the workflow, while `Applied` and `In progress` remain active stages.
