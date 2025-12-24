# Chatu — Simple Spring Boot WebSocket Chat

Chatu is a lightweight (group & private) chat web application built with Spring Boot, WebSocket (STOMP). It demonstrates real-time messaging, simple user authentication.

## Project overview

This repository contains a Spring Boot application that provides a real-time chat interface using WebSocket/STOMP for message transport. It includes simple user management (registration/login), online user tracking, active group listing, and controllers/services to handle messaging and presence.

The project is intentionally compact to keep the learning surface small while still showing common real-time application patterns: WebSocket configuration, message DTOs, service layer separation.

## Features

- Real-time group chat (STOMP over WebSocket)
- Private messaging between users
- User registration and login (simple in-repo User entity + repository)
- Online user presence tracking
- Active groups listing

## Project structure

Top-level packages (under `src/main/java/com/example/chatu`):

- `config` — WebSocket and security configuration classes
- `controller` — REST and WebSocket controllers (message endpoints, user presence)
- `dto` — Message transfer objects (group/ private message payloads)
- `entity` — Domain entities (User)
- `repository` — Spring Data repository interfaces
- `service` — Business logic and presence tracking

For exact file names and locations, see the repo tree. The application main class is `com.example.chatu.ChatuApplication`.

## WebSocket details

- Endpoint: configured in `WebSocketConfig` (look for the STOMP endpoint registration)
- Message DTOs: `GroupMessageDTO` and `PrivateMessageDTO` define the payload structure used by controllers and front-end
- Security: WebSocket handshake and messaging are validated via `WebSocketValidationAdvice` and `WebSocketTracker` for tracking sessions
