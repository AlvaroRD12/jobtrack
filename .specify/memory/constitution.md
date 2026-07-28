<!--
Sync Impact Report
- Version change: 0.0.0 → 1.0.0
- Modified principles: N/A (initial constitution adopted from template)
- Added sections: Core Principles, Technology and Architecture Constraints, Development Workflow and Quality Gates, Governance
- Removed sections: None
- Templates requiring updates: ✅ .specify/templates/plan-template.md, ✅ .specify/templates/spec-template.md, ✅ .specify/templates/tasks-template.md
- Follow-up TODOs: None
-->

# Jobtrack Constitution

## Core Principles

### I. Spec-First Delivery
Every feature MUST start from a written specification with explicit user stories, acceptance criteria, and scope boundaries before implementation begins. If a requirement is unclear, it MUST be captured as a clarification item and resolved before coding. This prevents drift between product intent and delivery.

### II. Workflow Integrity
The system MUST preserve the core user journey of tracking job applications through a kanban board, ordered phases, and summary statistics. Changes to application state, phase transitions, filtering, or reporting MUST maintain the expected workflow semantics and present clear status to users.

### III. Test-First and Regression Safety
New behavior MUST be specified by tests or executable contracts before implementation. Backend changes MUST include unit or integration tests around persistence, service rules, and phase transitions; UI changes MUST cover interaction flows that affect board movement or candidate status. The default expectation is a red-green-refactor cycle for changed behavior.

### IV. Data Integrity and Integration
Application state MUST be persisted and validated consistently across layers. Domain rules such as phase transitions, status values, and candidate data MUST be enforced in the backend or shared service layer rather than only in the UI. Cross-component changes MUST preserve data contracts and support rollback-safe migration planning when schema changes are required.

### V. Operability and Simplicity
The implementation MUST favor clear, maintainable structure over premature abstraction. Logging, error handling, and user-visible feedback MUST be present for failures affecting candidate updates, persistence, or board operations. Unnecessary complexity or framework sprawl is not allowed without explicit justification in the feature plan.

## Technology and Architecture Constraints
The default implementation stack for the backend MUST remain Spring Boot with JPA, and the default UI stack MUST remain Vue or React unless a feature spec explicitly approves a different approach. Domain logic MUST stay in backend services and repositories rather than being implemented only in UI state. Candidate, application, and phase data MUST be stored in a relational model with clear validation rules.

## Development Workflow and Quality Gates
Every change MUST follow the Spec Kit workflow: specification, implementation plan, task breakdown, and review. Plans MUST identify testing strategy, data or schema impact, and any documentation updates required for user-visible behavior. Pull requests and milestone reviews MUST verify compliance with this constitution, including tests, documentation, and the preservation of the core job-tracking workflow.

## Governance
This constitution supersedes informal practices and ad-hoc decisions. Amendments MUST be documented with rationale, affected templates or guidance files, and a version bump before they take effect. Changes to a core principle require explicit review, a migration note for affected work, and confirmation that the updated rule still supports the project’s user workflow and delivery discipline.

**Version**: 1.0.0 | **Ratified**: 2026-07-28 | **Last Amended**: 2026-07-28
