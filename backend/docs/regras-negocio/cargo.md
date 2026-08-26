# Regras de Negócio — Cargo

## 1. Identificação

| Item | Valor |
|------|-------|
| **Módulo** | Cargo |
| **Entidade** | Cargo |
| **Tabela** | `cargo` |
| **Endpoint base** | `/api/v1/cargos` |
| **Objetivo** | Gerenciar cadastro de cargos (posições) da empresa, com nome, código, descrição e status ativo/inativo. |

---

## 2. Campos

### Campos de entrada (Create / Update)

| Campo | Tipo | Obrigatório | Tamanho/Formato | Regra | Observação |
|--------|------|-------------|-----------------|-------|------------|
| `nome` | String | Sim (criação) | Máx. 100 caracteres | Único no sistema. Validado por `CargoValidator`. | No update, é opcional (parcial). |
| `codigo` | String | Sim (criação) | Máx. 20 caracteres | Único no sistema. Validado por `CargoValidator`. | No update, é opcional (parcial). |
| `descricao` | String | Não | Máx. 255 caracteres | — | Opcional em criação e atualização. |
| `ativo` | Boolean | Não | — | — | Apenas no update. Controla o status ativo/inativo. |

### Campos somente de resposta (CargoResponse)

| Campo | Tipo | Origem | Observação |
|--------|------|--------|------------|
| `id` | Long | PK gerada pelo banco | Somente leitura. |
| `nome` | String | Entidade | Retornado na resposta. |
| `descricao` | String | Entidade | Retornado na resposta. |
| `codigo` | String | Entidade | Retornado na resposta. |
| `ativo` | Boolean | Entidade | Retornado na resposta. |
| `criadoEm` | LocalDateTime | Entidade | Timestamp de criação. |
| `atualizadoEm` | LocalDateTime | Entidade | Timestamp de última atualização. |

---

## 3. Regras de validação

### Validações de campos (Bean Validation — DTOs)

- **`nome`** — obrigatório na criação (`@NotBlank`), máximo 100 caracteres (`@Size(max = 100)`). No update, é opcional.
- **`codigo`** — obrigatório na criação (`@NotBlank`), máximo 20 caracteres (`@Size(max = 20)`). No update, é opcional.
- **`descricao`** — opcional, máximo 255 caracteres.
- **`ativo`** — opcional, apenas no update.

### Validações customizadas (CargoValidator)

- **`validateUniqueName(nome)`** — Garante que o nome informado não existe em outro cargo. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueCode(codigo)`** — Garante que o código informado não existe em outro cargo. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueNameForUpdate(nome, id)`** — Na atualização, garante que o nome informado não pertence a outro cargo diferente do atual. Dispara `DuplicateResourceException` se conflitar.
- **`validateUniqueCodeForUpdate(codigo, id)`** — Na atualização, garante que o código informado não pertence a outro cargo diferente do atual. Dispara `DuplicateResourceException` se conflitar.

---

## 4. Unicidade

| Campo | Regra | Onde é garantida | Comportamento na duplicidade |
|--------|-------|------------------|------------------------------|
| `nome` | UNIQUE | Migration (`V2__create_table_cargo.sql`) + `CargoValidator.validateUniqueName()` + `CargoValidator.validateUniqueNameForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |
| `codigo` | UNIQUE | Migration (`V2__create_table_cargo.sql`) + `CargoValidator.validateUniqueCode()` + `CargoValidator.validateUniqueCodeForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |

---

## 5. Status

### Valores possíveis

| Valor | Campo | Significado |
|-------|-------|-------------|
| `true` | `ativo` | Cargo ativo. |
| `false` | `ativo` | Cargo inativo. |

### Comportamento

- **Criação:** O valor padrão definido na migration e no builder é `true` (ativo).
- **Atualização:** O campo `ativo` é opcional no `UpdateCargoRequest`. Se informado, atualiza o status.
- **Ativação/Desativação:** Existem endpoints dedicados: `PATCH /api/v1/cargos/{id}/activate` e `PATCH /api/v1/cargos/{id}/deactivate`.
- **DELETE:** Não existe endpoint DELETE no Cargo.
- **Soft delete:** Não existe. O status é alterado apenas via activate/deactivate.

---

## 6. Relacionamentos

O módulo Cargo não possui relacionamentos com outras entidades no backend.

> **Observação:** O Cargo é referenciado pelo módulo Employee (`employee.cargo_id` → `cargo.id`), mas essa regra de integridade pertence ao módulo Employee.

---

## 7. Regras específicas do módulo

- O status é controlado pelo campo booleano `ativo`, não por um enum.
- O status é alterado exclusivamente via endpoints `activate` e `deactivate`, ou via PUT no campo `ativo`.
- Não há endpoint DELETE.

---

## 8. Regras de criação

**Endpoint:** `POST /api/v1/cargos`

Fluxo:

