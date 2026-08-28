# Application API Contract

## Authentication

### POST /api/auth/register
Creates a user account with username/password credentials.

**Request body**
```json
{
  "username": "jobseeker",
  "password": "secret123"
}
```

**Response**
```json
{
  "token": "jwt-token",
  "user": {
    "id": 1,
    "username": "jobseeker"
  }
}
```

### POST /api/auth/login
Authenticates a user and returns a JWT.

## Applications

### GET /api/applications
Returns all applications owned by the authenticated user.

### POST /api/applications
Creates a new application.

**Request body**
```json
{
  "company": "Acme Corp",
  "position": "Backend Engineer",
  "source": "LinkedIn",
  "applicationDate": "2026-07-28",
  "notes": "Initial outreach sent",
  "nextFollowUpDate": "2026-08-04"
}
```

### GET /api/applications/{id}
Returns one application by id.

### PUT /api/applications/{id}
Updates an application record.

### PATCH /api/applications/{id}/stage
Moves an application to a new stage.

**Request body**
```json
{
  "stageName": "Interview"
}
```

### PATCH /api/applications/{id}/archive
Archives or restores an application.

### DELETE /api/applications/{id}
Deletes an application.

## Stages

### GET /api/stages
Returns the configured pipeline stages and their ordering.

## Statistics

### GET /api/statistics
Returns summary information for the dashboard, including stage counts, response rate, and activity trends.
