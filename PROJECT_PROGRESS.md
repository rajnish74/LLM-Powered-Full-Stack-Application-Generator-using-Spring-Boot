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

## 📅 2026-07-23 — Day 5: Exception Handling, Security Config, Auth Signup

**Goal for the day:** Build a proper validation/exception layer, wire up Spring Security scaffolding, and implement the signup flow.

### ✅ Completed
- **Exception handling** (`com.rajnish.common.exceptions`):
  - `ApiError` record — standard error response shape (`status`, `message`, `timeStamp`, optional `errors` list, `NON_NULL` so `errors` is omitted when empty)
  - `ApiFieldError` record — per-field validation error (`field`, `message`)
  - `BadRequestException`, `ResourceNotFoundException` — custom exception types
  - `GlobalExceptionHandler` (`@RestControllerAdvice`) — handles `BadRequestException`, `ResourceNotFoundException`, and `MethodArgumentNotValidException` (bean validation failures), logs each error, returns a consistent `ApiError` response
- **Security scaffolding** (`com.rajnish.common.security`):
  - `WebSecurityConfig` — stateless session policy, CSRF disabled (appropriate for a stateless JSON API), `BCryptPasswordEncoder` bean, `AuthenticationManager` bean
  - **Note:** `/api/**` is currently `permitAll()` — this is intentionally open for now since there's no JWT filter yet; it is not real protection.
