# Plano: Conclusão e Consistência do Módulo `employee`

## Contexto

Módulo `employee` (`src/main/java/br/com/logicore/modules/employee/`) — entidade com
relacionamentos (ManyToOne Cargo/Department, OneToOne Address), status como `String`,
soft delete via `DELETE`. É o módulo de maior complexidade e **sem testes**. Objetivo:
torná-lo testado e funcionalmente consistente, **sem** mexer em Department/Cargo/Address/frontend.

Classificação dos desvios (conforme solicitado):
- **Bug funcional**: status `String` sem validação de valores permitidos.
- **Ausência de testes**: nenhum teste existe.
- **Inconsistência arquitetural**: acoplamento direto a repositories de outros módulos;
  falta `@Repository`; `@OneToOne` sem `unique` (risco de integridade).
- **Melhoria opcional**: `mapper.toEntity()` ausente; `findAll()` sem paginação (morto);
  `@PrePersist` redundante; README interno em `src/main`; nomes qualificados inline.
- **Decisão de domínio**: status `String` vs `enum`; `@OneToOne` vs 1:N; constraint unique.

## 1. Estado atual do Employee

- **Controller** (`EmployeeController.java`): `/api/v1/employees`; POST/GET(paginado+filtros)/
  GET `/summary`/GET `/{id}`/PUT `/{id}`/DELETE `/{id}` (soft delete); OpenAPI presente.
- **DTOs**: `CreateEmployeeRequest` (cargoId/departamentoId obrigatórios, CPF 11, email),
  `UpdateEmployeeRequest` (todos opcionais + `status` String), `EmployeeResponse` (flat,
  com campos de Cargo/Departamento/Endereco), `EmployeeSummaryResponse`.
- **Entity** (`Employee.java`): `ManyToOne` Cargo/Department (LAZY, not null), `OneToOne`
  Address (opcional); `status` `String`; `@PrePersist` define `criadoEm` + default `"ACTIVE"`
  (redundante com `@CreationTimestamp`).
- **Mapper** (`EmployeeMapper.java`): só `toResponse` (null-safe); **sem `toEntity`**.
- **Repository** (`EmployeeRepository.java`): **sem `@Repository`**; `@Query` com literais
  `'ACTIVE'`/`'INACTIVE'`; `findByCpf/Matricula`, `existsByCpf/Matricula`, counts.
- **Specifications**: `withSearch`(nome/cpf/matricula/email/telefone) + `withNome` + `withCpf`
  + `withCargoId` + `withDepartamentoId`; `null` = sem filtro.
- **Validator** (`EmployeeValidator.java`): unicidade de `cpf` e `matricula` (create/update).
- **Service** (`EmployeeService.java`): create (busca relações em repositories de outros
  módulos), findAll paginado, **findAll() sem paginação (morto)**, summary, findById, update
  parcial (null-safe), soft delete (`status="INACTIVE"`).
- **Migration** `V4__create_table_employee.sql`: `status VARCHAR(30)` (nullable), `endereco_id`
  FK **sem unique**.
- **Testes**: **inexistentes**.
- **README interno**: `modules/employee/README.md` (untracked) afirma a existência de 4
  classes de teste que não existem.

## 2. Problemas encontrados (severidade)

| # | Tipo | Problema | Severidade |
|---|------|----------|-----------|
| P1 | Bug funcional | `status` é `String` sem validação: `update` aceita qualquer valor (ex. `"FOO"`), quebrando `summary` (`countActive`/`countInactive` usam literais) | 🔴 |
| P2 | Ausência de testes | Módulo sem nenhum teste (controller/service/mapper/validator/spec) | 🔴 |
| P3 | Inconsistência | `EmployeeRepository` sem `@Repository` (convenção dos demais módulos) | 🟠 |
| P4 | Inconsistência | Acoplamento direto a `CargoRepository`/`DepartmentRepository`/`AddressRepository` no `EmployeeService` | 🟠 |
| P5 | Inconsistência | `@OneToOne` `endereco` sem `unique` em `V4` → dois funcionários podem apontar p/ mesmo endereço | 🟠 |
| P6 | Melhoria | `findAll()` sem paginação não é consumido por endpoint (código morto) | 🟡 |
| P7 | Melhoria | `mapper.toEntity()` ausente (entidade construída inline no service) | 🟡 |
| P8 | Melhoria | `@PrePersist` redundante com `@CreationTimestamp` | 🟢 |
| P9 | Melhoria | README interno em `src/main/java` com afirmações falsas (testes) | 🟢 |
| P10 | Melhoria | nomes qualificados inline (`br.com.logicore.modules.employee.dto.EmployeeSummaryResponse`) | 🟢 |

## 3. O que está correto (preservar)

