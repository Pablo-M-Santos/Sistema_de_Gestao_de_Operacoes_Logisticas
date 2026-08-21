# Plano: Conclusão e Consistência do Módulo `address`

## Contexto

Módulo `address` (`src/main/java/br/com/logicore/modules/address/`) — CRUD de endereços sob
`/api/v1/addresses`, **sem status**, com update parcial, multi-filtros e `DELETE` real.
Referência para update parcial (padrão já adotado em `cargo`). Objetivo: torná-lo
funcional e testado, consistente com as convenções, **sem tocar** Department/Cargo/Employee/frontend.

## 1. Estado atual do Address

- **Controller** (`AddressController.java`): `@RequestMapping({"/api/v1/addresses"})`,
  endpoints POST/GET(paginado+5 filtros)/GET `/summary`/GET `/{id}`/PUT `/{id}`/DELETE `/{id}`;
  OpenAPI presente; 201/200/204.
- **DTOs**: `CreateAddressRequest` validado (`@NotBlank` em cep/logradouro/numero/bairro/cidade/estado,
  `@Size` cep=8, estado=2); `UpdateAddressRequest` sem `@NotBlank` (parcial); `AddressResponse`,
  `AddressSummaryResponse`.
- **Entity** (`Address.java`): campos consistentes com a migration `V3`; `pais` default "Brasil".
- **Mapper**: `toEntity` (default `pais="Brasil"`) + `toResponse`, manual `@Component`.
- **Repository**: `countWithCoordinates` (`@Query`); sem métodos de unicidade (endereço não tem
  unique constraint).
- **Specifications**: `withSearch`(8 colunas) + `withCep/withCidade/withEstado/withPais`; `null`=sem filtro.
- **Validator** (`AddressValidator.java`): `validateLatitude/-Longitude/-State` (puro, `BusinessException`).
- **Service** (`AddressService.java`): create/summary/findById/update(parcial via `isPresent`)/delete.
- **Migration** `V3__create_table_endereco.sql`: coerente com a entidade.
- **Testes**: controller (6), service (8), mapper (2), validator (6), spec IT (6).

## 2. Problemas encontrados e severidade

| # | Problema | Severidade | Evidência |
|---|----------|-----------|-----------|
| P1 | `AddressControllerTest` usa `/api/addresses` em 5/6 testes; controller mapeia só `/api/v1/addresses` → esses testes retornam 404 e **falham** | 🔴 Crítico (testes vermelhos) | `AddressControllerTest.java:67,119,144,156,172` |
| P2 | `AddressService.delete` faz `repository.delete` sem proteção; se o endereço é referenciado por `employee.endereco_id` (FK `fk_employee_endereco`), ocorre `DataIntegrityViolationException` → **500** | 🔴 Crítico (funcional) | `AddressService.java:138-144` |
| P3 | `@RequestMapping({"/api/v1/addresses"})` em formato de array (resíduo de dual-mapping já removido) | 🟢 Normal | cosmetic |

**Verificações que NÃO encontraram divergência (preservar):**
- Entity × Migration × API: consistentes (tamanhos, nullability, `pais` default).
- Update parcial: já correto e é a referência seguida por `cargo` (não mexer).
- Cobertura de service/mapper/validator/spec: adequada.

## 3. O que está correto e deve ser preservar

- Controller + OpenAPI + paths `/api/v1`.
- `PageResponse<T>`, `@PageableDefault`, Specifications estáticas.
- `isPresent` no update parcial (modelo para os demais módulos).
- `AddressValidator` puro (sem repositório).
- `AddressMapper` default `pais`.
- Migration `V3` coerente.
- Suíte service/mapper/validator/spec.

## 4. Arquivos que precisarão ser alterados

- `modules/address/controller/AddressControllerTest.java` — corrigir 5 paths (P1). **Obrigatório.**
- `modules/address/service/AddressService.java` — proteger `delete` (P2). **Obrigatório.**
- `modules/address/service/AddressServiceTest.java` — cobrir cenário de FK no delete (P2). **Obrigatório.**