1. Recebe `CreateCargoRequest` no body.
2. Validação Bean Validation é aplicada automaticamente (`@Valid`).
3. `CargoValidator` valida unicidade de `nome` e `codigo`.
4. Cria entidade `Cargo` com os dados recebidos. O campo `ativo` é definido como `true` por padrão.
5. Persiste no banco.
6. Retorna HTTP 201 com `CargoResponse`.

**Campos obrigatórios na criação:**
- `nome`
- `codigo`

**Possíveis conflitos na criação:**
- Nome duplicado → HTTP 409
- Código duplicado → HTTP 409

---

## 9. Regras de atualização

**Endpoint:** `PUT /api/v1/cargos/{id}`

Fluxo:

1. Recebe `UpdateCargoRequest` no body.
2. Busca cargo por ID. Se não encontrado, retorna HTTP 404.
3. Atualização é **parcial**: apenas campos não nulos são atualizados.
4. Se `nome` for informado e diferente do atual, valida unicidade.
5. Se `codigo` for informado e diferente do atual, valida unicidade.
6. Se `descricao` for informada, atualiza.
7. Se `ativo` for informado e diferente do atual, atualiza.
8. Persiste alterações.
9. Retorna HTTP 200 com `CargoResponse`.

**Campos permitidos no update (todos opcionais):**
- `nome`
- `codigo`
- `descricao`
- `ativo`

**Comportamento quando Cargo não existe:**
- Retorna HTTP 404 (`ResourceNotFoundException`).

---

## 10. Regras de exclusão

**Endpoint:** Não existe endpoint DELETE para Cargo.

O módulo não possui exclusão física nem soft delete. O status é alterado apenas via:
- `PATCH /api/v1/cargos/{id}/activate`
- `PATCH /api/v1/cargos/{id}/deactivate`

---

## 11. Endpoints

| Método | Endpoint | Finalidade | Regras relevantes |
|--------|----------|------------|-------------------|
| `POST` | `/api/v1/cargos` | Criar cargo | Nome e código únicos. Campos obrigatórios: nome, código. Status default `true`. |
| `GET` | `/api/v1/cargos` | Listar cargos (paginado) | Filtros: `search`, `active`. Paginação padrão: 20 por página, ordenado por `id ASC`. |
| `GET` | `/api/v1/cargos/summary` | Resumo de cargos | Retorna totais: `total`, `active`, `inactive`. |
| `GET` | `/api/v1/cargos/{id}` | Buscar cargo por ID | Retorna dados completos. |
| `PUT` | `/api/v1/cargos/{id}` | Atualizar cargo | Atualização parcial. Nome e código validados para unicidade. Campo `ativo` opcional. |
| `PATCH` | `/api/v1/cargos/{id}/activate` | Ativar cargo | Define `ativo = true`. |
| `PATCH` | `/api/v1/cargos/{id}/deactivate` | Desativar cargo | Define `ativo = false`. |

---

## 12. Filtros e paginação

### Filtros disponíveis em `GET /api/v1/cargos`

| Parâmetro | Tipo | Descrição | Comportamento |
|-----------|------|-----------|---------------|
| `search` | String | Busca geral | Pesquisa em: `nome`, `codigo` (case-insensitive, parcial). |
| `active` | Boolean | Filtro por status ativo | Aceita `true` ou `false`. Se nulo, não filtra. |

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

- `nome` é obrigatório na criação e único.
- `codigo` é obrigatório na criação e único.
- `descricao` é opcional.
- Atualização é parcial: enviar apenas campos que devem ser alterados.
- Na atualização, se `nome` ou `codigo` forem alterados, a unicidade é validada contra outros cargos.
- `ativo` pode ser alterado no update ou via endpoints `activate`/`deactivate`.
- Não existe endpoint DELETE.
- Listagem suporta filtro por status ativo e busca geral.
- Paginação padrão é de 20 registros por página, ordenados por ID crescente.
- Resumo (`/summary`) retorna contadores de total, ativos e inativos.

---

## 14. Fontes analisadas

- `src/main/java/br/com/logicore/modules/cargo/entity/Cargo.java`
- `src/main/java/br/com/logicore/modules/cargo/dto/CreateCargoRequest.java`
- `src/main/java/br/com/logicore/modules/cargo/dto/UpdateCargoRequest.java`
- `src/main/java/br/com/logicore/modules/cargo/dto/CargoResponse.java`
- `src/main/java/br/com/logicore/modules/cargo/dto/CargoSummaryResponse.java`
- `src/main/java/br/com/logicore/modules/cargo/service/CargoService.java`
- `src/main/java/br/com/logicore/modules/cargo/controller/CargoController.java`
- `src/main/java/br/com/logicore/modules/cargo/validator/CargoValidator.java`
- `src/main/java/br/com/logicore/modules/cargo/repository/CargoRepository.java`
- `src/main/java/br/com/logicore/modules/cargo/repository/spec/CargoSpecifications.java`
- `src/main/java/br/com/logicore/modules/cargo/mapper/CargoMapper.java`
- `src/main/resources/db/migration/V2__create_table_cargo.sql`
