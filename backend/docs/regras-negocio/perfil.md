# Regras de Negócio — Perfil

## 1. Identificação

| Item | Valor |
|------|-------|
| **Módulo** | Perfil |
| **Entidade** | Perfil |
| **Tabela** | `perfil` |
| **Endpoint base** | `/api/v1/perfis` |
| **Objetivo** | Gerenciar cadastro de perfis de acesso do sistema, com nome e descrição. |

---

## 2. Campos

### Campos de entrada (Create / Update)

| Campo | Tipo | Obrigatório | Tamanho/Formato | Regra | Observação |
|--------|------|-------------|-----------------|-------|------------|
| `nome` | String | Sim (criação) | Máx. 100 caracteres | Único no sistema. Validado por `PerfilValidator`. | No update, é opcional (parcial). |
| `descricao` | String | Não | Máx. 255 caracteres | — | Opcional em criação e atualização. |

### Campos somente de resposta (PerfilResponse)

| Campo | Tipo | Origem | Observação |
|--------|------|--------|------------|
| `id` | Long | PK gerada pelo banco | Somente leitura. |
| `nome` | String | Entidade | Retornado na resposta. |
| `descricao` | String | Entidade | Retornado na resposta. |
| `criadoEm` | LocalDateTime | Entidade | Timestamp de criação. |
| `atualizadoEm` | LocalDateTime | Entidade | Timestamp de última atualização. |

---

## 3. Regras de validação

### Validações de campos (Bean Validation — DTOs)

- **`nome`** — obrigatório na criação (`@NotBlank`), máximo 100 caracteres (`@Size(max = 100)`). No update, é opcional.
- **`descricao`** — opcional, máximo 255 caracteres.

### Validações customizadas (PerfilValidator)

- **`validateUniqueNome(nome)`** — Garante que o nome informado não existe em outro perfil. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueNomeForUpdate(nome, id)`** — Na atualização, garante que o nome informado não pertence a outro perfil diferente do atual. Dispara `DuplicateResourceException` se conflitar.

---

## 4. Unicidade

| Campo | Regra | Onde é garantida | Comportamento na duplicidade |
|--------|-------|------------------|------------------------------|
| `nome` | UNIQUE | Migration (`V9__create_table_perfil.sql`) + `PerfilValidator.validateUniqueNome()` + `PerfilValidator.validateUniqueNomeForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |

---

## 5. Status

O módulo Perfil não possui campo de status.

---

## 6. Relacionamentos

O módulo Perfil não possui relacionamentos com outras entidades no backend.

> **Observação:** Perfis são associados a Usuários via módulo `UsuarioPerfil`, mas essa regra de relacionamento pertence ao módulo `UsuarioPerfil`.

---

## 7. Regras específicas do módulo

- Não há regras específicas além das validações de unicidade.
- O summary retorna apenas o `total` de perfis.

---

## 8. Regras de criação

**Endpoint:** `POST /api/v1/perfis`

Fluxo:

1. Recebe `CreatePerfilRequest` no body.
2. Validação Bean Validation é aplicada automaticamente (`@Valid`).
3. `PerfilValidator` valida unicidade de `nome`.
4. Cria entidade `Perfil` com os dados recebidos.
5. Persiste no banco.
6. Retorna HTTP 201 com `PerfilResponse`.

**Campos obrigatórios na criação:**
- `nome`

**Possíveis conflitos na criação:**
- Nome duplicado → HTTP 409

---

## 9. Regras de atualização

**Endpoint:** `PUT /api/v1/perfis/{id}`

Fluxo:

1. Recebe `UpdatePerfilRequest` no body.
2. Busca perfil por ID. Se não encontrado, retorna HTTP 404.
3. Atualização é **parcial**: apenas campos não nulos são atualizados.
4. Se `nome` for informado e diferente do atual, valida unicidade.
5. Se `descricao` for informada, atualiza.
6. Persiste alterações.
7. Retorna HTTP 200 com `PerfilResponse`.

