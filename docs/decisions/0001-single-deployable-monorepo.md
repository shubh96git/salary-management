# 0001 — One repository, one deployable application

**Status:** Accepted

## Context

The solution has a Spring Boot backend and an Angular frontend. It must be deployed, and reviewers need to follow its history in one place.

## Decision

- Keep backend, frontend and docs in **one Git repository**.
- Build the Angular app and serve its static files **from the Spring Boot application**, producing a single Docker image.

## Consequences

- ✅ One commit history shows how backend and UI evolved together.
- ✅ One service to deploy; UI and API share an origin, so no CORS configuration in production.
- ✅ Free hosting tiers usually allow one always-available service; this fits.
- ❌ Frontend and backend cannot be scaled or released independently. Acceptable for one persona and 10,000 records.
- ❌ The build is slightly more involved (frontend build step before packaging the backend).
