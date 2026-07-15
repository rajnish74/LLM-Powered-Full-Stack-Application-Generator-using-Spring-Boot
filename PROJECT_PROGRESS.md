# Progress Log — LLM-Powered Full-Stack Application Generator

Tracking daily/weekly progress on this project. Add a new entry at the top each time you work on it — future-you (and interviewers) will thank you.

---

## 📅 2026-07-15 — Day 1: Domain Modeling

**Goal for the day:** Design the core domain model for the entire platform before writing any persistence or API code.

### ✅ Completed
- Defined all core entities in `com.rajnish.entity`:
    - `User` — account/auth identity
    - `Project` — top-level workspace a user builds
    - `ProjectMember` + composite key `ProjectMemberId` — collaborator access with roles
    - `ChatSession` — conversation container scoped to a project + user
    - `ChatMessage` — individual AI/user/system/tool message with token tracking
    - `ProjectFile` — generated file metadata (path + MinIO object key), with audit fields
    - `Preview` — live deployment tracking (namespace, pod name, preview URL, status)
    - `Plan` — billing tier definition (limits on projects/tokens/previews)
    - `Subscriptions` — Stripe-backed subscription linking `User` ↔ `Plan`
    - `UsageLog` — per-action metering (tokens, duration, JSON metadata)
- Defined supporting enums in `com.rajnish.common.enums`:
    - `MessageRole`, `PreviewStatus`, `ProjectRole`, `SubscriptionStatus`
- Standardized on Lombok (`@Getter`, `@Setter`, `@FieldDefaults(level = PRIVATE)`) to keep entities clean and consistent.

### 🧠 Design Decisions / Notes
- File **contents** are intentionally NOT stored in the DB — only `minioObjectKey` is stored on `ProjectFile`, keeping the DB lightweight and letting MinIO handle blob storage.
- `Preview` is modeled around Kubernetes concepts (`namespace`, `podName`) from day one, since preview environments will be dynamically provisioned per project.
- `UsageLog.metadata` is a JSON string (model used, prompt used) rather than a rigid schema — keeps metering flexible as LLM providers/models change.
- Soft-delete pattern (`deletedAt`) used on `Project`, `ChatSession`, and `User` instead of hard deletes — needed for audit history and recoverability.
- `ProjectMember` uses a composite key (`ProjectMemberId`) instead of a surrogate key — enforces one membership row per (project, user) pair naturally.

### ⚠️ Known Gaps (to fix next)
- No JPA annotations yet (`@Entity`, `@Table`, `@Id`, `@GeneratedValue`, `@ManyToOne`, `@Embeddable`, etc.) — entities are currently plain POJOs.
- No `@Column` constraints (nullable, unique, length) defined yet.
- `ProjectMemberId` needs `@Embeddable` + `equals()`/`hashCode()` (or `@EqualsAndHashCode` via Lombok).
- No repository layer yet.
- No validation annotations (`@NotNull`, `@Email`, etc.).

### 🔜 Next Steps
1. Add proper JPA annotations to every entity + relationships (`@ManyToOne`, `@OneToMany`, `@EmbeddedId`).
2. Set up database (PostgreSQL recommended) + Flyway/Liquibase migration for initial schema.
3. Build Spring Data repositories for each entity.
4. Add `application.yml` config (DB, MinIO, Stripe keys — via env vars).
5. Start Auth module (Spring Security + JWT) — `User` entity needs password hashing wired in.
6. Basic Project CRUD API + ProjectMember invite flow.

---

## 📝 How to Use This Log

Each entry should answer:
- What did I set out to do today?
- What did I actually finish?
- What decisions did I make, and why (so I don't forget the reasoning in 3 months)?
- What's broken / incomplete?
- What's the very next concrete step?

This log doubles as interview prep — it's proof you can articulate design decisions, not just write code.