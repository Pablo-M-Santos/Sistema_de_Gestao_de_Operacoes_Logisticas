# Regras de Negócio — Driver

## 1. Identificação

| Item | Valor |
|------|-------|
| **Módulo** | Driver |
| **Entidade** | Driver |
| **Tabela** | `driver` |
| **Endpoint base** | `/api/v1/motoristas` |
| **Objetivo** | Gerenciar cadastro de motoristas, vinculando um funcionário existente a dados de CNH, categoria e validade. |

---

## 2. Campos

### Campos de entrada (Create / Update)

| Campo | Tipo | Obrigatório | Tamanho/Formato | Regra | Observação |
|--------|------|-------------|-----------------|-------|------------|
| `funcionarioId` | Long | Sim (criação) | — | Deve referenciar um `Employee` existente. Único por motorista. | No update, é opcional (mas o service não permite alterar funcionário). |
| `cnh` | String | Sim (criação) | Máx. 20 caracteres | Única no sistema. Validado por `DriverValidator`. | No update, é opcional (parcial). |
| `categoria` | String | Sim (criação) | 1 a 5 caracteres | Valores permitidos: A, B, C, D, E, AB, AC, AD, AE. Validado por `DriverValidator.validateCategoria()`. | No update, é opcional (parcial). |
| `validadeCnh` | LocalDate | Sim (criação) | Formato data | — | No update, é opcional (parcial). |
| `observacoes` | String | Não | Máx. 500 caracteres | — | Opcional em criação e atualização. |

### Campos somente de resposta (DriverResponse)

| Campo | Tipo | Origem | Observação |
|--------|------|--------|------------|
| `id` | Long | PK gerada pelo banco | Somente leitura. |
| `funcionarioId` | Long | FK → Employee | Retornado na resposta. |
| `funcionarioNome` | String | Employee.nome | Retornado na resposta. |
| `funcionarioMatricula` | String | Employee.matricula | Retornado na resposta. |
| `cnh` | String | Entidade | Retornado na resposta. |
| `categoria` | String | Entidade | Retornado na resposta. |
| `validadeCnh` | LocalDate | Entidade | Retornado na resposta. |
| `observacoes` | String | Entidade | Retornado na resposta. |
| `criadoEm` | LocalDateTime | Entidade | Timestamp de criação. |
| `atualizadoEm` | LocalDateTime | Entidade | Timestamp de última atualização. |

---

## 3. Regras de validação

### Validações de campos (Bean Validation — DTOs)

- **`funcionarioId`** — obrigatório na criação (`@NotNull`). No update, não existe no DTO.
- **`cnh`** — obrigatório na criação (`@NotBlank`), máximo 20 caracteres (`@Size(max = 20)`). No update, é opcional.
- **`categoria`** — obrigatório na criação (`@NotBlank`), tamanho entre 1 e 5 caracteres (`@Size(min = 1, max = 5)`). No update, é opcional.
- **`validadeCnh`** — obrigatório na criação (`@NotNull`). No update, é opcional.
- **`observacoes`** — opcional, máximo 500 caracteres.

### Validações customizadas (DriverValidator)

- **`validateUniqueCnh(cnh)`** — Garante que a CNH informada não existe em outro motorista. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueCnhForUpdate(cnh, id)`** — Na atualização, garante que a CNH informada não pertence a outro motorista diferente do atual. Dispara `DuplicateResourceException` se conflitar.
- **`validateUniqueFuncionarioId(funcionarioId)`** — Garante que o funcionário informado não está cadastrado como motorista em outro registro. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueFuncionarioIdForUpdate(funcionarioId, id)`** — Na atualização, garante que o funcionário informado não pertence a outro motorista diferente do atual. Dispara `DuplicateResourceException` se conflitar.
- **`validateCategoria(categoria)`** — Garante que a categoria é uma das permitidas: A, B, C, D, E, AB, AC, AD, AE. Dispara `BusinessException` para valores inválidos. A categoria é armazenada em maiúsculo.

### Validações de existência no Service

- **`funcionarioId`** — Na criação, o `Employee` referenciado deve existir. Caso contrário, retorna HTTP 404 (`ResourceNotFoundException`).

---

## 4. Unicidade

