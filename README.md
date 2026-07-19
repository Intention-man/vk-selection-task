# API Gateway with RBAC and Auditing

A Spring Boot service that acts as an authenticated proxy layer over an external REST API
([JSONPlaceholder](https://jsonplaceholder.typicode.com/)), adding role-based access control,
request auditing and caching.

Built as a selection task for a VK internship.

**Stack:** Java · Spring Boot · Spring Security · JUnit

---

## What it does

The service sits between clients and an external API, and adds three layers the original API doesn't have:

| Layer | Purpose |
|---|---|
| **Authentication & RBAC** | Token-based auth with an 8-role permission hierarchy |
| **Auditing** | Every request is persisted with its outcome, including denied ones |
| **Caching** | Reduces load on the upstream API; invalidation depends on the HTTP method |

All four CRUD methods are proxied: `GET`, `POST`, `PUT`, `DELETE`.

---

## Design notes

**Role hierarchy.** Permissions are modelled as a tree rather than a flat list, so a parent role
automatically inherits the rights of its children. This keeps authorization rules declarative
and avoids duplicating grants across roles.

```
ROLE_ADMIN
├── ROLE_USERS
│   └── ROLE_USERS_VIEWER
├── ROLE_POSTS
│   └── ROLE_POSTS_VIEWER
└── ROLE_ALBUMS
    └── ROLE_ALBUMS_VIEWER
        └── ROLE_DEFAULT   // included in every other role
```

**Auditing.** Each request is recorded with timestamp, HTTP method, path, user and whether the
user was authorized to perform it. Denied requests are logged as well — this makes the audit log
usable for detecting unauthorized access attempts, not just for tracing successful traffic.

**Caching.** Read requests are served from the cache when possible; write requests invalidate
the affected entries, so clients never observe stale data after a mutation.

---

## Getting started

```bash
./mvnw spring-boot:run
```

The application starts at `http://localhost:8181/app`. Default configuration lives in
`resources/application.properties`; no extra JVM arguments are required.

### Trying it out

A Postman collection is included: `Endpoints.postman_collection.json`. It defines two variables —
`url` (pre-set to the base path) and `token`.

1. **Register** — call any `*Login` endpoint with `/login` replaced by `/registration`.
   The response body contains a token.
2. **Set the token** — assign it to the `token` collection variable.
3. **Call the API** — every request already sends `Authorization: {{token}}`.

To authenticate an existing user, use the `/login` endpoints directly.

---

## Testing

Unit and integration tests cover the proxying layer, authorization rules, auditing and caching.

```bash
./mvnw test
```

---

## Roadmap

- WebSocket support for real-time updates