- Contrato REST `/api/v1/employees` + OpenAPI.
- Update **parcial** null-safe (consistente com cargo/address).
- Soft delete via `DELETE` (`status="INACTIVE"`).
- `summary` (total/active/inactive/withAddress/withoutAddress).
- Specifications estáticas + `null` = sem filtro.
- `EmployeeMapper.toResponse` null-safe.
- Validações de unicidade de `cpf`/`matricula`.

## 4. Arquivos que precisarão ser alterados (escopo core)

- `modules/employee/repository/EmployeeRepository.java` — adicionar `@Repository` (P3).
- `modules/employee/validator/EmployeeValidator.java` — adicionar `validateStatus` (P1).
- `modules/employee/service/EmployeeService.java` — chamar `validateStatus` no update (P1).
- `modules/employee/controller/EmployeeControllerTest.java` — **criar**.
- `modules/employee/service/EmployeeServiceTest.java` — **criar**.
- `modules/employee/mapper/EmployeeMapperTest.java` — **criar**.
- `modules/employee/validator/EmployeeValidatorTest.java` — **criar**.
- `modules/employee/repository/spec/EmployeeSpecificationsIntegrationTest.java` — **criar**.

Não alterar: Department, Cargo, Address, migration `V4`, DTOs (exceto se aprovado em D),
frontend, `pom.xml`, `EmployeeController`, `EmployeeSpecifications`, `EmployeeMapper` (além
do teste), `EmployeeRepository` (além de `@Repository`).

## 5. Testes a criar

- **EmployeeControllerTest** (`@WebMvcTest` + `@MockitoBean` service): create 201; findAll
  paginado com filtros 200; summary 200; findById 200/404; update 200; delete→soft 204.
- **EmployeeServiceTest**: create (valida unicidade + busca relações); create com cargo
  inexistente → `ResourceNotFoundException`; findById / not found; update parcial
  (cpf/matricula/relações/status); **update com `status` inválido → `BusinessException`** (P1);
  soft delete (status vira INACTIVE); summary (contas); specifications aplicadas.
- **EmployeeMapperTest**: `toResponse` com relações presentes; `toResponse` com relações `null`
  (null-safe).
- **EmployeeValidatorTest**: `validateUniqueCpf/Matricula` (create) ok/throw;
  `validateUniqueCpfForUpdate/MatriculaForUpdate` ok/throw; **`validateStatus` ok/throw**.
- **EmployeeSpecificationsIntegrationTest** (`@DataJpaTest` + H2): `withSearch`,
  `withNome`, `withCpf`, `withCargoId`, `withDepartamentoId`.

## 6. Plano de implementação (ordem)

1. `EmployeeRepository`: adicionar `@Repository` (P3).
2. `EmployeeValidator`: adicionar `validateStatus(String)` → `BusinessException` se não for
   `"ACTIVE"`/`"INACTIVE"`.
3. `EmployeeService.update`: envolver `employee.setStatus(...)` em
   `if (request.getStatus() != null) { validator.validateStatus(request.getStatus()); employee.setStatus(request.getStatus()); }` (P1).
4. Criar os 5 arquivos de teste (seção 5).
5. Validar: `mvn test -Dtest='Employee*'`.

## 7. Decisões arquiteturais que precisam da sua aprovação

- **D1 — status `String` vs `enum`:** recomendo **manter `String`** e apenas validar valores
  permitidos (P1). Introduzir `EmployeeStatus` (enum) exigiria mudar entity, DTO, mapper,
  `@Query` e migration — escopo maior. Aprovação necessária se preferir o enum.
- **D2 — `@OneToOne` × 1:N e `unique`:** recomendo **não alterar agora** (evita mudança de
  migration e de Address). Adicionar `unique` em `endereco_id` ou mudar para `@ManyToOne`
  ficam fora do escopo desta tarefa.
- **D3 — acoplamento cross-module:** recomendo **manter repositories** neste passo
  (funciona; refatorar para usar os *services* de Cargo/Department/Address é maior e pode ser
  débito tratado à parte). Confirmar se deve ficar como está.
- **D4 — melhorias opcionais (P6/P7/P8/P9/P10):** recomendo **não fazer nesta tarefa**
  (remover `findAll()` morto, criar `mapper.toEntity()`, remover `@PrePersist`, mover README
  interno, limpar nomes qualificados). Podem ser um passo posterior.

## 8. O que NÃO recomendo alterar neste momento

- Migration `V4` (incl. `unique` em `endereco_id`).
- DTOs `Create/Update/Response` (exceto se D1 aprovar enum).
- `EmployeeController`, `EmployeeSpecifications`, `Address`/`Cargo`/`Department`.
- Refatoração de acoplamento cross-module (D3).
- Limpeza cosmética (P6–P10).

## Validação

- `mvn test -Dtest='Employee*'` → BUILD SUCCESS, 0 falhas.
- Cobertura de controller/service/mapper/validator/spec do módulo Employee.
- `EmployeeServiceTest` cobre o bug de status inválido (P1).
