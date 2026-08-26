# Regras de Negócio — Employee

## 1. Identificação

| Item | Valor |
|------|-------|
| **Módulo** | Employee |
| **Entidade** | Employee |
| **Tabela** | `employee` |
| **Endpoint base** | `/api/v1/employees` |
| **Objetivo** | Gerenciar cadastro de funcionários, incluindo dados pessoais, vínculo com Cargo e Department, endereço opcional e status de ativação/inativação. |

---

## 2. Campos

### Campos de entrada (Create / Update)

| Campo | Tipo | Obrigatório | Tamanho/Formato | Regra | Observação |
|--------|------|-------------|-----------------|-------|------------|
| `matricula` | String | Sim (criação) | Máx. 20 caracteres | Único no sistema. Validado por `EmployeeValidator`. | No update, é opcional (parcial). |
| `nome` | String | Sim (criação) | Máx. 150 caracteres | — | No update, é opcional (parcial). |
| `cpf` | String | Sim (criação) | Exatamente 11 caracteres | Único no sistema. Validado por `EmployeeValidator`. | No update, é opcional (parcial). |
| `rg` | String | Não | Máx. 20 caracteres | — | Opcional em criação e atualização. |
| `dataNascimento` | LocalDate | Não | Formato data | — | Opcional em criação e atualização. |
| `telefone` | String | Não | Máx. 20 caracteres | — | Opcional em criação e atualização. |
| `email` | String | Não | Máx. 150 caracteres | Formato de e-mail válido (conforme Bean Validation `@Email`). | Opcional em criação e atualização. |
| `cargoId` | Long | Sim (criação) | — | Deve referenciar um `Cargo` existente. | No update, é opcional (parcial). |
| `departamentoId` | Long | Sim (criação) | — | Deve referenciar um `Department` existente. | No update, é opcional (parcial). |
| `enderecoId` | Long | Não | — | Deve referenciar um `Address` existente, se informado. | Opcional em criação e atualização. |
| `dataAdmissao` | LocalDate | Sim (criação) | Formato data | — | No update, é opcional (parcial). |
| `status` | String | Não | — | Valores permitidos: `ACTIVE` ou `INACTIVE`. Validado por `EmployeeValidator.validateStatus()`. | Apenas no update. |

### Campos somente de resposta (EmployeeResponse)

| Campo | Tipo | Origem | Observação |
|--------|------|--------|------------|
| `id` | Long | PK gerada pelo banco | Somente leitura. |
| `matricula` | String | Entidade | Retornado na resposta. |
| `nome` | String | Entidade | Retornado na resposta. |
| `cpf` | String | Entidade | Retornado na resposta. |
| `rg` | String | Entidade | Retornado na resposta. |
| `dataNascimento` | LocalDate | Entidade | Retornado na resposta. |
| `telefone` | String | Entidade | Retornado na resposta. |
| `email` | String | Entidade | Retornado na resposta. |
| `cargoId` | Long | FK → Cargo | Retornado na resposta. |
| `cargoNome` | String | Cargo.nome | Retornado na resposta. |
| `cargoCodigo` | String | Cargo.codigo | Retornado na resposta. |
| `departamentoId` | Long | FK → Department | Retornado na resposta. |
| `departamentoNome` | String | Department.nome | Retornado na resposta. |
| `departamentoSigla` | String | Department.sigla | Retornado na resposta. |
| `enderecoId` | Long | FK → Address | Retornado na resposta. |
| `enderecoCep` | String | Address.cep | Retornado na resposta. |
| `enderecoLogradouro` | String | Address.logradouro | Retornado na resposta. |
| `enderecoNumero` | String | Address.numero | Retornado na resposta. |
| `enderecoComplemento` | String | Address.complemento | Retornado na resposta. |
| `enderecoBairro` | String | Address.bairro | Retornado na resposta. |
| `enderecoCidade` | String | Address.cidade | Retornado na resposta. |
| `enderecoEstado` | String | Address.estado | Retornado na resposta. |
| `enderecoPais` | String | Address.pais | Retornado na resposta. |
| `enderecoLatitude` | BigDecimal | Address.latitude | Retornado na resposta. |
| `enderecoLongitude` | BigDecimal | Address.longitude | Retornado na resposta. |
| `dataAdmissao` | LocalDate | Entidade | Retornado na resposta. |
| `status` | String | Entidade | Retornado na resposta. |
| `criadoEm` | LocalDateTime | Entidade | Timestamp de criação. |
| `atualizadoEm` | LocalDateTime | Entidade | Timestamp de última atualização. |

