# LogiCore Backend - Architecture Overview

This document describes the **architecture currently implemented** in the backend.
It is the source of truth for "how the backend works today". Product vision and future
evolution belong to the root `README.md` and are intentionally not duplicated here.

## Technology (current)

| Concern | Technology |
|---------|-----------|
| Language | Java 17 |
| Framework | Spring Boot 4.1.0 (Web MVC starter) |
| Persistence | Spring Data JPA + Hibernate |
| Database | PostgreSQL (runtime), H2 (tests) |
| Migrations | Flyway |
| Schema mode | `ddl-auto=validate` |
| API docs | Springdoc OpenAPI |
| Build | Maven |
| Boilerplate | Lombok (manual mappers, no MapStruct) |
| Tests | JUnit 5, Mockito, AssertJ, JaCoCo |

> Spring Security, RabbitMQ, Redis, WebSocket, S3, Google Maps, Kubernetes and CI/CD are
> **planned/future** (described in `README.md`). They are **not** part of the current
> backend and must not be treated as implemented.

## Package Root

`br.com.logicore`

## Layered Architecture

```text
Client / Frontend (Next.js, base URL /api/v1)
        |
        v
Controller (@RestController, @RequestMapping("/api/v1/..."))
        |  validates @Valid request DTOs
        |  returns ResponseEntity<T> / PageResponse<T>
        v
Service (@Service, @Transactional)
        |  business rules, validation orchestration
        |  uses Mapper, Validator, Repository
        v
Repository (JpaRepository + JpaSpecificationExecutor)
        |  persistence and derived queries
        v
Database (PostgreSQL, schema managed by Flyway)
```

Cross-cutting concerns:

- `common/exception/GlobalExceptionHandler` (`@RestControllerAdvice`) centralizes error
  responses (`ErrorResponse`).
- `common/dto/PageResponse<T>` is the standard list envelope.
- `common/config/CorsConfig` configures CORS for `/api/**`.

## Modules

Located under `modules/`:

| Module | Type | Notes |
|--------|------|-------|
| `department` | business | Primary CRUD reference; `enum` status |
| `cargo` | business | Simple entity; `Boolean ativo`; two unique keys |
| `address` | business | No status; multi-filter; real `DELETE` |
| `employee` | business | Relationships (ManyToOne/OneToOne); **no tests** |
| `health` | technical | Liveness endpoint; not a CRUD reference |

## REST API

- Base path: `/api/v1/<plural-resource>`.
- Controllers expose HTTP endpoints; Services contain business logic; Repositories handle
  persistence; Specifications contain reusable query/filter logic; DTOs represent API
  input/output contracts; Mappers convert between entities and DTOs; Validators contain
  feature-specific validation rules.
- Standard endpoints per business module: `POST`, `GET` (paginated + filters), `GET /{id}`,
  `PUT /{id}`, `GET /summary`, plus status/delete operations depending on the module.
- All business controllers are documented with OpenAPI annotations
  (`@Tag`, `@Operation`, `@ApiResponses`, `@Parameter`). `health` is not.

## DTOs, Mapper and Entities

- DTO naming: `Create<X>Request`, `Update<X>Request`, `<X>Response`, `<X>SummaryResponse`.
- `<X>SummaryResponse` carries **aggregate statistics** (e.g. `total/active/inactive`,
  `withCoordinates`, `withAddress`), not a trimmed entity view.
- Mappers are manual `@Component` classes using Lombok `@Builder`; they convert
  `Create<X>Request -> Entity` (`toEntity`) and `Entity -> <X>Response` (`toResponse`).
  Updates are applied field-by-field inside the Service (no `toEntity` for updates).
- Entities use Lombok (`@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`),
  `Long` IDs with `GenerationType.IDENTITY`, and Hibernate `@CreationTimestamp` /
  `@UpdateTimestamp` for `criadoEm` / `atualizadoEm`.

## Specifications

Dynamic filters are implemented as `public final` specification classes with a private
constructor and static methods returning `Specification<T>`. A `null` predicate means the
filter does not apply. Filters are combined with `Specification.where(...).and(...)`.
Attribute names are referenced as String literals (no JPA metamodel).

## Persistence and Migrations

- Flyway migrations live in `src/main/resources/db/migration/` (`V1__` through `V4__`).
- `spring.jpa.hibernate.ddl-auto=validate` ensures the schema matches the entities.
- `src/test/resources` does not currently exist; there is no dedicated test profile.

## Exceptions

Centralized via `GlobalExceptionHandler`:

- `ResourceNotFoundException` -> 404
- `DuplicateResourceException` -> 409
- `BusinessException` -> 400
- `MethodArgumentNotValidException` -> 400 (field errors map)
- generic `Exception` -> 500

Business exceptions extend `BusinessException`; `ResourceNotFoundException` and
`DuplicateResourceException` extend it.

## Testing

- Controller: `@WebMvcTest` + `MockMvc` + `@MockitoBean` service + Jackson 3 `ObjectMapper`.
- Service: `@ExtendWith(MockitoExtension.class)` + `@Mock` + `@InjectMocks` + AssertJ.
- Mapper: pure unit tests (real instance).
- Validator: unit tests (mocked repository).
- Specification: `@DataJpaTest` + real repository + H2.

Coverage status (current, not assumed complete): `department`, `cargo`, `address` have
test suites; `employee` has none.

## Current vs Planned

| Capability | Status |
|------------|--------|
| CRUD modules (Department/Cargo/Address/Employee) | Current |
| Flyway + PostgreSQL | Current |
| OpenAPI documentation | Current |
| Automated tests (most modules) | Current |
| Spring Security / auth | Planned (not implemented) |
| Structured logging | Planned (not implemented) |
| Messaging / cache / realtime / cloud storage | Future (README) |
