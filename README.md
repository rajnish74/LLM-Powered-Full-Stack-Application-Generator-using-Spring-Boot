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
| ORM | Spring Data JPA / Hibernate |
| Auth | (planned) Spring Security + JWT |
| Object Storage | MinIO (project file contents) |
| Preview Infra | Kubernetes (per-project pod + namespace) |
| Billing | Stripe (Plans & Subscriptions) |
| Boilerplate | Lombok (`@Getter/@Setter`, `@FieldDefaults`) |

> 🔄 Tech stack is evolving — this table will be updated as new components (frontend, LLM provider, message queue, etc.) are added. See [`PROGRESS.md`](./PROGRESS.md) for the live log.

---

## 🗂️ Domain Model

### Entities

| Entity | Responsibility |
|---|---|
| `User` | Core account — auth identity, profile |
| `Project` | A single app/workspace a user is building |
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
├── common.enums        # Shared enums (MessageRole, ProjectRole, etc.)
├── repository           # (planned) Spring Data repositories
├── service              # (planned) Business logic
├── controller           # (planned) REST API layer
├── dto                  # (planned) Request/response contracts
├── config               # (planned) Security, MinIO, Stripe, K8s client config
└── llm                  # (planned) LLM orchestration layer
```

---

## 🗺️ Roadmap

- [x] Design core domain model (entities + enums)
- [ ] Add JPA annotations (`@Entity`, `@Id`, `@ManyToOne`, etc.) + Flyway/Liquibase migrations
- [ ] Repository layer
- [ ] Auth (Spring Security + JWT)
- [ ] Project CRUD + membership APIs
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

🚧 **Early-stage / actively in development.** Domain modeling phase complete; core Spring Boot wiring in progress.

---

## 👤 Author

**Rajnish** — building this as a flagship full-stack + AI systems design project.

## 📄 License

TBD (add a license once you decide, e.g. MIT).