| Campo | Regra | Onde é garantida | Comportamento na duplicidade |
|--------|-------|------------------|------------------------------|
| `cnh` | UNIQUE | Migration (`V7__create_table_driver.sql`) + `DriverValidator.validateUniqueCnh()` + `DriverValidator.validateUniqueCnhForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |
| `funcionario_id` | UNIQUE | Migration (`V7__create_table_driver.sql`) + `DriverValidator.validateUniqueFuncionarioId()` + `DriverValidator.validateUniqueFuncionarioIdForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |

---

## 5. Status

O módulo Driver não possui campo de status.

---

## 6. Relacionamentos

### Employee

| Aspecto | Comportamento |
|---------|---------------|
| **Obrigatoriedade** | Obrigatório. `funcionario_id` é `NOT NULL` e `UNIQUE` na migration. |
| **Tipo** | `@OneToOne(fetch = FetchType.LAZY)` |
| **FK** | `driver.funcionario_id` → `employee.id` |
| **Criação** | `funcionarioId` é obrigatório no `CreateDriverRequest`. O `Employee` deve existir. |
| **Atualização** | O `UpdateDriverRequest` não possui campo `funcionarioId`. O vínculo com o funcionário não pode ser alterado após a criação. |

---

## 7. Regras específicas do módulo

- A categoria da CNH é armazenada em maiúsculo (`trim().toUpperCase()`).
- A `validadeCnh` é uma data simples, sem validação de data mínima ou máxima no código atual.
- O motorista está vinculado a um único funcionário, e cada funcionário pode ter no máximo um registro de motorista (`funcionario_id UNIQUE`).
- Não há endpoint `activate`/`deactivate`.
- O summary retorna apenas o `total` de registros.

---

## 8. Regras de criação

**Endpoint:** `POST /api/v1/motoristas`

Fluxo:

1. Recebe `CreateDriverRequest` no body.
2. Validação Bean Validation é aplicada automaticamente (`@Valid`).
3. `DriverValidator` valida unicidade de `cnh` e `funcionarioId`.
4. `DriverValidator` valida a categoria da CNH.
5. Busca `Employee` por `funcionarioId`. Se não encontrado, retorna HTTP 404.
6. Cria entidade `Driver` com os dados recebidos. A categoria é convertida para maiúsculo.
7. Persiste no banco.
8. Retorna HTTP 201 com `DriverResponse`.

**Campos obrigatórios na criação:**
- `funcionarioId`
- `cnh`
- `categoria`
- `validadeCnh`

**Possíveis conflitos na criação:**
- CNH duplicada → HTTP 409
- Funcionário já cadastrado como motorista → HTTP 409

---

## 9. Regras de atualização

**Endpoint:** `PUT /api/v1/motoristas/{id}`

Fluxo:

1. Recebe `UpdateDriverRequest` no body.
2. Busca motorista por ID. Se não encontrado, retorna HTTP 404.
3. Atualização é **parcial**: apenas campos não nulos são atualizados.
4. Se `cnh` for informado e diferente do atual, valida unicidade.
5. Se `categoria` for informada, valida a categoria e atualiza (armazenando em maiúsculo).
6. Se `validadeCnh` for informada, atualiza.
7. Se `observacoes` for informada, atualiza.
8. Persiste alterações.
9. Retorna HTTP 200 com `DriverResponse`.

**Campos permitidos no update (todos opcionais):**
- `cnh`
- `categoria`
- `validadeCnh`
- `observacoes`

> **Observação:** O `funcionarioId` não pode ser alterado via update.

**Comportamento quando Driver não existe:**
- Retorna HTTP 404 (`ResourceNotFoundException`).

---

## 10. Regras de exclusão

**Endpoint:** `DELETE /api/v1/motoristas/{id}`

| Aspecto | Comportamento |
|---------|---------------|
| **Tipo** | Exclusão física. |
| **Ação** | Remove o registro do banco de dados. |
| **Resposta HTTP** | `204 No Content` |
| **Registro não existe** | Retorna HTTP 404 (`ResourceNotFoundException`). |

- O funcionário (`Employee`) não é afetado.
- O registro de motorista é removido permanentemente.

---

## 11. Endpoints