---

## 3. Regras de validação

### Validações de campos (Bean Validation — DTOs)

- **`matricula`** — obrigatório na criação (`@NotBlank`), máximo 20 caracteres (`@Size(max = 20)`). No update, é opcional (`@Size` apenas).
- **`nome`** — obrigatório na criação (`@NotBlank`), máximo 150 caracteres (`@Size(max = 150)`). No update, é opcional.
- **`cpf`** — obrigatório na criação (`@NotBlank`), tamanho exatamente 11 caracteres (`@Size(min = 11, max = 11)`). No update, é opcional.
- **`rg`** — opcional, máximo 20 caracteres.
- **`dataNascimento`** — opcional, formato data.
- **`telefone`** — opcional, máximo 20 caracteres.
- **`email`** — opcional, máximo 150 caracteres, deve ser um e-mail válido (`@Email`).
- **`cargoId`** — obrigatório na criação (`@NotNull`). No update, é opcional.
- **`departamentoId`** — obrigatório na criação (`@NotNull`). No update, é opcional.
- **`enderecoId`** — opcional. Se informado, deve referenciar um endereço existente (validado no Service).
- **`dataAdmissao`** — obrigatório na criação (`@NotNull`). No update, é opcional.

### Validações customizadas (EmployeeValidator)

- **`validateUniqueCpf(cpf)`** — Garante que o CPF informado não existe em outro funcionário. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueMatricula(matricula)`** — Garante que a matrícula informada não existe em outro funcionário. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueCpfForUpdate(cpf, id)`** — Na atualização, garante que o CPF informado não pertence a outro funcionário diferente do atual. Dispara `DuplicateResourceException` se conflitar.
- **`validateUniqueMatriculaForUpdate(matricula, id)`** — Na atualização, garante que a matrícula informada não pertence a outro funcionário diferente do atual. Dispara `DuplicateResourceException` se conflitar.
- **`validateStatus(status)`** — Garante que o status é exatamente `ACTIVE` ou `INACTIVE`. Dispara `BusinessException` para qualquer outro valor.

### Validações de existência no Service

- **`cargoId`** — Na criação e atualização, o `Cargo` referenciado deve existir. Caso contrário, retorna HTTP 404 (`ResourceNotFoundException`).
- **`departamentoId`** — Na criação e atualização, o `Department` referenciado deve existir. Caso contrário, retorna HTTP 404.
- **`enderecoId`** — Se informado na criação ou atualização, o `Address` referenciado deve existir. Caso contrário, retorna HTTP 404.

---

## 4. Unicidade

| Campo | Regra | Onde é garantida | Comportamento na duplicidade |
|--------|-------|------------------|------------------------------|
| `matricula` | UNIQUE | Migration (`V4__create_table_employee.sql`) + `EmployeeValidator.validateUniqueMatricula()` + `EmployeeValidator.validateUniqueMatriculaForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |
| `cpf` | UNIQUE | Migration (`V4__create_table_employee.sql`) + `EmployeeValidator.validateUniqueCpf()` + `EmployeeValidator.validateUniqueCpfForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |

---

## 5. Status

### Valores possíveis

| Valor | Significado |
|-------|-------------|
| `ACTIVE` | Funcionário ativo. |
| `INACTIVE` | Funcionário inativo (soft delete). |

### Comportamento

