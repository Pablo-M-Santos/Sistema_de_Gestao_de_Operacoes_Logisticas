# Regras de Negócio — Department

## 1. Identificação

| Item | Valor |
|------|-------|
| **Módulo** | Department |
| **Entidade** | Department |
| **Tabela** | `department` |
| **Endpoint base** | `/api/v1/departments` |
| **Objetivo** | Gerenciar cadastro de departamentos da empresa, com nome, descrição, sigla e status de ativação/inativação. |

---

## 2. Campos

### Campos de entrada (Create / Update)

| Campo | Tipo | Obrigatório | Tamanho/Formato | Regra | Observação |
|--------|------|-------------|-----------------|-------|------------|
| `nome` | String | Sim (criação e atualização) | Máx. 80 caracteres | Único no sistema. Validado por `DepartmentValidator`. | No update, é obrigatório conforme `@NotBlank` no DTO. |
| `descricao` | String | Sim (criação e atualização) | Máx. 250 caracteres | — | No update, é obrigatório conforme `@NotBlank` no DTO. |
| `sigla` | String | Sim (criação e atualização) | Máx. 10 caracteres | — | No update, é obrigatório conforme `@NotBlank` no DTO. |

### Campos somente de resposta (DepartmentResponse)

| Campo | Tipo | Origem | Observação |
|--------|------|--------|------------|
| `id` | Long | PK gerada pelo banco | Somente leitura. |
| `nome` | String | Entidade | Retornado na resposta. |
| `descricao` | String | Entidade | Retornado na resposta. |
| `sigla` | String | Entidade | Retornado na resposta. |
| `status` | DepartmentStatus | Entidade | Retornado na resposta. |
| `criadoEm` | LocalDateTime | Entidade | Timestamp de criação. |
| `atualizadoEm` | LocalDateTime | Entidade | Timestamp de última atualização. |

---

## 3. Regras de validação

### Validações de campos (Bean Validation — DTOs)

- **`nome`** — obrigatório na criação e atualização (`@NotBlank`), máximo 80 caracteres (`@Size(max = 80)`).
- **`descricao`** — obrigatório na criação e atualização (`@NotBlank`), máximo 250 caracteres (`@Size(max = 250)`).
- **`sigla`** — obrigatório na criação e atualização (`@NotBlank`), máximo 10 caracteres (`@Size(max = 10)`).

### Validações customizadas (DepartmentValidator)

- **`validateUniqueName(nome)`** — Garante que o nome informado não existe em outro departamento. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueNameForUpdate(nome, id)`** — Na atualização, garante que o nome informado não pertence a outro departamento diferente do atual. Dispara `DuplicateResourceException` se conflitar.

### Observação sobre atualização

O `UpdateDepartmentRequest` utiliza `@NotBlank` em todos os campos, o que significa que a atualização exige o envio de todos os campos. Não é uma atualização parcial.

---

## 4. Unicidade

| Campo | Regra | Onde é garantida | Comportamento na duplicidade |
|--------|-------|------------------|------------------------------|
| `nome` | UNIQUE | Migration (`V1__create_table_departamento.sql`) + `DepartmentValidator.validateUniqueName()` + `DepartmentValidator.validateUniqueNameForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |

---

## 5. Status

### Valores possíveis

| Valor | Significado |
|-------|-------------|
| `ACTIVE` | Departamento ativo. |
| `INACTIVE` | Departamento inativo. |

### Comportamento

- **Criação:** O valor padrão definido na migration é `ACTIVE`.
- **Atualização:** Não há campo `status` no `UpdateDepartmentRequest`. O status não pode ser alterado via PUT.
- **Ativação/Desativação:** Existem endpoints dedicados: `PATCH /api/v1/departments/{id}/activate` e `PATCH /api/v1/departments/{id}/deactivate`.
- **DELETE:** Não existe endpoint DELETE no Department.
- **Soft delete:** Não existe. O status é alterado apenas via activate/deactivate.

---

## 6. Relacionamentos

O módulo Department não possui relacionamentos com outras entidades no backend.

---

## 7. Regras específicas do módulo

- O status é alterado exclusivamente via endpoints `activate` e `deactivate`.
- Não há endpoint DELETE.
- A atualização (PUT) exige todos os campos no corpo da requisição, conforme validação Bean Validation.

---

## 8. Regras de criação

**Endpoint:** `POST /api/v1/departments`

Fluxo:

1. Recebe `CreateDepartmentRequest` no body.
2. Validação Bean Validation é aplicada automaticamente (`@Valid`).
3. `DepartmentValidator` valida unicidade de `nome`.
4. Cria entidade `Department` com os dados recebidos. O campo `status` é definido como `ACTIVE` por padrão (via `@Builder.Default` e migration).
5. Persiste no banco.
6. Retorna HTTP 201 com `DepartmentResponse`.

