# LogiCore Backend - Agent Instructions

## 1. Identity

Backend of:

```text
LogiCore — Sistema de Gestão de Operações Logísticas
```

Modular Spring Boot application written in Java.

> The root `README.md` describes the product vision and planned future evolution.
> It is **not** an inventory of the current backend. Do not treat it as such.

## 2. Current Stack

Documented as the current state of the backend:

```text
Java 17
Spring Boot 4.1.0
Spring Web MVC
Spring Data JPA
Hibernate
PostgreSQL
Flyway
Maven
Springdoc OpenAPI
Lombok
JUnit 5
Mockito
AssertJ
H2 (tests)
JaCoCo
```

Do **not** document Java 21 or Spring Security as implemented. The root `README.md`
may mention them as **planned/future**; that is intentional and must not be changed here
(Java 21 in the README is a future target; Spring Security is a planned evolution).

## 3. Documentation Hierarchy (Sources of Truth)

```text
AGENTS.md
    -> Agent working rules

docs/architecture/
    -> Current backend architecture

docs/conventions/
    -> Implementation patterns

docs/specifications/
    -> Specific module requirements, when they exist

Existing code
    -> Source of truth for the implementation

README.md
    -> Product vision and planned evolution (NOT current-state inventory)
```

Consult the minimal relevant scope before implementing:

1. `AGENTS.md`
2. Relevant architecture documentation
3. Relevant convention documentation
4. Specification, **when it exists**
5. Closest existing module
6. Only then, additional files when required

## 4. Primary Reference Module

`modules/department` is the **primary reference for standard CRUD modules**.

It is currently the most complete backend module and provides:

- CRUD
- search
- filters
- pagination
- sorting
- summary
- status (enum)
- specifications
- validator
- mapper
- tests
- OpenAPI documentation
- full backend + frontend flow

When a new module behaves like a standard administrative CRUD, **Department must be the
first reference**.

## 5. Complementary References

Do not treat Department as the absolute reference for every scenario. Use other modules
as complementary references when they have specific characteristics.

### Department

Primary reference for:

- standard CRUD
- pagination
- search
- filters
- summary
- status
- tests
- full backend/frontend flow

### Cargo

Complementary reference for:

- multiple uniqueness rules
- simple administrative entity
- active/inactive filtering

### Address

Complementary reference for:

- multiple filters
- more complex Specifications
- partial update
- domain-specific validations
- entity without status

### Employee

**Do not use as the primary architectural reference.**

Employee has cross-module relationships and represents a more complex scenario, but it
currently has inconsistencies and **no tests**. When studying relationships between
entities, Employee may be analyzed as an existing implementation example, but any new
code must follow the consolidated conventions and must **not** automatically reproduce its
problems (e.g. missing `mapper.toEntity`, cross-module repository coupling, missing
`@Repository`, `String` status, qualified names in code).

## 6. Module Structure

Business modules are organized under:

```text
src/main/java/br/com/logicore/modules/
```

A typical module contains:

- `controller/`
- `dto/`
- `entity/`
- `mapper/`
- `repository/`
- `repository/spec/`
- `service/`
- `validator/`
- `enums/` (only when needed, e.g. Department)

Not every module must contain every package. Follow the requirement of the feature and
existing patterns. `health` is a technical module (controller/dto/service) and is not a
business CRUD reference.

## 7. Agent Workflow

```text
Analyze
   |
   v
Plan
   |
   v
Present plan
   |
   v
Wait for approval
   |
   v
Implement
   |
   v
Test
   |
   v
Validate
```

Before modifying files:

1. Read `AGENTS.md`.
2. Read the relevant documentation.
3. Read the specification **if it exists**.
4. Identify the appropriate reference module.
5. Analyze existing patterns.
6. Present the plan.
7. Wait for approval.

Do not modify files before approval when the task requires planning.

## 8. Implementation Rules

- Follow existing architecture and conventions.
- Prefer existing patterns over introducing new ones.
- Do not duplicate existing functionality.
- Do not introduce dependencies without explicit approval.
- Do not modify frontend code.
- Do not modify unrelated modules.
- Do not change API contracts without approval.
- Do not change the database schema without approval.
- Avoid unnecessary refactoring.
- Preserve existing behavior.
- Use explicit types.
- Follow existing naming conventions.

### Consolidated patterns (observed in code)

