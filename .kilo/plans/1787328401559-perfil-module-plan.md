# Perfil Module Implementation Plan

## Context
Implement the `Perfil` module in the LogiCore backend. Perfil is a standalone administrative entity with only `id`, `nome`, and `descricao`.

## Key Decisions

### 1. Timestamps
- **Add `criado_em` and `atualizado_em`** to Perfil.
- Reason: Every existing entity in the project (department, cargo, address, employee, client, vehicle, driver, usuario) includes these timestamps. The project convention is to have them on all entities.

### 2. Status / Soft Delete
- **No `status` field.** The user model does not include it.
- **Physical DELETE.** Since there is no status/ativo flag, soft delete is not applicable. All existing modules with status use soft delete; modules without status do not exist yet in this project, so physical delete is the natural fit.

### 3. Migration
- Next Flyway version: `V9__create_table_perfil.sql`
- Table `perfil` with:
  - `id BIGSERIAL PRIMARY KEY`
  - `nome VARCHAR(100) NOT NULL UNIQUE`
  - `descricao VARCHAR(255) NULL`
  - `criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP`
  - `atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP`
  - Index on `nome`

### 4. Entity
- `Perfil.java` with `@Entity`, `@Table(name = "perfil")`, Lombok, timestamps via `@CreationTimestamp` / `@UpdateTimestamp`.
- No relationships to `Usuario` or `Permissao`.

### 5. DTOs
- `CreatePerfilRequest` — nome, descricao
- `UpdatePerfilRequest` — nome, descricao
- `PerfilResponse` — id, nome, descricao
- `PerfilSummary` — total count (following DepartmentSummary/UsuarioSummary pattern)

### 6. Mapper
- `PerfilMapper` with `toEntity(CreatePerfilRequest)`, `toResponse(Perfil)`, and `updateEntity(Perfil, UpdatePerfilRequest)` following existing mapper patterns.

### 7. Repository
- `PerfilRepository` extends `JpaRepository<Perfil, Long>`, `JpaSpecificationExecutor<Perfil>`
- Methods: `findByNome`, `existsByNome`, `existsByNomeAndIdNot`

### 8. Specifications
- `PerfilSpecifications` with:
  - `withSearch(String)` — LIKE on `nome` and `descricao`

### 9. Validator
- `PerfilValidator` validates unique nome on create and update.

### 10. Service
- `PerfilService` with create, findById, findAll (paginated + specs), update, delete (physical).
- Uses constructor injection.

### 11. Controller
- `PerfilController` at `/api/v1/perfis`
- Endpoints: POST, GET, GET/{id}, PUT/{id}, DELETE/{id}
- No activate/deactivate endpoints.
- OpenAPI annotations following existing pattern.

### 12. Tests
- `PerfilControllerTest`
- `PerfilServiceTest`
- `PerfilMapperTest`
- `PerfilValidatorTest`
- `PerfilSpecificationsIntegrationTest`

## Validation Steps
1. Run Perfil module tests.
2. Run full backend test suite.
3. Run Maven build.
4. Verify migration was created and no prior migrations were modified.
5. Verify no existing modules were changed.
6. Confirm no Usuario/Permissao relationships were added.

## Files to Create
- `src/main/resources/db/migration/V9__create_table_perfil.sql`
- `src/main/java/br/com/logicore/modules/perfil/entity/Perfil.java`
- `src/main/java/br/com/logicore/modules/perfil/dto/CreatePerfilRequest.java`
- `src/main/java/br/com/logicore/modules/perfil/dto/UpdatePerfilRequest.java`
- `src/main/java/br/com/logicore/modules/perfil/dto/PerfilResponse.java`
- `src/main/java/br/com/logicore/modules/perfil/dto/PerfilSummary.java`
- `src/main/java/br/com/logicore/modules/perfil/mapper/PerfilMapper.java`
- `src/main/java/br/com/logicore/modules/perfil/repository/PerfilRepository.java`
- `src/main/java/br/com/logicore/modules/perfil/repository/spec/PerfilSpecifications.java`
- `src/main/java/br/com/logicore/modules/perfil/validator/PerfilValidator.java`
- `src/main/java/br/com/logicore/modules/perfil/service/PerfilService.java`
- `src/main/java/br/com/logicore/modules/perfil/controller/PerfilController.java`
- `src/test/java/br/com/logicore/modules/perfil/controller/PerfilControllerTest.java`
- `src/test/java/br/com/logicore/modules/perfil/service/PerfilServiceTest.java`
- `src/test/java/br/com/logicore/modules/perfil/mapper/PerfilMapperTest.java`
- `src/test/java/br/com/logicore/modules/perfil/validator/PerfilValidatorTest.java`
- `src/test/java/br/com/logicore/modules/perfil/repository/spec/PerfilSpecificationsIntegrationTest.java`
