# Regras de Negócio — Usuario

## 1. Identificação

| Item | Valor |
|------|-------|
| **Módulo** | Usuario |
| **Entidade** | Usuario |
| **Tabela** | `usuario` |
| **Endpoint base** | `/api/v1/usuarios` |
| **Objetivo** | Gerenciar cadastro de usuários de sistema, vinculando um funcionário existente a dados de login (e-mail, senha) e status. |

---

## 2. Campos

### Campos de entrada (Create / Update)

| Campo | Tipo | Obrigatório | Tamanho/Formato | Regra | Observação |
|--------|------|-------------|-----------------|-------|------------|
| `nome` | String | Sim (criação) | Máx. 150 caracteres | — | No update, é opcional (parcial). |
| `email` | String | Sim (criação) | Máx. 150 caracteres | Único no sistema. Formato de e-mail válido (`@Email`). Validado por `UsuarioValidator`. | No update, é opcional (parcial). |
| `senha` | String | Sim (criação) | 6 a 255 caracteres | — | No update, é opcional (parcial). |
| `funcionarioId` | Long | Sim (criação) | — | Deve referenciar um `Employee` existente. Único por usuário. | No update, é opcional (parcial). |
| `status` | String | Não | — | Valores permitidos: `ACTIVE` ou `INACTIVE`. Validado por `UsuarioValidator.validateStatus()`. | Apenas no update. |

### Campos somente de resposta (UsuarioResponse)

| Campo | Tipo | Origem | Observação |
|--------|------|--------|------------|
| `id` | Long | PK gerada pelo banco | Somente leitura. |
| `nome` | String | Entidade | Retornado na resposta. |
| `email` | String | Entidade | Retornado na resposta. |
| `status` | String | Entidade | Retornado na resposta. |
| `ultimoAcesso` | LocalDateTime | Entidade | Retornado na resposta. |
| `funcionarioId` | Long | FK → Employee | Retornado na resposta. |
| `criadoEm` | LocalDateTime | Entidade | Timestamp de criação. |
| `atualizadoEm` | LocalDateTime | Entidade | Timestamp de última atualização. |

> **Observação:** O campo `senha` não é retornado na resposta.

---

## 3. Regras de validação

### Validações de campos (Bean Validation — DTOs)

- **`nome`** — obrigatório na criação (`@NotBlank`), máximo 150 caracteres (`@Size(max = 150)`). No update, é opcional.
- **`email`** — obrigatório na criação (`@NotBlank`), formato de e-mail válido (`@Email`), máximo 150 caracteres (`@Size(max = 150)`). No update, é opcional.
- **`senha`** — obrigatório na criação (`@NotBlank`), mínimo 6 e máximo 255 caracteres (`@Size(min = 6, max = 255)`). No update, é opcional.
- **`funcionarioId`** — obrigatório na criação (`@NotNull`). No update, é opcional.

### Validações customizadas (UsuarioValidator)

- **`validateUniqueEmail(email)`** — Garante que o e-mail informado não existe em outro usuário. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueEmailForUpdate(email, id)`** — Na atualização, garante que o e-mail informado não pertence a outro usuário diferente do atual. Dispara `DuplicateResourceException` se conflitar.
- **`validateUniqueFuncionarioId(funcionarioId)`** — Garante que o funcionário informado não possui usuário cadastrado. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueFuncionarioIdForUpdate(funcionarioId, id)`** — Na atualização, garante que o funcionário informado não está vinculado a outro usuário diferente do atual. Dispara `DuplicateResourceException` se conflitar.
- **`validateStatus(status)`** — Garante que o status é exatamente `ACTIVE` ou `INACTIVE`. Dispara `BusinessException` para qualquer outro valor.

### Validações de existência no Service

- **`funcionarioId`** — Na criação e atualização, o `Employee` referenciado deve existir. Caso contrário, retorna HTTP 404 (`ResourceNotFoundException`).

---

## 4. Unicidade

| Campo | Regra | Onde é garantida | Comportamento na duplicidade |
|--------|-------|------------------|------------------------------|
| `email` | UNIQUE | Migration (`V8__create_table_usuario.sql`) + `UsuarioValidator.validateUniqueEmail()` + `UsuarioValidator.validateUniqueEmailForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |
| `funcionario_id` | UNIQUE | Migration (`V8__create_table_usuario.sql`) + `UsuarioValidator.validateUniqueFuncionarioId()` + `UsuarioValidator.validateUniqueFuncionarioIdForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |

