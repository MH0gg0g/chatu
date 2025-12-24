# Chatu — Project Overview

This document describes the `chatu` Spring Boot project: its purpose, architecture, key packages and classes, configuration, runtime behaviour (REST + WebSocket/STOMP), and how to build and run it.

**Purpose**
- **Summary:** Chatu is a lightweight chat server built with Spring Boot that supports authenticated REST endpoints (for register/login and basic info) and real-time messaging using STOMP over WebSocket for private and group chats.

**Tech Stack**
- **Language:** Java (Spring Boot)
- **WebSocket:** Spring Messaging / STOMP over SockJS
- **Security:** Spring Security + JWT tokens
- **Persistence:** Spring Data JPA (entities in `user` package)
- **Build:** Maven (wrapper `./mvnw` included)

**How It Fits Together (High Level)**
- The application bootstraps from `com.example.chatu.ChatuApplication`.
- Authentication is handled using JWTs. REST authentication endpoints (`/api/auth`) issue tokens.
- WebSocket connections use STOMP; clients connect to a STOMP endpoint and send application messages under `/app` destinations. The server forwards messages to `/topic` (group) or `/user/{username}/queue` (private).
- User presence and active groups are tracked in-memory via `OnlineUserService` and `ActiveGroupService`.

**Project Structure (package-by-package)**
- **`com.example.chatu`**: root package containing `ChatuApplication` (Spring Boot entrypoint).

- **`com.example.chatu.message`**
  - `MessageController` — STOMP message controller. Handles two message mappings:
    - `@MessageMapping("/private.send")` — sends a private message. Uses `SimpMessagingTemplate.convertAndSendToUser(..., "/queue/private", payload)` so each user receives messages on their user queue.
    - `@MessageMapping("/group.send")` — sends a group message to `/topic/group.{groupId}` and adds the sender to the active group via `ActiveGroupService`.
  - DTOs: `PrivateMessageDTO`, `GroupMessageDTO`, `LoginRequest`, `RegisterRequest` (used for STOMP payloads / auth REST requests).

- **`com.example.chatu.secuirty`** (note: package name contains a small misspelling `secuirty`)
  - `AuthController` — REST controller at `/api/auth` with endpoints:
    - `POST /api/auth/register` — register new users (calls `UserService.register`).
    - `POST /api/auth/login` — authenticates credentials and returns `{ "token": "..." }` on success.
  - `JwtService` — issues and validates JWT tokens. Reads `jwt.secret` and `jwt.expiration-ms` from configuration.
  - `AuthenticationFilter` — extracts `Authorization: Bearer ...` header from HTTP requests and sets `SecurityContext` if token is valid.
  - `CustomUserDetailsService` — (used by security provider) loads user details from `UserRepository`.
  - `SecurityConfig` — configures Spring Security to:
    - Permit unauthenticated access to `/ws/**`, `/api/auth/**`, `/login`, and `/register`.
    - Add the `AuthenticationFilter` before the `UsernamePasswordAuthenticationFilter`.
    - Use stateless session management (JWT-based).
    - Configure CORS for `http://localhost:4200`.

- **`com.example.chatu.user`**
  - `User` — JPA entity mapped to `users` table with fields `id`, `username`, `email`, `password` (BCrypt-hashed on registration).
  - `UserRepository` — Spring Data JPA repository with `findByUsername` and `findByEmail`.
  - `UserService` — registration and user lookup; validates uniqueness and encodes passwords.
  - `OnlineUserService` / `OnlineUserController` — in-memory tracking of currently connected users; `GET /online-users` returns the set of online usernames.

- **`com.example.chatu.group`**
  - `ActiveGroupService` — in-memory map of groupId -> member usernames. Used to add/remove users and produce active-group summaries.
  - (There is an `ActiveGroupController` class present in the repo — it likely exposes group-related APIs; inspect it for exact mappings if needed.)