- **Criação:** Se `status` não for informado, o valor padrão definido em `@PrePersist` é `ACTIVE`.
- **Atualização:** O campo `status` é opcional. Se informado, deve ser `ACTIVE` ou `INACTIVE`. Qualquer outro valor dispara `BusinessException` (HTTP 400).
- **DELETE:** Não é exclusão física. O DELETE marca o funcionário como `INACTIVE` (soft delete).
- **Reativação:** Não existe endpoint de reativação (`activate`/`deactivate`) no Employee.
- **Endpoint específico para status:** Não existe endpoint dedicado para alterar status. A alteração de status ocorre apenas via `PUT /api/v1/employees/{id}`.

---

## 6. Relacionamentos

### Cargo

| Aspecto | Comportamento |
|---------|---------------|
| **Obrigatoriedade** | Obrigatório. `cargo_id` é `NOT NULL` na migration. |
| **Tipo** | `@ManyToOne(fetch = FetchType.LAZY)` |
| **FK** | `employee.cargo_id` → `cargo.id` |
| **Criação** | `cargoId` é obrigatório no `CreateEmployeeRequest`. O `Cargo` deve existir. |
| **Atualização** | `cargoId` é opcional no `UpdateEmployeeRequest`. Se informado, o novo `Cargo` deve existir. |

### Department

| Aspecto | Comportamento |
|---------|---------------|
| **Obrigatoriedade** | Obrigatório. `departamento_id` é `NOT NULL` na migration. |
| **Tipo** | `@ManyToOne(fetch = FetchType.LAZY)` |
| **FK** | `employee.departamento_id` → `department.id` |
| **Criação** | `departamentoId` é obrigatório no `CreateEmployeeRequest`. O `Department` deve existir. |
| **Atualização** | `departamentoId` é opcional no `UpdateEmployeeRequest`. Se informado, o novo `Department` deve existir. |

### Address

| Aspecto | Comportamento |
|---------|---------------|
| **Obrigatoriedade** | Opcional. `endereco_id` permite `NULL` na migration. |
| **Tipo** | `@OneToOne(fetch = FetchType.LAZY)` |
| **FK** | `employee.endereco_id` → `address.id` |
| **Criação** | `enderecoId` é opcional no `CreateEmployeeRequest`. Se informado, o `Address` deve existir. |
| **Atualização** | `enderecoId` é opcional no `UpdateEmployeeRequest`. Se informado, o novo `Address` deve existir. |

**Comportamento quando não há endereço:**
- O funcionário pode ser cadastrado sem `enderecoId`.
- Na resposta, todos os campos de endereço retornam `null`.

---

## 7. Regras de endereço

- O Employee **não cria** Address. Ele apenas recebe um `enderecoId` referenciando um endereço já existente.
- O endereço é **opcional**.
- Quando `enderecoId` é informado na criação ou atualização, o backend valida a existência do `Address` correspondente.
- Se `enderecoId` não for informado, `endereco` permanece `null`.
- Na atualização, se `enderecoId` for informado, o vínculo é substituído pelo novo endereço.
- Não há regra de criação ou edição de endereço pelo módulo Employee.

> **Observação:** Regras de validação de CEP ou preenchimento automático de endereço (ex.: ViaCEP) não existem no backend do Employee. São decisões de UX do frontend, não regras de negócio deste módulo.

---

## 8. Regras de criação

**Endpoint:** `POST /api/v1/employees`

Fluxo:

1. Recebe `CreateEmployeeRequest` no body.
2. Validação Bean Validation é aplicada automaticamente (`@Valid`).
3. `EmployeeValidator` valida unicidade de `cpf` e `matricula`.
4. Busca `Cargo` por `cargoId`. Se não encontrado, retorna HTTP 404.
5. Busca `Department` por `departamentoId`. Se não encontrado, retorna HTTP 404.
6. Se `enderecoId` for informado, busca `Address`. Se não encontrado, retorna HTTP 404.
7. Cria entidade `Employee` com os dados recebidos. O campo `status` é definido como `ACTIVE` por padrão via `@PrePersist`.
8. Persiste no banco.
9. Retorna HTTP 201 com `EmployeeResponse`.

