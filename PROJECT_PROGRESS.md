# Progress Log — LLM-Powered Full-Stack Application Generator

Tracking daily/weekly progress on this project. **New entries go at the bottom** (chronological order — oldest first, newest last) so the log reads top-to-bottom like a story.

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

## 📅 2026-07-16 — Day 2: Controller Layer + Service Contracts

**Goal for the day:** Wire up the REST API surface — controllers, DTOs, and service interfaces — without touching business logic yet.

### ✅ Completed
- **Controllers** (`com.rajnish.controller`):
  - `AuthController` — `/api/auth/signup`, `/api/auth/login`, `/api/auth/me`
  - `ProjectController` — `/api/project` full CRUD (list, get by id, create, update, soft-delete)
  - `ProjectMemberController` — `/api/projects/{id}/members` (list, invite, update role, remove)
  - `FileController` — `/api/projects/{projectId}/files` (file tree + file content by path)
  - `BillingController` — `/api/plans`, `/api/me/subscription`, `/api/stripe/checkout`, `/api/stripe/portal`
  - `UsageController` — `/api/usage/today`, `/api/usage/limits`
- **Service interfaces** (contracts only, no implementation yet): `AuthService`, `ProjectService`, `ProjectMemberService`, `FIleService`, `SubscriptionService`, `PlanService`, `UsageService`, `UserService`
- DTOs referenced across `dto.auth`, `dto.project`, `dto.member`, `dto.file`, `dto.subscriptions` packages (request/response separation already in place)

### 🧠 Design Decisions / Notes
- Kept controllers **thin** — no business logic inside them, purely routing + delegating to service interfaces. This keeps the controller layer testable and swappable later.
- Used `@RequiredArgsConstructor` (Lombok) for constructor injection everywhere — no field injection.
- Request/response DTOs are separated by direction (`request` / `response` sub-packages) instead of reusing one DTO both ways — avoids leaking internal fields back to clients.
- `FileController` uses `@GetMapping("/{*path}")` for nested file paths so nested folder structures can be fetched with a single endpoint.

### ⚠️ Known Gaps / Bugs to fix next
- **`Long userId = 1L` is hardcoded in every controller method.** This is a placeholder until auth is wired in — replace with the authenticated principal (from JWT / `SecurityContext`) before anything goes near real users. Flagging this loudly so it isn't forgotten.
- Typo: `FIleService` should be `FileService` (capital I) — rename before it spreads further into implementations.
- Typo: `UsageController.etPlanLimits()` should be `getPlanLimits()` — currently won't map cleanly to intent/tests referencing it.
- No `@Valid` on some request bodies (e.g. `ProjectRequest`, `SignupRequest`, `LoginRequest`) — only `ProjectMemberController` has validation so far. Add `@Valid` + Bean Validation annotations on all request DTOs.
- No global exception handler (`@ControllerAdvice`) yet — right now any service exception will bubble up as a raw 500.
- Service interfaces have zero implementations yet — nothing actually runs end-to-end.

### 🔜 Next Steps
1. Fix the two typos (`FIleService` → `FileService`, `etPlanLimits` → `getPlanLimits`).
2. Add Spring Security + JWT so `userId` comes from the authenticated principal, not a hardcoded `1L`.
3. Add `@Valid` + validation annotations consistently across all request DTOs.
4. Add a global `@ControllerAdvice` for consistent error responses.
5. Start implementing service classes (business logic) — probably `AuthService` and `ProjectService` first since everything else depends on a real user/project existing.

---

## 📅 2026-07-17 — Day 3: Repository Layer, MapStruct, First Service Implementation

**Goal for the day:** Get one full vertical slice working — `Project` — from DB query up through mapping to response DTOs.

### ✅ Completed
- **Repository layer**: `ProjectRepository`, `UserRepository` (Spring Data JPA)
  - `findAllAccessibleByUser` — custom JPQL query, excludes soft-deleted projects, ordered by `updatedAt DESC`
  - `findAccessibleProjectById` — fetch single project by id, excludes soft-deleted
- **MapStruct** introduced for entity → DTO mapping: `ProjectMapper` (`toProjectResponse`, `toProjectSummaryResponse` with field rename `name` → `projectName`, `toListOfProjectSummaryResponse`)
- **First real service implementation**: `ProjectServiceImpl`
  - `createProject` — fully implemented (fetch owner, build project, save, map to response)
  - `getUserProjects` — fully implemented using the repository + mapper
  - `getProjectById`, `updateProject`, `softDelete` — stubbed (return `null` / empty for now)
- **Database config**: PostgreSQL wired via `application.yml`, Hibernate `ddl-auto: update`, `llm-fullstack-gen` app name, running on port `8000`

### 🧠 Design Decisions / Notes
- Chose **MapStruct** over manual mapping or ModelMapper — compile-time generated mappers, no reflection overhead, and mapping mismatches (like `name` → `projectName`) get caught at build time.
- `ddl-auto: update` is fine for solo local development right now, but this is explicitly a **temporary convenience** — flagged below as a gap since it's unsafe for anything beyond local dev.