| Método | Endpoint | Finalidade | Regras relevantes |
|--------|----------|------------|-------------------|
| `POST` | `/api/v1/motoristas` | Criar motorista | Funcionário e CNH únicos. Campos obrigatórios: funcionário, CNH, categoria, validade CNH. Categoria validada (A-E, AB-AE). |
| `GET` | `/api/v1/motoristas` | Listar motoristas (paginado) | Filtros: `search`, `categoria`, `funcionarioId`. Paginação padrão: 20 por página, ordenado por `id ASC`. |
| `GET` | `/api/v1/motoristas/summary` | Resumo de motoristas | Retorna apenas `total`. |
| `GET` | `/api/v1/motoristas/{id}` | Buscar motorista por ID | Retorna dados completos com nome e matrícula do funcionário. |
| `GET` | `/api/v1/motoristas/employee/{funcionarioId}` | Buscar motorista por funcionário | Retorna o motorista associado ao funcionário informado. |
| `PUT` | `/api/v1/motoristas/{id}` | Atualizar motorista | Atualização parcial. CNH validada para unicidade. Categoria validada (A-E, AB-AE). |
| `DELETE` | `/api/v1/motoristas/{id}` | Remover motorista | Exclusão física. |

---

## 12. Filtros e paginação

### Filtros disponíveis em `GET /api/v1/motoristas`

| Parâmetro | Tipo | Descrição | Comportamento |
|-----------|------|-----------|---------------|
| `search` | String | Busca geral | Pesquisa em: `cnh`, `categoria`, `observacoes`, `funcionario.nome` (case-insensitive, parcial). |
| `categoria` | String | Filtro por categoria | Busca exata case-insensitive. |
| `funcionarioId` | Long | Filtro por funcionário | Busca exata pelo ID do funcionário. |

### Paginação

| Aspecto | Valor |
|---------|-------|
| **Tamanho padrão** | 20 registros por página |
| **Ordenação padrão** | `id ASC` |
| **Parâmetros** | `page`, `size`, `sort` (padrão Spring Data) |

### Comportamento dos filtros

- Filtros são combinados com `AND`.
- Filtros vazios ou nulos são ignorados.
- O filtro `search` usa `OR` entre os campos pesquisados.

---

## 13. Regras que o Frontend deve respeitar

- `funcionarioId` é obrigatório na criação. O frontend deve selecionar um funcionário existente.
- `cnh` é obrigatório na criação e único.
- `categoria` é obrigatória na criação e deve ser uma das permitidas: A, B, C, D, E, AB, AC, AD, AE.
- `validadeCnh` é obrigatória na criação.
- `observacoes` é opcional.
- Atualização é parcial: enviar apenas campos que devem ser alterados.
- Na atualização, se `cnh` for alterado, a unicidade é validada contra outros motoristas.
- O `funcionarioId` não pode ser alterado após a criação.
- DELETE é físico: remove o registro de motorista permanentemente.
- Listagem suporta filtros por categoria e funcionário, além de busca geral.
- Paginação padrão é de 20 registros por página, ordenados por ID crescente.
- Resumo (`/summary`) retorna apenas o total de motoristas.

---

## 14. Fontes analisadas

- `src/main/java/br/com/logicore/modules/driver/entity/Driver.java`
- `src/main/java/br/com/logicore/modules/driver/enums/DriverCnhCategory.java`
- `src/main/java/br/com/logicore/modules/driver/dto/CreateDriverRequest.java`
- `src/main/java/br/com/logicore/modules/driver/dto/UpdateDriverRequest.java`
- `src/main/java/br/com/logicore/modules/driver/dto/DriverResponse.java`
- `src/main/java/br/com/logicore/modules/driver/dto/DriverSummaryResponse.java`
- `src/main/java/br/com/logicore/modules/driver/service/DriverService.java`
- `src/main/java/br/com/logicore/modules/driver/controller/DriverController.java`
- `src/main/java/br/com/logicore/modules/driver/validator/DriverValidator.java`
- `src/main/java/br/com/logicore/modules/driver/repository/DriverRepository.java`
- `src/main/java/br/com/logicore/modules/driver/repository/spec/DriverSpecifications.java`
- `src/main/java/br/com/logicore/modules/driver/mapper/DriverMapper.java`
- `src/main/resources/db/migration/V7__create_table_driver.sql`