**Campos obrigatórios na criação:**
- `nome`
- `descricao`
- `sigla`

**Possíveis conflitos na criação:**
- Nome duplicado → HTTP 409

---

## 9. Regras de atualização

**Endpoint:** `PUT /api/v1/departments/{id}`

Fluxo:

1. Recebe `UpdateDepartmentRequest` no body.
2. Busca departamento por ID. Se não encontrado, retorna HTTP 404.
3. Atualização é **total**: todos os campos são obrigatórios no DTO (`@NotBlank`).
4. Se `nome` for diferente do atual, valida unicidade.
5. Atualiza `nome`, `descricao` e `sigla`.
6. Persiste alterações.
7. Retorna HTTP 200 com `DepartmentResponse`.

**Campos permitidos no update (todos obrigatórios):**
- `nome`
- `descricao`
- `sigla`

**Comportamento quando Department não existe:**
- Retorna HTTP 404 (`ResourceNotFoundException`).

---

## 10. Regras de exclusão

**Endpoint:** Não existe endpoint DELETE para Department.

O módulo não possui exclusão física nem soft delete. O status é alterado apenas via:
- `PATCH /api/v1/departments/{id}/activate`
- `PATCH /api/v1/departments/{id}/deactivate`

---

## 11. Endpoints

| Método | Endpoint | Finalidade | Regras relevantes |
|--------|----------|------------|-------------------|
| `POST` | `/api/v1/departments` | Criar departamento | Nome único. Campos obrigatórios: nome, descrição, sigla. Status default `ACTIVE`. |
| `GET` | `/api/v1/departments` | Listar departamentos (paginado) | Filtros: `search`, `status`. Paginação padrão: 20 por página, ordenado por `id ASC`. |
| `GET` | `/api/v1/departments/summary` | Resumo de departamentos | Retorna totais: `total`, `active`, `inactive`. |
| `GET` | `/api/v1/departments/{id}` | Buscar departamento por ID | Retorna dados completos. |
| `PUT` | `/api/v1/departments/{id}` | Atualizar departamento | Atualização total. Nome validado para unicidade. |
| `PATCH` | `/api/v1/departments/{id}/activate` | Ativar departamento | Define status como `ACTIVE`. |
| `PATCH` | `/api/v1/departments/{id}/deactivate` | Desativar departamento | Define status como `INACTIVE`. |

---

## 12. Filtros e paginação

### Filtros disponíveis em `GET /api/v1/departments`

| Parâmetro | Tipo | Descrição | Comportamento |
|-----------|------|-----------|---------------|
| `search` | String | Busca geral | Pesquisa em: `nome`, `sigla` (case-insensitive, parcial). |
| `status` | String | Filtro por status | Aceita `ACTIVE`, `INACTIVE` ou `ALL`. Se `ALL` ou vazio, não filtra. |

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

- `nome` é obrigatório na criação e atualização, e é único.
- `descricao` é obrigatório na criação e atualização.
- `sigla` é obrigatório na criação e atualização.
- Não existe endpoint DELETE.
- O status é alterado via endpoints `activate` e `deactivate`, não via PUT.
- A atualização (PUT) exige todos os campos no corpo da requisição.
- Listagem suporta filtro por status (`ACTIVE`, `INACTIVE`, `ALL`) e busca geral.
- Paginação padrão é de 20 registros por página, ordenados por ID crescente.
- Resumo (`/summary`) retorna contadores de total, ativos e inativos.

---

## 14. Fontes analisadas

- `src/main/java/br/com/logicore/modules/department/entity/Department.java`
- `src/main/java/br/com/logicore/modules/department/enums/DepartmentStatus.java`
- `src/main/java/br/com/logicore/modules/department/dto/CreateDepartmentRequest.java`
- `src/main/java/br/com/logicore/modules/department/dto/UpdateDepartmentRequest.java`
- `src/main/java/br/com/logicore/modules/department/dto/DepartmentResponse.java`
- `src/main/java/br/com/logicore/modules/department/dto/DepartmentSummaryResponse.java`
- `src/main/java/br/com/logicore/modules/department/service/DepartmentService.java`
- `src/main/java/br/com/logicore/modules/department/controller/DepartmentController.java`
- `src/main/java/br/com/logicore/modules/department/validator/DepartmentValidator.java`
- `src/main/java/br/com/logicore/modules/department/repository/DepartmentRepository.java`
- `src/main/java/br/com/logicore/modules/department/repository/spec/DepartmentSpecifications.java`
- `src/main/java/br/com/logicore/modules/department/mapper/DepartmentMapper.java`
- `src/main/resources/db/migration/V1__create_table_departamento.sql`
