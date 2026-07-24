# LLM-Powered Full-Stack Application Generator

> An AI-native platform that turns natural language prompts into fully working, deployable full-stack applications — built with **Spring Boot**.

Think of it as an open engineering take on tools like Lovable / v0 / bolt.new: a user chats with an AI assistant inside a **Project**, the assistant generates and edits real code (**ProjectFile**s), and the app can be spun up instantly in an isolated **Preview** environment for the user to see it live.

---

## ✨ Core Idea

1. A user creates a **Project**.
2. Inside the project, they open a **ChatSession** and talk to an LLM (`ChatMessage`s with `USER`, `ASSISTANT`, `SYSTEM`, `TOOL` roles).
3. The assistant generates/updates files → stored as **ProjectFile**s (content persisted in **MinIO** via object keys, not in the DB).
4. The project can be deployed to a **Preview** environment (namespace + pod on Kubernetes) so the user gets a live, shareable URL.
5. Usage is metered per user/project (**UsageLog** — tokens, duration, model used) and gated by a **Plan** (project limits, token limits, preview limits) tied to a **Stripe Subscription**.

---

## 🏗️ Architecture

![System architecture](./docs/architecture.svg)

`User` owns a `Project` → opens a `Chat session` where the LLM generates code → saved as `Project files` in MinIO → deployed to a live `Preview` on Kubernetes. `Plan and billing` (Stripe) hangs off the `Project` to gate limits (max projects, tokens/day, previews).

---

## 🧱 Tech Stack (current)

| Layer | Technology |
|---|---|
| Backend | Java, Spring Boot |
| Database | PostgreSQL |
| ORM | Spring Data JPA / Hibernate |
| Mapping | MapStruct (entity ↔ DTO) |
| Auth | Spring Security + JWT (jjwt) — login/signup issue real tokens, `JwtAuthFilter` reads `Authorization: Bearer` |
| Object Storage | MinIO (project file contents) |
| Preview Infra | Kubernetes (per-project pod + namespace) |
| Billing | Stripe (Plans & Subscriptions) |
| Boilerplate | Lombok (`@Getter/@Setter`, `@FieldDefaults`, `@Builder`) |

> 🔄 Tech stack is evolving — this table will be updated as new components (frontend, LLM provider, message queue, etc.) are added. See [`PROGRESS.md`](./PROGRESS.md) for the live log.

---

## 🗂️ Domain Model

### Entities

| Entity | Responsibility |
|---|---|
| `User` | Core account — auth identity, profile |
| `Project` | A single app/workspace a user is building. Ownership is expressed via a `ProjectMember` row with role `OWNER` (created automatically alongside the project), not a separate owner field. |
| `ProjectMember` (+ `ProjectMemberId`) | Many-to-many collaborators on a project, with `ProjectRole` (`OWNER`, `EDITOR`, `VIEWER`) |
| `ChatSession` | One conversation thread scoped to a project + user |
| `ChatMessage` | Individual message in a session (`role`, `content`, `toolCalls`, `tokensUsed`) |
| `ProjectFile` | A generated/edited file in the project (path + MinIO object key + audit fields) |
| `Preview` | A live deployment of a project (Kubernetes namespace/pod, `PreviewStatus`, preview URL) |
| `Plan` | A billing tier definition (project/token/preview limits, unlimited AI flag) |
| `Subscriptions` | A user's active Stripe subscription against a `Plan` |
| `UsageLog` | Metering record per action (tokens used, duration, JSON metadata of model/prompt) |

### Enums

- `MessageRole` → `USER`, `ASSISTANT`, `SYSTEM`, `TOOL`
- `PreviewStatus` → `CREATING`, `RUNNING`, `FAILED`, `TERMINATED`
- `ProjectRole` → `OWNER`, `EDITOR`, `VIEWER`
- `SubscriptionStatus` → `ACTIVE`, `TRAILING`, `CANCELLED`, `PAST_DUE`, `INCOMPLETE`

### Relationships (high level)

```
User ──< Project (owner)
User ──< ProjectMember >── Project        (many-to-many, with role)
Project ──< ChatSession >── User
ChatSession ──< ChatMessage
Project ──< ProjectFile >── User (createdBy / updatedBy)
Project ──< Preview
User ──< Subscriptions >── Plan
User ──< UsageLog >── Project
```

---

## 🚀 Getting Started

```bash
# clone
git clone https://github.com/<your-username>/<repo-name>.git
cd <repo-name>

# run (once application.yml / DB is configured)
./mvnw spring-boot:run
```

> Setup instructions (DB config, MinIO, environment variables) will be filled in as those modules are built.

---

## 📁 Project Structure