Não alterar: `AddressController`, `AddressRepository`, `AddressSpecifications`, DTOs,
`AddressMapper`, `AddressValidator`, migration, Department, Cargo, Employee, frontend, `pom.xml`.

## 5. Testes a criar/ajustar

`AddressControllerTest` (ajuste):
- `shouldCreateAddressSuccessfully`: `/api/addresses` → `/api/v1/addresses`.
- `shouldFindAddressById`: `/api/addresses/1` → `/api/v1/addresses/1`.
- `shouldUpdateAddressSuccessfully`: `/api/addresses/1` → `/api/v1/addresses/1`.
- `shouldDeleteAddress`: `/api/addresses/1` → `/api/v1/addresses/1`.
- `shouldReturnAddressSummary`: `/api/addresses/summary` → `/api/v1/addresses/summary`.
- (O teste `shouldFindAllAddressesUsingVersionedEndpoint` já usa v1 — manter.)

`AddressServiceTest` (adição):
- `shouldThrowBusinessExceptionWhenDeleteReferencedAddress`: `repository.delete` lança
  `DataIntegrityViolationException` → serviço deve propagar `BusinessException`.
- (Opcional) `shouldDeleteAddress` já existe; manter.

## 6. Plano de implementação (ordem)

1. **Corrigir `AddressControllerTest` (P1):** substituir `/api/addresses` por `/api/v1/addresses`
   nos 5 testes. Sem alteração de código de produção.
2. **Proteger `AddressService.delete` (P2):** envolver `repository.delete(address)` +
   `repository.flush()` em `try/catch (DataIntegrityViolationException)` lançando
   `BusinessException("Address cannot be deleted because it is referenced by other records.")`.
   - Importar `org.springframework.dao.DataIntegrityViolationException` e
     `br.com.logicore.common.exception.BusinessException`.
   - `flush()` garante que a violação de FK dispara dentro da transação (capturável),
     em vez de 500 em commit.
   - `findAddressById` continua retornando 404 quando inexistente (sem alteração).
3. **Adicionar teste de FK no `AddressServiceTest`** (item 5).
4. **(Opcional)** `AddressController`: simplificar `@RequestMapping({"/api/v1/addresses"})`
   para `@RequestMapping("/api/v1/addresses")` (cosmético, sem impacto).
5. **Validar:** `mvn test -Dtest='Address*'`.

## 7. Decisões que precisam da sua aprovação

- **D1 — Tratamento do DELETE com FK violada:**
  - **Recomendado:** capturar `DataIntegrityViolationException` e lançar `BusinessException`
    (→ 400 via `GlobalExceptionHandler`). Autocontido, **sem acoplamento cross-module** (não
    mexe em Employee, não injeta `EmployeeRepository`).
  - Alternativa rejeitada: injetar `EmployeeRepository` para checar referência antes de deletar
    — cria o mesmo acoplamento cross-module que o `AGENTS.md` cita como anti-padrão de Employee,
    e exigiria alterar Employee (fora de escopo).
  - HTTP: 400 (`BusinessException`) é o padrão atual; 409 seria semanticamente melhor para
    "em uso", mas exigiria novo mapeamento no handler. Recomendo 400 por consistência.
- **D2 — Simplificar `@RequestMapping` array → string:** recomendo **não fazer** (cosmético, sem
  ganho funcional; evita ruído). Apenas se desejar.
- **D3 — paths de teste:** confirmação de que o ajuste é nos testes (e não re-adicionar o
  mapeamento antigo `/api/addresses` no controller), mantendo apenas `/api/v1`.

## Validação final esperada

- `mvn test -Dtest='Address*'` → BUILD SUCCESS, 0 falhas.
- `AddressControllerTest` com 6/6 verdes (paths v1).
- Novo teste de `delete` com FK → `BusinessException`.
- Nenhuma regra de update parcial alterada; Address permanece a referência de parcial update.