**Campos obrigatórios na criação:**
- `matricula`
- `nome`
- `cpf`
- `cargoId`
- `departamentoId`
- `dataAdmissao`

**Possíveis conflitos na criação:**
- CPF duplicado → HTTP 409
- Matrícula duplicada → HTTP 409

---

## 9. Regras de atualização

**Endpoint:** `PUT /api/v1/employees/{id}`

Fluxo:

1. Recebe `UpdateEmployeeRequest` no body.
2. Busca funcionário por ID. Se não encontrado, retorna HTTP 404.
3. Atualização é **parcial**: apenas campos não nulos são atualizados.
4. Se `cpf` for informado:
   - Valida unicidade contra outros funcionários.
   - Atualiza o CPF.
5. Se `matricula` for informado:
   - Valida unicidade contra outros funcionários.
   - Atualiza a matrícula.
6. Se `status` for informado:
   - Valida se é `ACTIVE` ou `INACTIVE`.
   - Atualiza o status.
7. Demais campos (`nome`, `rg`, `dataNascimento`, `telefone`, `email`, `dataAdmissao`) são atualizados diretamente se não nulos.
8. Se `cargoId` for informado, busca o `Cargo` e atualiza o vínculo. Se não encontrado, retorna HTTP 404.
9. Se `departamentoId` for informado, busca o `Department` e atualiza o vínculo. Se não encontrado, retorna HTTP 404.
10. Se `enderecoId` for informado, busca o `Address` e atualiza o vínculo. Se não encontrado, retorna HTTP 404.
11. Persiste alterações.
12. Retorna HTTP 200 com `EmployeeResponse`.

**Campos permitidos no update (todos opcionais):**
- `matricula`
- `nome`
- `cpf`
- `rg`
- `dataNascimento`
- `telefone`
- `email`
- `cargoId`
- `departamentoId`
- `enderecoId`
- `dataAdmissao`
- `status`

**Comportamento quando Employee não existe:**
- Retorna HTTP 404 (`ResourceNotFoundException`).

---

## 10. Regras de exclusão

**Endpoint:** `DELETE /api/v1/employees/{id}`

| Aspecto | Comportamento |
|---------|---------------|
| **Tipo** | Soft delete (não é exclusão física). |
| **Ação** | Define o campo `status` como `INACTIVE`. |
| **Resposta HTTP** | `204 No Content` |
| **Registro não existe** | Retorna HTTP 404 (`ResourceNotFoundException`). |

- O registro é mantido no banco de dados.
- Não há endpoint de reativação.
- Não há exclusão física.

---

## 11. Endpoints

| Método | Endpoint | Finalidade | Regras relevantes |
|--------|----------|------------|-------------------|
| `POST` | `/api/v1/employees` | Criar funcionário | CPF e matrícula únicos. Cargo, Department e Address (opcional) devem existir. Status default `ACTIVE`. |
| `GET` | `/api/v1/employees` | Listar funcionários (paginado) | Filtros: `search`, `nome`, `cpf`, `cargoId`, `departamentoId`. Paginação padrão: 20 por página, ordenado por `id ASC`. |
| `GET` | `/api/v1/employees/summary` | Resumo de funcionários | Retorna totais: `total`, `active`, `inactive`, `withAddress`, `withoutAddress`. |
| `GET` | `/api/v1/employees/{id}` | Buscar funcionário por ID | Retorna dados completos com relacionamentos (Cargo, Department, Address). |
| `PUT` | `/api/v1/employees/{id}` | Atualizar funcionário | Atualização parcial. CPF e matrícula validados para unicidade. Status validado (`ACTIVE`/`INACTIVE`). |
| `DELETE` | `/api/v1/employees/{id}` | Remover funcionário | Soft delete: define `status = INACTIVE`. |

---

## 12. Filtros e paginação

### Filtros disponíveis em `GET /api/v1/employees`