- packages organized per module
- constructor injection (explicit; do not use Lombok `@RequiredArgsConstructor` in business modules)
- `ResponseEntity<T>` in controllers
- OpenAPI annotations on business controllers (no `@RestControllerAdvice`/health)
- `PageResponse<T>` as the list envelope
- pagination with `Pageable` + `@PageableDefault(size = 20, sort = "id", direction = ASC)`
- Specifications for dynamic filters
- centralized exceptions via `GlobalExceptionHandler`
- Flyway migrations (`db/migration/V*__*.sql`) + `ddl-auto=validate`
- `@Transactional` on services; `@Transactional(readOnly = true)` on reads
- `ResourceNotFoundException` for missing entities (thrown by a private `findById` helper)
- mapper as manual `@Component` (no MapStruct)

## 9. Status and Update Strategy (Important)

Current modules use **different status strategies**:

```text
Department -> enum DepartmentStatus (ACTIVE / INACTIVE)
Cargo      -> Boolean ativo
Address    -> no status
Employee   -> String status
```

Similarly, `PUT` semantics differ across modules:

- Department: full replacement (`@NotBlank` on update DTO fields)
- Cargo / Address / Employee: partial update (`null` fields are ignored)

**Do not invent a new status convention during module implementation.** For a new module:

1. analyze the specification;
2. analyze the domain;
3. check similar modules;
4. pick the most adequate strategy;
5. present the decision in the plan before implementing.

Do not assume every `PUT` must be total or partial. The semantics must be defined by the
specification and the pattern adequate to the module.

## 10. Tests

New functionality must have tests following the closest existing pattern.

Current test patterns:

```text
Controller  -> @WebMvcTest + MockMvc + @MockitoBean service + Jackson 3 ObjectMapper
Service     -> @ExtendWith(MockitoExtension.class) + @Mock + @InjectMocks + AssertJ
Mapper      -> pure unit tests (real instance, no mocks)
Validator   -> unit tests (mock repository)
Specification -> @DataJpaTest + real repository + H2
```

Do **not** claim that all current modules have complete coverage. As of this writing:

- `department`, `cargo`, `address` have test suites.
- `employee` has **no tests** (known pending item).

## 11. Validation

After implementation:

1. Run the relevant tests.
2. Run the full test suite when appropriate.
3. Run the Maven build when appropriate.
4. Review the changed files.
5. Report validation results.
6. Report assumptions and decisions.

Do not fix unrelated existing problems unless explicitly requested.

## 12. Approval Workflow

Before modifying files:

1. Analyze the specification.
2. Analyze the relevant existing module.
3. Identify files to create or modify.
4. Present the implementation plan.
5. Wait for approval.

After approval, implement only the requested scope.

## 13. Context Optimization

Do not scan the entire repository unless necessary.

Prefer:

1. `AGENTS.md`
2. Relevant architecture documentation
3. Relevant conventions
4. Relevant specification (when it exists)
5. Closest existing module
6. Only then additional files when required

Do not inspect generated or unrelated files unless explicitly required.

Avoid:

- `target/`
- `.git/`
- `.idea/`
- generated build files

## 14. Known Issues / Technical Debt

These are **known pending items**, not architectural conventions. Do not reproduce them in
new modules and do not "fix" them in this documentation task:

- `address` controller tests use outdated endpoints (`/api/addresses`); the controller now
  exposes `/api/v1/addresses`. Tests need updating.
- `employee` has no tests.
- Status strategy is inconsistent across modules (see section 9).
- `PUT` semantics are inconsistent across modules (see section 9).
- `employee` deviates from the pattern: no `mapper.toEntity`, injects other modules'
  repositories directly, missing `@Repository`, `String` status, qualified names in code.
- Divergences between Flyway migrations and entities (e.g. nullable columns).
- Possible relationship modeling issues (e.g. `employee.endereco` is `@OneToOne` without a
  unique constraint, while the product model suggests 1:N).
- No Spring Security in the current backend (planned, not implemented).
- No structured logging in the current backend.
- Detailed SQL logging is enabled by default (`show-sql`, bind TRACE) — review for
  non-development environments.

## 15. What NOT to do

- Do not modify frontend code.
- Do not change API contracts without approval.
- Do not change the database schema without approval.
- Do not introduce dependencies without approval.
- Do not duplicate functionality.
- Do not perform unnecessary refactoring.
- Do not alter unrelated modules.
- Do not invent specifications.
- Do not assume `README.md` represents the current state.
- Do not assume all modules share the same pattern.
- Do not copy known problems from older modules.
- Do not add Spring Security (or RabbitMQ, Redis, WebSocket, S3, Google Maps, Kubernetes,
  CI/CD) to the backend — they are planned/future, not implemented.