```
com.rajnish
├── entity              # JPA entities (domain model)
├── common
│   ├── enums            # Shared enums (MessageRole, ProjectRole, etc.)
│   ├── exceptions        # ApiError, BadRequestException, ResourceNotFoundException, GlobalExceptionHandler
│   └── security          # WebSecurityConfig (BCrypt, stateless sessions; JWT filter pending)
├── controller           # REST API layer — routing only, no business logic
├── service              # Service interfaces + impl/ (Project, ProjectMember done; Auth signup done, login pending)
├── mapper               # MapStruct mappers (entity ↔ DTO)
├── repository           # Spring Data JPA repositories (Project, ProjectMember, User done)
├── dto                  # Request/response contracts (auth, project, member, file, subscriptions)
└── llm                  # (planned) LLM orchestration layer
```

---

## 📡 API Endpoints (in progress)

Controllers and service contracts are wired up; business logic implementations are next. Current routes:

| Area | Method & Path | Purpose |
|---|---|---|
| Auth | `POST /api/auth/signup` | Create a new account |
| Auth | `POST /api/auth/login` | Authenticate, get tokens |
| Auth | `GET /api/auth/me` | Current user profile |
| Projects | `GET /api/project` | List current user's projects |
| Projects | `GET /api/project/{id}` | Get a single project |
| Projects | `POST /api/project` | Create a project |
| Projects | `PATCH /api/project/{id}` | Update a project |
| Projects | `DELETE /api/project/{id}` | Soft-delete a project |
| Members | `GET /api/projects/{id}/members` | List project members |
| Members | `POST /api/projects/{id}/members` | Invite a member |
| Members | `PATCH /api/projects/{id}/members/{memberId}` | Update a member's role |
| Members | `DELETE /api/projects/{id}/members/{memberId}` | Remove a member |
| Files | `GET /api/projects/{projectId}/files` | Get project file tree |
| Files | `GET /api/projects/{projectId}/files/{*path}` | Get file content by path |
| Billing | `GET /api/plans` | List active plans |
| Billing | `GET /api/me/subscription` | Current user's subscription |
| Billing | `POST /api/stripe/checkout` | Create Stripe checkout session |
| Billing | `POST /api/stripe/portal` | Open Stripe customer portal |
| Usage | `GET /api/usage/today` | Today's usage for current user |
| Usage | `GET /api/usage/limits` | Current plan limits |

> ⚠️ Auth isn't wired in yet — `userId` is currently hardcoded as a placeholder in every controller. See [`PROGRESS.md`](./PROGRESS.md) for details.

---

## 🗺️ Roadmap

- [x] Design core domain model (entities + enums)
- [x] Controller layer + DTOs + service interfaces (contracts only, no logic yet)
- [x] Repository layer (Project, ProjectMember, User) + PostgreSQL wired via `application.yml`
- [x] MapStruct mappers for entity ↔ DTO conversion
- [x] `ProjectServiceImpl` fully implemented (create, list, get, update, soft-delete)
- [x] `ProjectMemberServiceImpl` fully implemented (list, invite, update role, remove)
- [x] Custom exceptions + global `@RestControllerAdvice` (`ApiError`, `BadRequestException`, `ResourceNotFoundException`)
- [x] JWT auth end-to-end: signup, login, `JwtAuthFilter`, `AuthUtils.getCurrentUserId()` — hardcoded `userId` removed from all controllers/services
- [x] Ownership scoping fixed in `findAccessibleProjectById` (now scoped via `ProjectMember` `EXISTS` subquery)
- [ ] **Fix permission regression**: `inviteMember` / `updateMemberRole` / `removeProjectMember` no longer check the caller is an `OWNER` (lost when `Project.owner` was replaced by `ProjectMember` rows)
- [ ] Verify `JwtAuthFilter` is registered in the security filter chain + `permitAll()` is tightened
- [ ] Flyway/Liquibase migrations (replace `ddl-auto: update`)
- [ ] `UserServiceImpl.getProfile()` still unimplemented
- [ ] Remaining service implementations (File, Subscription, Plan, Usage)
- [ ] Chat session + message APIs
- [ ] LLM integration (prompt orchestration, tool calls)
- [ ] File generation → MinIO storage integration
- [ ] Preview deployment engine (Kubernetes API integration)
- [ ] Stripe billing integration (Plans, Subscriptions, webhooks)
- [ ] Usage metering + rate limiting per plan
- [ ] Frontend (stack TBD)

Full day-by-day log lives in **[PROGRESS.md](./PROGRESS.md)**.

---

## 📌 Status

🚧 **Early-stage / actively in development.** Domain model, controller layer, DTOs, and service contracts are in place. Business logic implementation and auth wiring are in progress next.

---

## 👤 Author

**Rajnish** — building this as a flagship full-stack + AI systems design project.

## 📄 License

TBD (add a license once you decide, e.g. MIT).