| Parâmetro | Tipo | Descrição | Comportamento |
|-----------|------|-----------|---------------|
| `search` | String | Busca geral | Pesquisa em: `nome`, `cpf`, `matricula`, `email`, `telefone` (case-insensitive, parcial). |
| `nome` | String | Filtro por nome | Pesquisa parcial case-insensitive. |
| `cpf` | String | Filtro por CPF | Busca exata. |
| `cargoId` | Long | Filtro por cargo | Busca exata pelo ID do cargo. |
| `departamentoId` | Long | Filtro por department | Busca exata pelo ID do department. |

### Paginação

| Aspecto | Valor |
|---------|-------|
| **Tamanho padrão** | 20 registros por página |
| **Ordenação padrão** | `id ASC` |
| **Parâmetros** | `page`, `size`, `sort` (padrão Spring Data) |

### Comportamento dos filtros

- Filtros são combinados com `AND`.
- Filtros vazios ou nulos são ignorados (retornam `null` na Specification, que o Spring Data ignora).
- O filtro `search` usa `OR` entre os campos pesquisados.

---

## 13. Resumo para o Frontend

### Regras que o Frontend deve respeitar

- `matricula` é obrigatório na criação e único.
- `nome` é obrigatório na criação.
- `cpf` é obrigatório na criação, deve ter exatamente 11 caracteres e é único.
- `cargoId` é obrigatório na criação. O frontend deve carregar a lista de cargos para seleção.
- `departamentoId` é obrigatório na criação. O frontend deve carregar a lista de departments para seleção.
- `dataAdmissao` é obrigatória na criação.
- `enderecoId` é opcional. Se informado, deve referenciar um endereço existente.
- `rg`, `telefone`, `email`, `dataNascimento` são opcionais. Se `email` for informado, deve ser um e-mail válido.
- Atualização é parcial: enviar apenas campos que devem ser alterados. Campos não enviados são preservados.
- Na atualização, se `cpf` ou `matricula` forem alterados, a unicidade é validada contra outros funcionários.
- `status` pode ser alterado no update, mas apenas para `ACTIVE` ou `INACTIVE`.
- DELETE não remove o registro: marca o funcionário como `INACTIVE`.
- Não existe endpoint de reativação.
- Listagem suporta filtros por nome, CPF, cargo e department, além de busca geral.
- Paginação padrão é de 20 registros por página, ordenados por ID crescente.
- Resumo (`/summary`) retorna contadores de total, ativos, inativos, com endereço e sem endereço.

---

## 14. Fontes analisadas

- `src/main/java/br/com/logicore/modules/employee/entity/Employee.java`
- `src/main/java/br/com/logicore/modules/employee/dto/CreateEmployeeRequest.java`
- `src/main/java/br/com/logicore/modules/employee/dto/UpdateEmployeeRequest.java`
- `src/main/java/br/com/logicore/modules/employee/dto/EmployeeResponse.java`
- `src/main/java/br/com/logicore/modules/employee/dto/EmployeeSummaryResponse.java`
- `src/main/java/br/com/logicore/modules/employee/service/EmployeeService.java`
- `src/main/java/br/com/logicore/modules/employee/controller/EmployeeController.java`
- `src/main/java/br/com/logicore/modules/employee/validator/EmployeeValidator.java`
- `src/main/java/br/com/logicore/modules/employee/repository/EmployeeRepository.java`
- `src/main/java/br/com/logicore/modules/employee/repository/spec/EmployeeSpecifications.java`
- `src/main/java/br/com/logicore/modules/employee/mapper/EmployeeMapper.java`
- `src/main/resources/db/migration/V4__create_table_employee.sql`
- `src/test/java/br/com/logicore/modules/employee/service/EmployeeServiceTest.java`
- `src/test/java/br/com/logicore/modules/employee/validator/EmployeeValidatorTest.java`
- `src/test/java/br/com/logicore/modules/employee/controller/EmployeeControllerTest.java`