- **`AuthServiceImpl.signup`** implemented:
  - Checks for existing username, throws `BadRequestException` if taken
  - Maps request → entity via `UserMapper`, encodes password with `BCryptPasswordEncoder`, saves
  - Returns `AuthResponse` with a placeholder `"dummy"` token (real JWT generation commented out — `authUtils.generateAccessToken(user)` doesn't exist yet)
  - `login` is still a stub (returns `null`)

### 🧠 Design Decisions / Notes
- Went with a **generic `ApiError` shape** across all exception types instead of custom shapes per exception — makes frontend error handling predictable regardless of which endpoint failed.
- `ResourceNotFoundException` carries `resourceName` + `resourceId` separately rather than a pre-built message — lets `GlobalExceptionHandler` format the message consistently in one place instead of every throw-site writing its own string.
- Password hashing done at the service layer (not in the mapper or entity) — keeps `UserMapper` a pure structural mapper with no security-sensitive logic in it.

### ⚠️ Known Gaps / Bugs to fix next
- **`/api/**` is fully open (`permitAll()`) with no JWT filter yet.** This is expected at this stage but is a hard blocker before anything resembling real auth — every controller's hardcoded `userId = 1L` is still the actual security model right now, not Spring Security.
- **`login()` is unimplemented** — no way to actually authenticate yet, so signup produces a user but there's no way back in.
- Real JWT token generation is commented out — `authUtils` (or equivalent JWT utility class) needs to be built: token generation, token validation, and a filter to extract the authenticated user from the request.
- `ApiFieldError` is declared as a **package-private record** (no `public` modifier). It currently works because `GlobalExceptionHandler` is in the same package, but this is fragile — worth making it `public` explicitly so it can't silently break if the handler ever moves packages.
- `AuthServiceImpl` uses `userRepository.findByUsername(...)` and `user.setPassword(...)` — worth double-checking these fields/methods exist on the current `User` entity and `UserRepository`, since the entity as originally designed only had `email` / `passwordHash` (no separate `username`). If `username` was added since, note it in the domain model section of the README too.
- No rate limiting or attempt-throttling on `/api/auth/login` or `/api/auth/signup` yet — worth keeping in mind once `login` is implemented (brute-force protection).

### 🔜 Next Steps
1. Implement `login()` — verify credentials with `PasswordEncoder.matches()`, and issue a real token.
2. Build the JWT utility (generate + validate) and a `OncePerRequestFilter` to populate the security context from the token.
3. Lock down `WebSecurityConfig` — replace `permitAll()` with real rules (public: `/api/auth/**`; everything else: authenticated).
4. Replace hardcoded `userId = 1L` across all controllers with the authenticated principal from the security context — this has been a known gap since Day 2 and is now finally unblockable.
5. Confirm `User` entity fields match what `AuthServiceImpl` expects (`username`, `password` setter) and update the domain model docs if the entity changed.

---

## 📅 2026-07-24 — Day 6: JWT Auth Wired End-to-End, Ownership Model Reworked

**Goal for the day:** Replace the hardcoded `userId = 1L` everywhere with real authentication, and finish the login flow.

### ✅ Completed
- **JWT infrastructure** (`com.rajnish.common.security`):
  - `JwtUserPrinciple` (record) — `userId`, `username`, `authorities`
  - `AuthUtils` — `generateAccessToken(User)`, `verifyAccessToken(String)`, `getCurrentUserId()` (reads from `SecurityContextHolder`, throws `AuthenticationCredentialsNotFoundException` if no JWT principal present)
  - `JwtAuthFilter` (`OncePerRequestFilter`) — reads `Authorization: Bearer <token>`, verifies it, populates `SecurityContextHolder` with a `UsernamePasswordAuthenticationToken`
- **`User` entity is now fully JPA-mapped**: `@Entity`, `@Table(name = "users")`, `@Id @GeneratedValue`, `@CreationTimestamp`/`@UpdateTimestamp`, and now **implements `UserDetails`** (Spring Security). Fields are now `username` + `password` (matches what `AuthServiceImpl` was already using).
- **`AuthServiceImpl.login` implemented** — delegates to Spring's `AuthenticationManager`, extracts the authenticated `User`, issues a real JWT via `AuthUtils.generateAccessToken`.
- **`UserServiceImpl`** added, implements `UserDetailsService.loadUserByUsername` (needed by Spring Security for the `AuthenticationManager` to work). `getProfile()` is still a stub.
- **Removed `userId` parameters everywhere** — `ProjectService`, `ProjectMemberService` interfaces, their impls, and `ProjectController`/`ProjectMemberController` no longer take/pass `userId` at all. It's now pulled from `AuthUtils.getCurrentUserId()` inside each service method. **This finally closes the "hardcoded userId = 1L" gap that's been flagged since Day 2.**
- **`ProjectRepository` queries are now properly scoped by `userId`** via an `EXISTS` subquery against `ProjectMember` — **this closes the ownership-scoping security gap flagged on Day 3 and Day 4.**
- **Ownership model changed**: a project's owner is no longer just a `Project.owner` field — `createProject` now also inserts a `ProjectMember` row for the creator with `ProjectRole.OWNER`. Ownership/access is now fully expressed through `ProjectMember` rows.

### 🧠 Design Decisions / Notes
- Modeling the owner as a `ProjectMember` row (role = `OWNER`) instead of a separate `Project.owner` field unifies "who can access this project" into one table/query path — `getProjectMembers` no longer needs to special-case the owner separately (Day 4's `toProjectMemberResponseFromOwner` mapping path is no longer needed for this flow).
- `getCurrentUserId()` centralizes "who is making this request" in one place (`AuthUtils`), so services stay free of any direct `SecurityContextHolder` calls.

### 🚨 New Issue Introduced (higher priority than the "next steps" below)
- **Permission checks were lost in the ownership model change.** Previously, `inviteMember` and `removeProjectMember` checked `project.getOwner().getId().equals(userId)` before proceeding. Now that `Project.owner` no longer exists (ownership lives in `ProjectMember` rows), **that check was removed entirely rather than replaced** — `inviteMember`, `updateMemberRole`, and `removeProjectMember` now only verify the caller has *some* access to the project (via `getAccessibleProjectById`), not that they're specifically an `OWNER`. Right now any project member — including a `VIEWER` — can invite people, remove people, and change anyone's role. This needs a role check (e.g. fetch the caller's own `ProjectMember` row and assert `projectRole == OWNER`, or introduce an `EDITOR`/`OWNER`-only rule per action) before this goes further.

### ⚠️ Other Known Gaps / Bugs to fix next
- **`UserServiceImpl.loadUserByUsername` calls `ResourceNotFoundException` incorrectly**: `new ResourceNotFoundException("User not found with username: " + username, "User not found with id")` — the constructor expects `(resourceName, resourceId)`, but this passes a full sentence as the "name" and another sentence as the "id". `GlobalExceptionHandler` will format this into a garbled message. Fix: `new ResourceNotFoundException("User", username)`.
- **Confirm `JwtAuthFilter` is actually registered in `WebSecurityConfig`'s filter chain** (e.g. `.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)`) and that the `permitAll()` rule from Day 5 has been tightened to require authentication on protected routes. The filter class existing isn't enough by itself — this wasn't shown in today's changes, so it needs verifying (or is the very next thing to do if not done yet).
- **`jwt.secret-key` comes from `application.yml`** (`@Value("${jwt.secret-key}")`) — same category of risk as the DB password from Day 3. Confirm it's env-var-backed with no plaintext default committed to the repo.
- `createProject` uses `userRepository.getReferenceById(userId)` instead of `findById(...).orElseThrow(...)`. This returns a lazy proxy without hitting the DB immediately — if the user somehow doesn't exist, the failure will surface later (e.g. as a `EntityNotFoundException` on access) instead of a clean `ResourceNotFoundException` up front. Worth deciding intentionally whether that trade-off is acceptable here.
- `UserServiceImpl.getProfile()` is still unimplemented (`return null`) — `/api/auth/me` will break until this is done.
- Access token expiry is short (10 minutes) with no refresh token flow yet — fine for now, but note it before this feels like a bug in manual testing.

### 🔜 Next Steps
1. **Fix the permission regression** — add proper `OWNER`-only (or role-based) checks back into `inviteMember`, `updateMemberRole`, `removeProjectMember`.
2. Fix the malformed `ResourceNotFoundException` call in `UserServiceImpl`.
3. Verify/complete `JwtAuthFilter` registration in `WebSecurityConfig` + tighten `permitAll()`.
4. Confirm `jwt.secret-key` is not hardcoded in committed config.
5. Implement `UserServiceImpl.getProfile()`.
6. Start thinking about refresh tokens if 10-minute expiry becomes annoying during testing.

---

## 📝 How to Use This Log

Each entry should answer:
- What did I set out to do today?
- What did I actually finish?
- What decisions did I make, and why (so I don't forget the reasoning in 3 months)?
- What's broken / incomplete?
- What's the very next concrete step?

This log doubles as interview prep — it's proof you can articulate design decisions, not just write code.