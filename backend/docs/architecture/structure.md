# LogiCore Backend - Project Structure

This document reflects the **actual current structure** of the backend. It does not
describe features or files that do not exist.

## Root

```text
backend/
├── AGENTS.md                      Agent instructions (how Agents work)
├── pom.xml                        Maven project configuration
├── Dockerfile                     container configuration
├── mvnw / mvnw.cmd                Maven Wrapper
├── docs/
│   ├── README.md                 (testing documentation)
│   ├── architecture/             current backend architecture
│   │   ├── overview.md
│   │   └── structure.md
│   └── conventions/              implementation patterns
│       └── backend.md
├── src/
│   ├── main/
│   │   ├── java/br/com/logicore/
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/     Flyway migrations (V1__..V4__)
│   └── test/java/br/com/logicore/
└── target/                        build output (never treated as source)
```

> `docs/conventions/` did not exist previously; `backend.md` was created from the audit.
> `docs/specifications/` is not present. Do not invent specification files.

## Main Source

Production code:

```text
src/main/java/br/com/logicore/
```

Tests:

```text
src/test/java/br/com/logicore/
```

## Layers (package root: `br.com.logicore`)

### Core Application

`BackendApplication.java` — Spring Boot entry point.

### Common (`common/`)

Shared functionality used across modules:

- `common/config/` — `CorsConfig` (CORS for `/api/**`).
- `common/dto/` — `PageResponse<T>` (shared list envelope).
- `common/exception/` — `BusinessException`, `ResourceNotFoundException`,
  `DuplicateResourceException`, `ErrorResponse`, `GlobalExceptionHandler`.

Avoid moving feature-specific logic into `common/`.

### Config (`config/`)

Application-level configuration:

- `config/OpenApiConfig` — OpenAPI definition.

### Modules (`modules/`)

Business and technical modules:

```text
modules/
├── address/     controller dto entity mapper repository/spec service validator
├── cargo/       controller dto entity mapper repository/spec service validator
├── department/  controller dto entity enums mapper repository/spec service validator
├── employee/    controller dto entity mapper repository/spec service validator
└── health/      controller dto service
```

Each business module follows the standard structure described in `AGENTS.md`. `department`
is the reference implementation; `health` is a technical module (no CRUD conventions).

## Standard Business Module

The Department module represents the primary CRUD structure:

```text
department/
├── controller/
├── dto/
├── entity/
├── enums/
├── mapper/
├── repository/
│   └── spec/
├── service/
└── validator/
```

Not all packages are required for every module. Follow the requirement of the feature and
the existing patterns (e.g. `address` has no `enums/`; `employee` currently lacks tests).

## Resources

```text
src/main/resources/
├── application.properties
└── db/migration/
    ├── V1__create_table_departamento.sql  -> table "department"
    ├── V2__create_table_cargo.sql         -> table "cargo"
    ├── V3__create_table_endereco.sql      -> table "address"
    └── V4__create_table_employee.sql      -> table "employee"
```

Flyway is enabled and migrations are the source of schema truth (`ddl-auto=validate`).

## Tests

Tests mirror the production module/package layout under `src/test/java/...`:

```text
modules/
├── address/     controller mapper service validator repository/spec
├── cargo/       controller mapper service validator repository/spec
├── department/  controller mapper service validator repository/spec
└── (employee)  no tests yet
```

## Responsibilities Summary

| Area | Responsibility |
|------|---------------|
| Controller | HTTP mapping, input validation (`@Valid`), response wrapping, OpenAPI |
| Service | Business rules, transactions, orchestration of mapper/validator/repo |
| Repository | Persistence, derived queries, specification execution |
| Specification | Reusable dynamic filters |
| Mapper | Entity <-> DTO conversion (manual) |
| Validator | Uniqueness and domain rules |
| DTO | API input/output contracts |
| Entity | Persisted domain model |
| Common | Truly cross-module concerns |
| Config | Application-wide configuration |
