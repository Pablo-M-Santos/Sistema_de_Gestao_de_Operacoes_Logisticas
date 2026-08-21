# Backend Conventions

Consolidated implementation conventions **observed in the current codebase**. Follow these
patterns when adding or modifying code. Do not introduce new patterns without reason.

## General

- Modules are organized as packages under `src/main/java/br/com/logicore/modules/<module>/`.
- Business modules use: `controller/`, `dto/`, `entity/`, `mapper/`, `repository/`,
  `repository/spec/`, `service/`, `validator/` (plus `enums/` when needed).
- Prefer existing patterns over new ones.
- Use explicit types; follow existing naming.

## Controllers

- Constructor injection (explicit constructor, no Lombok `@RequiredArgsConstructor` in
  business modules).
- Always return `ResponseEntity<T>`.
- REST conventions: base path `/api/v1/<plural-resource>` (e.g. `/api/v1/departments`).
- Use `@RestController` + `@RequestMapping`.
- Annotate business controllers with OpenAPI: `@Tag`, `@Operation`, `@ApiResponses`,
  `@Parameter`.
- Validate input with `@Valid` on request bodies.
- Status codes: `201` create, `200` read/update, `204` delete/activate/deactivate,
  `400` validation/business, `404` not found, `409` duplicate.
- Do not put business logic in controllers; delegate to the Service.

## DTOs

- Naming: `Create<X>Request`, `Update<X>Request`, `<X>Response`, `<X>SummaryResponse`.
- `<X>SummaryResponse` holds **aggregate statistics**, not a trimmed entity.
- Request DTOs carry Bean Validation annotations (`@NotBlank`, `@Size`, `@NotNull`,
  `@Email`).
- Response DTOs are immutable-style (`@Getter` + `@Builder`, sometimes `@Data`).
- Field names are in **Portuguese** (`nome`, `descricao`, `sigla`, `criadoEm`, `cep`,
  `matricula`) while class names and endpoints are in English — keep this convention.

## Services

- `@Service` with constructor injection.
- `@Transactional` on write methods; `@Transactional(readOnly = true)` on reads.
- Business rules, validation orchestration and persistence calls live here.
- Throw `ResourceNotFoundException` for missing entities. A private `find<X>ById(id)`
  helper is the established pattern (Department/Cargo/Address).
- Interact with the repository only through its interface.

## Repositories

- Extend `JpaRepository<Entity, Long>` and `JpaSpecificationExecutor<Entity>`.
- Annotate with `@Repository` (required for consistency; `employee` currently misses it).
- Use derived queries (`existsBy...`, `findBy...`, `countBy...`) and, when needed,
  `@Query` (JPQL).
- Keep queries in the repository; filters belong in `spec/`.

## Specifications

- Place in `repository/spec/` as `public final class` with a private constructor.
- Provide `static` methods returning `Specification<T>`.
- Return `null` from the predicate when the filter does not apply (empty/blank/null).
- Combine with `Specification.where(...).and(...)`.
- Reference attributes by String name (`root.get("nome")`).
- A `withSearch(...)` method is the common pattern: case-insensitive `LIKE` over several
  columns.

## Mapper

- Manual `@Component` (no MapStruct).
- Use Lombok `@Builder` on entities and responses.
- `toEntity(Create<X>Request)` builds the entity (defaults, e.g. `pais = "Brasil"`, may be
  applied here).
- `toResponse(Entity)` builds the response.
- Updates are applied field-by-field in the Service, not via the mapper.

## Validators

- `@Component`, constructor-injected repository.
- Encapsulate uniqueness checks (`validateUniqueX`, `validateUniqueXForUpdate`) and
  domain rules.
- Throw `DuplicateResourceException` for uniqueness conflicts.
- Throw `BusinessException` for domain rule violations.
- Pure-domain validators (no repository) are also acceptable (e.g. Address coordinate/state
  rules).

## Entities

- Lombok: `@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder`.
- `Long` id with `@GeneratedValue(strategy = GenerationType.IDENTITY)`.
- Audit timestamps via `@CreationTimestamp` / `@UpdateTimestamp`
  (`criadoEm`, `atualizadoEm`), mapped to snake_case columns.
- Relationships: `@ManyToOne(fetch = LAZY)` with `@JoinColumn`; `@OneToOne` for optional
  references.
- Status: strategy differs per module — see `AGENTS.md` section 9 (do not invent a new
  convention).

## Exceptions

- `BusinessException` (base, `RuntimeException`).
- `ResourceNotFoundException extends BusinessException` -> 404.
- `DuplicateResourceException extends BusinessException` -> 409.
- `GlobalExceptionHandler` (`@RestControllerAdvice`) maps exceptions to `ErrorResponse`
  (`timestamp, status, error, message, path`). Validation errors return a field map.

## Pagination

- List endpoints return `PageResponse<T>`.
- Accept `Pageable` with `@PageableDefault(size = 20, sort = "id", direction = ASC)`.
- `PageResponse` wraps `content, page, size, totalElements, totalPages`.

## Tests

- Controller: `@WebMvcTest` + `MockMvc` + `@MockitoBean` service + Jackson 3
  `ObjectMapper` (`tools.jackson.databind.ObjectMapper`) + `jsonPath` assertions.
- Service: `@ExtendWith(MockitoExtension.class)` + `@Mock` (repo/mapper/validator) +
  `@InjectMocks` + AssertJ.
- Mapper: pure JUnit, real instance via `new`, no mocks.
- Validator: unit tests, mock the repository as needed.
- Specification: `@DataJpaTest` + real repository + H2.
- Test method naming: `should<Behavior>[When<Condition>]`.

## What NOT to do

- Do not modify frontend, unrelated modules, API contracts or the database schema without
  approval.
- Do not introduce dependencies, duplications or unnecessary refactors.
- Do not invent specifications or assume `README.md` reflects the current state.
- Do not copy known problems from older modules (e.g. Employee's missing `toEntity`,
  repository coupling, `String` status, qualified names in code).