- **`com.example.chatu.webSocket`**
  - `WebSocketConfig` — enables STOMP messaging and registers the STOMP endpoint at `POST /ws/chat` using SockJS and permitting origin `http://localhost:4200`.
    - Message broker config: application destination prefix `/app`, simple broker destinations `/topic` and `/queue`, and user destination prefix `/user`.
  - `WebSocketAuthChannelInterceptor` — intercepts inbound CONNECT frames, extracts `Authorization` header, validates JWT, and if valid attaches a `Principal` to the STOMP session so message-handling methods can identify the user.
  - `WebSocketTracker` — listens to session connect/disconnect events and updates online users and removes disconnected users from active groups.

**Messaging & Endpoints Summary**
- REST endpoints (high-level):
  - `POST /api/auth/register` — register
  - `POST /api/auth/login` — login (returns JWT)
  - `GET /online-users` — returns current online usernames
  - `GET /active-groups` — returns a `Map<String, Integer>` name of the group and the number of members.

- WebSocket/STOMP endpoints:
  - STOMP endpoint: `ws://<host>:<port>/ws/chat` (SockJS fallback supported).
  - Client sends messages to application destinations prefixed with `/app` (because `WebSocketConfig.setApplicationDestinationPrefixes("/app")`):
    - `/app/private.send` — send private message (payload `PrivateMessageDTO`) — server forwards to `/user/{username}/queue/private`.
    - `/app/group.send` — send group message (payload `GroupMessageDTO`) — server forwards to `/topic/group.{groupId}`.

**Security Flow**
- Clients call `POST /api/auth/login` with username/password to receive a JWT token.
- For REST calls, include `Authorization: Bearer <token>` header. The `AuthenticationFilter` validates and populates `SecurityContext`.
- For WebSocket, during the STOMP `CONNECT` frame clients should include an `Authorization` header with `Bearer <token>`; the `WebSocketAuthChannelInterceptor` validates it and attaches a Principal to the session.

**Configuration**
- Profiles: `application.yaml`, `application-dev.yaml`, and `application-prod.yaml` are present in `src/main/resources` and also in `target/classes`.
- Important properties referenced by code:
  - `jwt.secret` — HMAC secret used by `JwtService`.
  - `jwt.expiration-ms` — token lifetime in milliseconds.
- CORS allowed origin is currently configured for `http://localhost:4200` in `SecurityConfig` and `WebSocketConfig`.

**Persistence & Database**
- The project uses Spring Data JPA for `User` persistence. Check `application*.yaml` to see the configured datasource (H2, PostgreSQL, MySQL, etc.).

**Build & Run**
- Build the project:
```bash
./mvnw -DskipTests package
```
- Run the app (dev):
```bash
./mvnw spring-boot:run
```
- Run tests:
```bash
./mvnw test
```

**Client Integration Notes**
- After logging in via `/api/auth/login`, send the returned JWT in two places:
  - For REST calls: `Authorization: Bearer <token>` header.
  - For WebSocket STOMP CONNECT: include `Authorization: Bearer <token>` as a STOMP header (native header).

**Known Caveats & Suggestions**
- The package name `secuirty` is misspelled — consider renaming to `security` for clarity (requires refactoring imports).
- `JwtService` uses `io.jsonwebtoken` APIs. Verify token parsing/signing API compatibility and exception handling.
- JWT secret (`jwt.secret`) should be a sufficiently long random key and not committed to source control; use environment variables or a secrets manager.
- The in-memory `OnlineUserService` and `ActiveGroupService` are not distributed — for multi-node deployments, back them with a shared store (Redis) if needed.
- Consider adding more explicit API documentation (OpenAPI/Swagger) and sample clients (Postman collection or a simple web client example).

**Next Steps / Optional Improvements**
- Add `README.md` section with sample curl and STOMP examples (I can add them).
- Add API docs (OpenAPI) and integration tests for WebSocket flows.
- Move CORS origins and other environment-specific settings into configuration per environment.

---
Generated by code review on the local workspace. If you want, I can:
- add example Postman requests for auth endpoints,
- create a minimal front-end demo showing login + STOMP messaging, or
- run the test suite (`./mvnw test`) and paste results here.

If you want any of those, tell me which next.