---

## 5. Status

### Valores possíveis

| Valor | Significado |
|-------|-------------|
| `ACTIVE` | Usuário ativo. |
| `INACTIVE` | Usuário inativo (soft delete). |

### Comportamento

- **Criação:** O valor padrão definido no builder é `ACTIVE`.
- **Atualização:** O campo `status` é opcional. Se informado, deve ser `ACTIVE` ou `INACTIVE`. Qualquer outro valor dispara `BusinessException` (HTTP 400).
- **DELETE:** Não é exclusão física. O DELETE marca o usuário como `INACTIVE` (soft delete).
- **Reativação:** Existe endpoint `PATCH /api/v1/usuarios/{id}/activate` para reativar um usuário.
- **Desativação:** Existe endpoint `PATCH /api/v1/usuarios/{id}/deactivate` para desativar um usuário.
- **Endpoint específico para status:** Além do PUT, existem endpoints dedicados para alterar status.

---

## 6. Relacionamentos

### Employee

| Aspecto | Comportamento |
|---------|---------------|
| **Obrigatoriedade** | Obrigatório. `funcionario_id` é `NOT NULL` e `UNIQUE` na migration. |
| **Tipo** | `@OneToOne(fetch = FetchType.LAZY)` |
| **FK** | `usuario.funcionario_id` → `employee.id` |
| **Criação** | `funcionarioId` é obrigatório no `CreateUsuarioRequest`. O `Employee` deve existir. |
| **Atualização** | `funcionarioId` é opcional no `UpdateUsuarioRequest`. Se informado, o novo `Employee` deve existir. |

---

## 7. Regras específicas do módulo

- O campo `ultimoAcesso` é retornado na resposta, mas não há lógica de atualização automática dele no código atual do módulo.
- O summary retorna `total`, `active` e `inactive` baseado em consultas específicas do repositório.

---

## 8. Regras de criação

**Endpoint:** `POST /api/v1/usuarios`

Fluxo:

1. Recebe `CreateUsuarioRequest` no body.
2. Validação Bean Validation é aplicada automaticamente (`@Valid`).
3. `UsuarioValidator` valida unicidade de `email` e `funcionarioId`.
4. Busca `Employee` por `funcionarioId`. Se não encontrado, retorna HTTP 404.
5. Cria entidade `Usuario` com os dados recebidos. O campo `status` é definido como `ACTIVE` por padrão.
6. Persiste no banco.
7. Retorna HTTP 201 com `UsuarioResponse`.

**Campos obrigatórios na criação:**
- `nome`
- `email`
- `senha`
- `funcionarioId`

**Possíveis conflitos na criação:**
- E-mail duplicado → HTTP 409
- Funcionário já possui usuário → HTTP 409
- Funcionário não encontrado → HTTP 404

---

## 9. Regras de atualização

**Endpoint:** `PUT /api/v1/usuarios/{id}`

Fluxo:

1. Recebe `UpdateUsuarioRequest` no body.
2. Busca usuário por ID. Se não encontrado, retorna HTTP 404.
3. Atualização é **parcial**: apenas campos não nulos são atualizados.
4. Se `nome` for informado, atualiza.
5. Se `email` for informado e diferente do atual, valida unicidade e atualiza.
6. Se `senha` for informada, atualiza.
7. Se `funcionarioId` for informado e diferente do atual, valida unicidade, busca o `Employee` e atualiza o vínculo. Se não encontrado, retorna HTTP 404.
8. Se `status` for informado, valida e atualiza.
9. Persiste alterações.
10. Retorna HTTP 200 com `UsuarioResponse`.

**Campos permitidos no update (todos opcionais):**
- `nome`
- `email`
- `senha`
- `funcionarioId`
- `status`

**Comportamento quando Usuario não existe:**
- Retorna HTTP 404 (`ResourceNotFoundException`).

---

## 10. Regras de exclusão

**Endpoint:** `DELETE /api/v1/usuarios/{id}`

| Aspecto | Comportamento |
|---------|---------------|
| **Tipo** | Soft delete (não é exclusão física). |
| **Ação** | Define o campo `status` como `INACTIVE`. |
| **Resposta HTTP** | `204 No Content` |
| **Registro não existe** | Retorna HTTP 404 (`ResourceNotFoundException`). |