**Campos permitidos no update (todos opcionais):**
- `nome`
- `descricao`

**Comportamento quando Perfil não existe:**
- Retorna HTTP 404 (`ResourceNotFoundException`).

---

## 10. Regras de exclusão

**Endpoint:** `DELETE /api/v1/perfis/{id}`

| Aspecto | Comportamento |
|---------|---------------|
| **Tipo** | Exclusão física. |
| **Ação** | Remove o registro do banco de dados. |
| **Resposta HTTP** | `204 No Content` |
| **Registro não existe** | Retorna HTTP 404 (`ResourceNotFoundException`). |

- O registro é removido permanentemente.
- Associações com usuários (via `UsuarioPerfil`) podem ser afetadas dependendo da configuração de cascade no banco.

---

## 11. Endpoints

| Método | Endpoint | Finalidade | Regras relevantes |
|--------|----------|------------|-------------------|
| `POST` | `/api/v1/perfis` | Criar perfil | Nome único. Campo obrigatório: nome. |
| `GET` | `/api/v1/perfis` | Listar perfis (paginado) | Filtros: `search`. Paginação padrão: 20 por página, ordenado por `id ASC`. |
| `GET` | `/api/v1/perfis/summary` | Resumo de perfis | Retorna apenas `total`. |
| `GET` | `/api/v1/perfis/{id}` | Buscar perfil por ID | Retorna dados completos. |
| `PUT` | `/api/v1/perfis/{id}` | Atualizar perfil | Atualização parcial. Nome validado para unicidade. |
| `DELETE` | `/api/v1/perfis/{id}` | Remover perfil | Exclusão física. |

---

## 12. Filtros e paginação

### Filtros disponíveis em `GET /api/v1/perfis`

| Parâmetro | Tipo | Descrição | Comportamento |
|-----------|------|-----------|---------------|
| `search` | String | Busca por nome ou descrição | Pesquisa parcial case-insensitive em `nome` e `descricao`. |

### Paginação

| Aspecto | Valor |
|---------|-------|
| **Tamanho padrão** | 20 registros por página |
| **Ordenação padrão** | `id ASC` |
| **Parâmetros** | `page`, `size`, `sort` (padrão Spring Data) |

### Comportamento dos filtros

- Filtros vazios ou nulos são ignorados.
- O filtro `search` usa `OR` entre `nome` e `descricao`.

---

## 13. Regras que o Frontend deve respeitar

- `nome` é obrigatório na criação e único.
- `descricao` é opcional.
- Atualização é parcial: enviar apenas campos que devem ser alterados.
- Na atualização, se `nome` for alterado, a unicidade é validada contra outros perfis.
- DELETE é físico: remove o registro permanentemente.
- Listagem suporta busca por nome ou descrição.
- Paginação padrão é de 20 registros por página, ordenados por ID crescente.
- Resumo (`/summary`) retorna apenas o total de perfis.

---

## 14. Fontes analisadas

- `src/main/java/br/com/logicore/modules/perfil/entity/Perfil.java`
- `src/main/java/br/com/logicore/modules/perfil/dto/CreatePerfilRequest.java`
- `src/main/java/br/com/logicore/modules/perfil/dto/UpdatePerfilRequest.java`
- `src/main/java/br/com/logicore/modules/perfil/dto/PerfilResponse.java`
- `src/main/java/br/com/logicore/modules/perfil/dto/PerfilSummary.java`
- `src/main/java/br/com/logicore/modules/perfil/service/PerfilService.java`
- `src/main/java/br/com/logicore/modules/perfil/controller/PerfilController.java`
- `src/main/java/br/com/logicore/modules/perfil/validator/PerfilValidator.java`
- `src/main/java/br/com/logicore/modules/perfil/repository/PerfilRepository.java`
- `src/main/java/br/com/logicore/modules/perfil/repository/spec/PerfilSpecifications.java`
- `src/main/java/br/com/logicore/modules/perfil/mapper/PerfilMapper.java`
- `src/main/resources/db/migration/V9__create_table_perfil.sql`
