# Permissao Module Implementation Plan

## Context
Implement the `Permissao` module in the LogiCore backend. Permissao is a standalone administrative entity with `id`, `nome`, `descricao`, and timestamps. It follows the exact same pattern as `Perfil`.

## Key Decisions

### 1. Timestamps
- Add `criado_em` and `atualizado_em` to Permissao, following the project convention established by Perfil and all other entities.

### 2. Status / Soft Delete
- No `status` field.
- Physical DELETE. No soft delete.

### 3. Migration
- Next Flyway version: `V10__create_table_permissao.sql`
- Table `permissao` with:
  - `id BIGSERIAL PRIMARY KEY`
  - `nome VARCHAR(100) NOT NULL UNIQUE`
  - `descricao VARCHAR(255)`
  - `criado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP`
  - `atualizado_em TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP`
  - Index on `nome`

### 4. Entity
- `Permissao.java` with `@Entity`, `@Table(name = "permissao")`, Lombok, timestamps.
- No relationships to `Usuario`, `Perfil`, or any other module.

### 5. DTOs
- `CreatePermissaoRequest` — nome, descricao
- `UpdatePermissaoRequest` — nome, descricao
- `PermissaoResponse` — id, nome, descricao, criadoEm, atualizadoEm
- `PermissaoSummary` — total count

### 6. Mapper
- `PermissaoMapper` with `toEntity(CreatePermissaoRequest)`, `toResponse(Permissao)`.

### 7. Repository
- `PermissaoRepository` extends `JpaRepository<Permissao, Long>`, `JpaSpecificationExecutor<Permissao>`
- Methods: `findByNome`, `existsByNome`, `existsByNomeAndIdNot`

### 8. Specifications
- `PermissaoSpecifications` with `withSearch(String)` — LIKE on `nome` and `descricao`.

### 9. Validator
- `PermissaoValidator` validates unique nome on create and update.

### 10. Service
- `PermissaoService` with create, findById, findAll (paginated + specs), update, delete (physical).
- Constructor injection.

### 11. Controller
- `PermissaoController` at `/api/v1/permissoes`
- Endpoints: POST, GET, GET/{id}, PUT/{id}, DELETE/{id}, GET/summary
- OpenAPI annotations following existing pattern.

### 12. Tests
- `PermissaoControllerTest`
- `PermissaoServiceTest`
- `PermissaoMapperTest`
- `PermissaoValidatorTest`
- `PermissaoSpecificationsIntegrationTest`

## Validation Steps
1. Run Permissao module tests.
2. Run full backend test suite.
3. Run Maven build.
4. Verify migration was created and no prior migrations were modified.
5. Verify no existing modules were changed.
6. Confirm no Usuario/Perfil relationships were added.

## Files to Create
- `src/main/resources/db/migration/V10__create_table_permissao.sql`
- `src/main/java/br/com/logicore/modules/permissao/entity/Permissao.java`
- `src/main/java/br/com/logicore/modules/permissao/dto/CreatePermissaoRequest.java`
- `src/main/java/br/com/logicore/modules/permissao/dto/UpdatePermissaoRequest.java`
- `src/main/java/br/com/logicore/modules/permissao/dto/PermissaoResponse.java`
- `src/main/java/br/com/logicore/modules/permissao/dto/PermissaoSummary.java`
- `src/main/java/br/com/logicore/modules/permissao/mapper/PermissaoMapper.java`
- `src/main/java/br/com/logicore/modules/permissao/repository/PermissaoRepository.java`
- `src/main/java/br/com/logicore/modules/permissao/repository/spec/PermissaoSpecifications.java`
- `src/main/java/br/com/logicore/modules/permissao/validator/PermissaoValidator.java`
- `src/main/java/br/com/logicore/modules/permissao/service/PermissaoService.java`
- `src/main/java/br/com/logicore/modules/permissao/controller/PermissaoController.java`
- `src/test/java/br/com/logicore/modules/permissao/controller/PermissaoControllerTest.java`
- `src/test/java/br/com/logicore/modules/permissao/service/PermissaoServiceTest.java`
- `src/test/java/br/com/logicore/modules/permissao/mapper/PermissaoMapperTest.java`
- `src/test/java/br/com/logicore/modules/permissao/validator/PermissaoValidatorTest.java`
- `src/test/java/br/com/logicore/modules/permissao/repository/spec/PermissaoSpecificationsIntegrationTest.java`