- O registro é mantido no banco de dados.
- Existem endpoints de reativação/desativação: `PATCH /api/v1/usuarios/{id}/activate` e `PATCH /api/v1/usuarios/{id}/deactivate`.

---

## 11. Endpoints

| Método | Endpoint | Finalidade | Regras relevantes |
|--------|----------|------------|-------------------|
| `POST` | `/api/v1/usuarios` | Criar usuário | E-mail e funcionário únicos. Campos obrigatórios: nome, e-mail, senha, funcionário. Status default `ACTIVE`. |
| `GET` | `/api/v1/usuarios` | Listar usuários (paginado) | Filtros: `search`, `email`, `status`, `funcionarioId`. Paginação padrão: 20 por página, ordenado por `id ASC`. |
| `GET` | `/api/v1/usuarios/summary` | Resumo de usuários | Retorna totais: `total`, `active`, `inactive`. |
| `GET` | `/api/v1/usuarios/{id}` | Buscar usuário por ID | Retorna dados completos. |
| `PUT` | `/api/v1/usuarios/{id}` | Atualizar usuário | Atualização parcial. E-mail e funcionário validados para unicidade. Status validado (`ACTIVE`/`INACTIVE`). |
| `DELETE` | `/api/v1/usuarios/{id}` | Remover usuário | Soft delete: define `status = INACTIVE`. |
| `PATCH` | `/api/v1/usuarios/{id}/activate` | Ativar usuário | Define status como `ACTIVE`. |
| `PATCH` | `/api/v1/usuarios/{id}/deactivate` | Desativar usuário | Define status como `INACTIVE`. |

---

## 12. Filtros e paginação

### Filtros disponíveis em `GET /api/v1/usuarios`

| Parâmetro | Tipo | Descrição | Comportamento |
|-----------|------|-----------|---------------|
| `search` | String | Busca por nome | Pesquisa parcial case-insensitive no campo `nome`. |
| `email` | String | Filtro por e-mail | Busca parcial case-insensitive no campo `email`. |
| `status` | String | Filtro por status | Aceita `ACTIVE`, `INACTIVE` ou `ALL`. Se `ALL` ou vazio, não filtra. |
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

---

## 13. Regras que o Frontend deve respeitar

- `nome` é obrigatório na criação.
- `email` é obrigatório na criação, deve ser um e-mail válido e é único.
- `senha` é obrigatório na criação, com mínimo de 6 e máximo de 255 caracteres.
- `funcionarioId` é obrigatório na criação. O frontend deve selecionar um funcionário existente.
- Atualização é parcial: enviar apenas campos que devem ser alterados.
- Na atualização, se `email` ou `funcionarioId` forem alterados, a unicidade é validada contra outros usuários.
- `status` pode ser alterado no update, ou via endpoints `activate`/`deactivate`.
- DELETE não remove o registro: marca o usuário como `INACTIVE`.
- Existem endpoints de reativação (`activate`) e desativação (`deactivate`).
- Listagem suporta filtros por e-mail, status, funcionário e busca por nome.
- Paginação padrão é de 20 registros por página, ordenados por ID crescente.
- Resumo (`/summary`) retorna contadores de total, ativos e inativos.

---

## 14. Fontes analisadas

- `src/main/java/br/com/logicore/modules/usuario/entity/Usuario.java`
- `src/main/java/br/com/logicore/modules/usuario/enums/UserStatus.java`
- `src/main/java/br/com/logicore/modules/usuario/dto/CreateUsuarioRequest.java`
- `src/main/java/br/com/logicore/modules/usuario/dto/UpdateUsuarioRequest.java`
- `src/main/java/br/com/logicore/modules/usuario/dto/UsuarioResponse.java`
- `src/main/java/br/com/logicore/modules/usuario/dto/UsuarioSummary.java`
- `src/main/java/br/com/logicore/modules/usuario/service/UsuarioService.java`
- `src/main/java/br/com/logicore/modules/usuario/controller/UsuarioController.java`
- `src/main/java/br/com/logicore/modules/usuario/validator/UsuarioValidator.java`
- `src/main/java/br/com/logicore/modules/usuario/repository/UsuarioRepository.java`
- `src/main/java/br/com/logicore/modules/usuario/repository/spec/UsuarioSpecifications.java`
- `src/main/java/br/com/logicore/modules/usuario/mapper/UsuarioMapper.java`
- `src/main/resources/db/migration/V8__create_table_usuario.sql`