### ⚠️ Known Gaps / Bugs to fix next
- **Security: hardcoded DB password in `application.yml`** (`DB_PASSWORD:Ranjit24` as a default fallback). This must never reach a public (or even private) remote as-is. Fix: remove the default value, rely purely on the env var, and rotate the DB password since it was written in plaintext.
- **`findAccessibleProjectById` doesn't actually filter by `userId`** — the `@Param("userId")` is declared but never used in the JPQL query. Right now *any* user could fetch *any* project by id as long as it isn't soft-deleted. This needs an ownership/membership check in the query (or in the service layer) before `getProjectById` is implemented on top of it.
- `getProjectById`, `updateProject`, `softDelete` are still stubs — no logic yet.
- `getUserProjects` has a commented-out duplicate implementation left in the file — clean this up (dead code).
- `ddl-auto: update` should move to Flyway/Liquibase migrations before this goes anywhere near a shared or production database.
- `show-sql: true` is fine for now but should be turned off (or moved to a dev-only profile) later — noisy and slightly risky in shared logs.

### 🔜 Next Steps
1. Fix the `application.yml` password issue immediately (see note above).
2. Fix `findAccessibleProjectById` to actually scope by `userId` (ownership or membership check).
3. Implement `getProjectById`, `updateProject`, `softDelete` in `ProjectServiceImpl`.
4. Remove the commented-out dead code in `getUserProjects`.
5. Start `UserMapper` + `UserServiceImpl` / `AuthServiceImpl` next, since `ProjectServiceImpl` currently does a raw `userRepository.findById(userId).orElseThrow()` with no real auth backing it yet.

---

## 📅 2026-07-17 — Day 4: Project Service Completed + Project Member Service

**Goal for the day:** Finish the remaining `ProjectService` stubs, and build out the full member/collaborator flow (`ProjectMemberService`).

### ✅ Completed
- **`ProjectServiceImpl` fully implemented** (was previously stubbed):
  - `getProjectById` — fetches via `getAccessibleProjectById` helper, maps to response
  - `updateProject` — fetches, checks the caller is the owner, updates name, saves
  - `softDelete` — fetches, checks the caller is the owner, sets `deletedAt`, saves
  - Introduced a shared internal helper `getAccessibleProjectById(projectId, userId)` used by all four methods
- **`ProjectMemberServiceImpl`** — full implementation:
  - `getProjectMembers` — returns the owner (mapped specially, since the owner isn't a row in `ProjectMember`) plus all rows from `ProjectMember`
  - `inviteMember` — owner-only check, looks up invitee by email, blocks self-invite, blocks duplicate invites (via `existsById` on the composite key), saves
  - `updateMemberRole` — fetches project + member, updates role, saves
  - `removeProjectMember` — owner-only check, verifies membership exists, deletes by composite key
- **`ProjectMemberMapper`** (MapStruct) — two mapping paths: one for the `User` who is the project owner (role hardcoded to `OWNER` via `constant`), one for actual `ProjectMember` rows
- **`ProjectMemberRepository.findByProjectId`**, **`UserRepository.findByEmail`** added

### 🧠 Design Decisions / Notes
- Owner is **not** stored as a `ProjectMember` row — it's derived from `Project.owner` and mapped separately with a hardcoded `OWNER` role via MapStruct's `constant`. This avoids a redundant membership row for every project's creator, but means any query for "all people with access to a project" has to combine both sources (as `getProjectMembers` does).
- Reused the same `getAccessibleProjectById` pattern across both `ProjectServiceImpl` and `ProjectMemberServiceImpl` — good for consistency, though it's currently duplicated in both classes rather than shared from one place (see gaps below).

### ⚠️ Known Gaps / Bugs to fix next
- **`updateMemberRole` has no owner-only permission check.** `inviteMember` and `removeProjectMember` both verify `project.getOwner().getId().equals(userId)` before proceeding, but `updateMemberRole` skips this — right now any project member (not just the owner) could promote/demote roles. Add the same check here.
- **`findAccessibleProjectById` still doesn't filter by `userId` in the query** (carried over from Day 3 — not addressed yet). This is now used by *more* code paths (`ProjectMemberServiceImpl` too), so it's a growing risk — worth fixing before adding more features on top of it.
- Errors are all raised as plain `RuntimeException` with string messages (e.g. `"cannot invite yourself"`, `"member not found in project"`). These will surface as generic 500s to API clients right now. Needs custom exception types (e.g. `ForbiddenException`, `NotFoundException`, `ConflictException`) + the still-pending global `@ControllerAdvice`.
- `getAccessibleProjectById` is duplicated verbatim in both `ProjectServiceImpl` and `ProjectMemberServiceImpl` — pull this into a shared place (e.g. a small internal helper/service or a repository method used directly) instead of copy-pasting.
- `removeProjectMember`'s error message says `"Not allowed to invite member"` — copy-pasted from `inviteMember`, should say `"Not allowed to remove member"`.

### 🔜 Next Steps
1. Add the missing owner check in `updateMemberRole`.
2. Fix the copy-pasted error message in `removeProjectMember`.
3. Fix `findAccessibleProjectById` to properly scope by `userId` — this is now overdue.
4. Introduce custom exception classes + a global `@ControllerAdvice` instead of raw `RuntimeException`.
5. De-duplicate `getAccessibleProjectById` between the two service classes.
6. Move on to `AuthServiceImpl` — everything so far still relies on a hardcoded/manual `userId`, not a real authenticated principal.

---

## 📝 How to Use This Log

Each entry should answer:
- What did I set out to do today?
- What did I actually finish?
- What decisions did I make, and why (so I don't forget the reasoning in 3 months)?
- What's broken / incomplete?
- What's the very next concrete step?

This log doubles as interview prep — it's proof you can articulate design decisions, not just write code.