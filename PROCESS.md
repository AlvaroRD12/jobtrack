# Development Process

This document describes how JobTrack was actually built: not just what
was implemented, but how issues were found and fixed along the way. It's
part of a deliberate Spec-Driven Development workflow using
[GitHub Spec Kit](https://github.com/github/spec-kit), where an AI coding
agent (GitHub Copilot, in Agent mode) implemented the code from a written
specification, plan, and task breakdown — with every non-trivial claim
verified against the running application before being trusted.

## Methodology

Every feature followed the same pipeline:

1. **Specify** — write user stories, functional requirements, and edge
   cases (`spec.md`), refined with `/speckit.clarify` to resolve open
   ambiguities before any code was written.
2. **Plan** — turn the spec into a technical plan, data model, and API
   contract (`plan.md`, `data-model.md`, `contracts/`), checked against
   the project's constitution (`constitution.md`).
3. **Tasks** — break the plan into ordered, story-scoped tasks
   (`tasks.md`).
4. **Implement** — have the agent implement one user story at a time,
   never the whole feature in one pass.
5. **Verify** — treat the agent's "done" and "tests pass" as a claim to
   check, not a fact. Every user story was confirmed with real HTTP
   requests against the running backend (and, for UI behavior, real
   browser interaction) before being committed.

That last step is the one worth documenting in detail, because it's where
most of the actual engineering happened.

## What verification caught

### 1. A hardcoded authentication path (Foundational phase)

Early in the project, login appeared to work — the server started, tests
passed. But the generated `CustomUserDetailsService` didn't query the
database at all: it accepted exactly one hardcoded user (`demo`/`demo`)
regardless of what was actually registered. It was a plausible-looking
placeholder that let the app boot and appear functional.

**Found by**: reading the actual `UserDetailsService` implementation
instead of trusting that "login works" because a test passed.
**Fixed by**: wiring a real `UserRepository`-backed lookup, with BCrypt
password hashes read from storage, not regenerated on the fly.

### 2. A fake JWT ("demo-token")

After fixing the hardcoded user, a follow-up manual login test revealed
the returned "token" was the literal string `"demo-token"` — not a real
JSON Web Token. It would have made every protected endpoint either
trivially bypassable or permanently broken once real validation was
added.

**Found by**: inspecting the actual HTTP response body of a login
request, not just checking the status code.
**Fixed by**: implementing real JWT generation and validation with the
`jjwt` library already declared in `pom.xml`, including signature
verification and expiry.

### 3. Cross-user data access returning 500 instead of 403

During User Story 1 (application CRUD), a non-owner attempting to
archive another user's application returned an unhandled `500` instead
of a clean `403`. The exception was real (the ownership check existed)
but wasn't mapped to the right HTTP status, and — worse — the generic
exception handler wasn't logging anything, making the failure invisible
in the server console.

**Found by**: manually testing the two-user scenario (create as user A,
attempt to modify as user B) instead of relying on unit tests that
happened to cover a different path.
**Fixed by**: adding a dedicated exception type and handler mapping
ownership violations to `403`, and making the generic handler always log
the exception type, message, and stack trace.

### 4. Unauthenticated requests also returning 500

A related gap: `/api/applications/**` was left in Spring Security's
`permitAll()` list — likely a leftover from early testing before JWT
existed. Requests with no token at all reached the controller, hit a
`null` authenticated user, and fell into the same generic 500 handler
instead of a clean `401`.

**Found by**: testing the "no token at all" case explicitly, not just the
"wrong user" case.
**Fixed by**: removing those routes from `permitAll()` and adding a JSON
`AuthenticationEntryPoint` so missing authentication returns a proper
`401` with a JSON body.

### 5. `order` as a column name (User Story 2)

The most instructive bug of the project. `StageDefinition.order` compiled
fine, unit tests passed, but **every** query against the
`stage_definitions` table failed at runtime with a SQL syntax error —
because `ORDER` is a reserved SQL keyword, and H2 refused to create or
query a column with that literal name.

This one was hard to find because the symptom (a `500` on `PUT
.../stage`) looked identical to an unrelated exception-mapping issue we
were chasing at the same time, and multiple rounds of "it's fixed, tests
pass" from the agent didn't match live behavior. The actual cause was
sitting in the **application startup log** the whole time — a
`CommandAcceptanceException` during schema creation — but had been
scrolled past while looking for something more specific to the request
path.

**Found by**: reading the full backend console output from a clean
restart, top to bottom, instead of only the lines near the failing
request.
**Fixed by**: mapping the field to a non-reserved column name
(`@Column(name = "display_order")`), followed by a full-codebase scan for
other entity fields that might collide with SQL reserved words.

**Lesson**: when a live reproduction contradicts a "tests pass" claim
repeatedly, the mismatch is often not in the code path being debugged —
it's in what the test actually exercises versus what the real client
sends. Going back to first principles (full logs, clean rebuild, minimal
reproduction) resolved in minutes what several rounds of targeted
guessing hadn't.

### 6. Frontend never actually wired to the backend (User Story 2)

The kanban board's drag-and-drop code was implemented and had a passing
component test, but nothing in the frontend attached the JWT to outgoing
requests, and the Vite dev server had no proxy configured for `/api`
routes. In local development, every real request from the browser would
have failed before ever reaching the backend.

**Found by**: reading `vite.config.ts` and the axios client setup
directly, rather than accepting "the code is present" as equivalent to
"the code works end to end."
**Fixed by**: adding the dev proxy, and centralizing all API calls
through a single axios instance with a request interceptor that attaches
the stored token.

### 7. Frontend field name mismatch silently overriding correct backend data (User Story 3)

The backend correctly computed and returned `overdue: true/false` via a
`@Transient isOverdue()` method. But the frontend's `loadApplications()`
recomputed `overdue` itself from `app.nextFollowUpDate` — a field that
doesn't exist on the response object (the real field is `followUpDate`).
The mismatch meant the recomputed value was always `false`, silently
overwriting the backend's correct value. No error, no crash — every
application just always looked "not overdue," including ones that were.

**Found by**: comparing the field names in a real, previously-verified
API response against what the frontend code was actually reading —
not by re-testing the backend, which was already known to be correct.
**Fixed by**: removing the frontend recomputation entirely and trusting
the backend's `overdue` field directly, since duplicating trivial derived
logic across two layers is exactly the kind of thing that drifts out of
sync.

### 8. JWT filter rejecting public routes because of a stale token (User Story 3)

Logging in through the actual browser UI failed with `401 "Invalid or
expired token"` — but this had nothing to do with the login credentials.
`JwtAuthenticationFilter` was validating the `Authorization` header on
*every* request, including `/api/auth/login` and `/api/auth/register`,
which don't require authentication at all. Any stale token left in
`localStorage` from a previous session (e.g. after an H2 in-memory
database reset on backend restart) was enough to block login entirely.

**Found by**: reproducing the failure through the real UI instead of only
testing the API directly with curl, where no stale browser token exists
to trigger the bug.
**Fixed by**: moving the public-route check (`/api/auth/**`) to the very
start of the filter, before any token parsing happens, so those routes
are never affected by whatever the client happens to send.

### 9. A fully implemented, fully tested feature that was unreachable (User Story 4)

`DashboardView.vue` and `StatisticsPanel.vue` were built, wired to a
working backend endpoint, and covered by passing tests — but there was no
`vue-router` in the project at all. `main.ts` only ever mounted `App.vue`
directly, with no routes and no navigation. The statistics page existed
on disk and worked in isolation, but no user could ever reach it from the
running application.

**Found by**: actually opening the app in a browser and looking for the
feature, instead of accepting "the component exists and its tests pass"
as equivalent to "a user can use this."
**Fixed by**: adding `vue-router` with routes for the existing
Applications view and the new Dashboard view, plus a visible nav link
between them.

**Lesson**: a component's own test suite can never prove it's reachable.
That has to be checked one level up, in the thing that's supposed to
route to it.

### 10. Wrapped API responses unwrapped one level too few — and a mock that hid it (User Story 4)

The backend wraps every response as `{ "message": "...", "data": ... }`.
`api/applications.ts` already unwrapped this correctly, but the new
`api/statistics.ts` returned `response.data` directly instead of
`response.data.data`. In the running UI, this meant the panel iterated
over the wrapper object itself — rendering a couple of blank `":"` rows
instead of real stage names and counts.

This shipped with all tests green because `StatisticsPanel.test.ts`
mocked the API functions to resolve directly to the raw arrays, which is
not the shape the real functions returned before the fix. The mock
described what the code *should* do, not what it *did* — so the test
suite couldn't have caught this regardless of how thorough it looked.

**Found by**: comparing the real rendered output (blank rows with a lone
`:`) against the already-confirmed-correct API response from earlier
manual curl testing, and recognizing the exact shape of a
"one-level-too-shallow unwrap" bug from having seen the pattern before.
**Fixed by**: unwrapping to `response.data.data` in `statistics.ts`,
matching `applications.ts`, and correcting the test mocks to match the
real (wrapped) response shape instead of the assumed one.

**Recurring pattern across this project**: this is the third time a bug
came from the frontend's assumption about a data shape or field name not
matching what the backend actually sent (see also #7, the
`nextFollowUpDate`/`followUpDate` mismatch, and #8's related stale-token
issue). The general lesson: whenever a new frontend module talks to an
existing backend contract, check the *actual* response shape and field
names against a real request — never assume they match a mental model or
a mock, even one written by the same agent that wrote the endpoint.

### 11. The first real cross-origin request in the project (Deployment)

Everything up to this point — local dev, Docker testing, even the
production Docker container — always had the frontend and backend on the
same origin (via Vite's dev proxy) or wasn't tested from a browser at
all. The first time the deployed frontend (on Render's static site
domain) tried to talk to the deployed backend (on a different Render
domain), the browser's CORS preflight `OPTIONS` request came back `403
Forbidden`, blocking the real request before it was ever sent.

**Found by**: testing the production build (`npm run preview`) against
the real deployed backend before deploying the frontend — catching this
locally instead of discovering it only after the frontend was live.
**Fixed by**: adding an explicit `CorsConfigurationSource` bean listing
allowed origins (including a configurable `FRONTEND_URL` environment
variable for the real production frontend domain), permitting `OPTIONS`
requests without authentication, and wiring `.cors()` into the security
filter chain.

**Lesson**: same-origin setups (dev proxies, single-container tests) can
fully hide a CORS bug until the exact moment two independently deployed
services try to talk to each other for the first time. Test the actual
cross-origin path — even locally, with `npm run preview` against a real
remote backend — before assuming a deployment will "just work."

## Deployment

The application is deployed with entirely free-tier infrastructure:

- **Database**: [Neon](https://neon.tech) — serverless PostgreSQL, free
  tier does not expire (unlike most providers' free database tiers,
  which are time-limited).
- **Backend**: [Render](https://render.com) Web Service, deployed from a
  multi-stage Docker build. Free tier spins down after 15 minutes of
  inactivity; the first request after that takes 30-60 seconds to wake
  up.
- **Frontend**: Render Static Site, built from the same repository.

The backend uses a Spring profile (`prod`) activated via
`SPRING_PROFILES_ACTIVE`, keeping local development on H2 completely
unaffected by the production PostgreSQL configuration. The Docker image's
entrypoint reads this variable at runtime rather than hardcoding it, so
the same image can be run locally against H2 for quick manual testing or
against Neon for a production-like check before deploying.

## Why this matters

None of these were exotic bugs. Each one is the kind of thing a code
review would normally catch — which is exactly the point: an AI agent
implementing quickly and confidently is not a substitute for that review,
it's a reason to do it more deliberately, not less. The pattern that
worked throughout this project was simple and repeated constantly:

- Treat "tests pass" and "it's fixed" as claims, not facts.
- Prefer live reproduction (real HTTP requests, real browser interaction)
  over trusting a summary.
- When an explanation doesn't fully account for the evidence, don't
  accept it — ask for the raw output, not a description of it.
- Read full logs, not just the lines that seem relevant to the current
  hypothesis.