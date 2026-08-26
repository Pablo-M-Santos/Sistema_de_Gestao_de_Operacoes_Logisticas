# Regras de Negócio — UsuarioPerfil

## 1. Identificação

| Item | Valor |
|------|-------|
| **Módulo** | UsuarioPerfil |
| **Entidade** | UsuarioPerfil |
| **Tabela** | `usuario_perfil` |
| **Endpoint base** | `/api/v1/usuarios-perfis` |
| **Objetivo** | Gerenciar a associação N:N entre Usuário e Perfil. O vínculo representa a inclusão de um usuário em um perfil. |

---

## 2. Campos

### Campos de entrada (Create)

| Campo | Tipo | Obrigatório | Tamanho/Formato | Regra | Observação |
|--------|------|-------------|-----------------|-------|------------|
| `usuarioId` | Long | Sim | — | Deve referenciar um `Usuario` existente. | Apenas no create. |
| `perfilId` | Long | Sim | — | Deve referenciar um `Perfil` existente. | Apenas no create. |

### Campos somente de resposta (UsuarioPerfilResponse)

| Campo | Tipo | Origem | Observação |
|--------|------|--------|------------|
| `id` | Long | PK gerada pelo banco | Somente leitura. |
| `usuarioId` | Long | FK → Usuario | Retornado na resposta. |
| `perfilId` | Long | FK → Perfil | Retornado na resposta. |
| `nomeUsuario` | String | Usuario.nome | Retornado na resposta. |
| `nomePerfil` | String | Perfil.nome | Retornado na resposta. |

> **Observação:** O módulo não possui DTO de atualização. O relacionamento é tratado como criação ou remoção de vínculo.

---

## 3. Regras de validação

### Validações de campos (Bean Validation — DTOs)

- **`usuarioId`** — obrigatório (`@NotNull`).
- **`perfilId`** — obrigatório (`@NotNull`).

### Validações customizadas (UsuarioPerfilValidator)

- **`validateUsuarioExists(usuarioId)`** — Garante que o `Usuario` informado existe. Dispara `ResourceNotFoundException` se não existir.
- **`validatePerfilExists(perfilId)`** — Garante que o `Perfil` informado existe. Dispara `ResourceNotFoundException` se não existir.
- **`validateUniqueAssociation(usuarioId, perfilId)`** — Garante que a combinação `usuarioId` + `perfilId` não existe. Dispara `DuplicateResourceException` se já existir.

### Validações no Service

- Na criação, busca `Usuario` por `usuarioId` e `Perfil` por `perfilId`. Se qualquer um não existir, retorna HTTP 404.

---

## 4. Unicidade

| Campo | Regra | Onde é garantida | Comportamento na duplicidade |
|--------|-------|------------------|------------------------------|
| `(usuario_id, perfil_id)` | UNIQUE composta | Migration (`V11__create_table_usuario_perfil.sql`) + `UsuarioPerfilValidator.validateUniqueAssociation()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |

---

## 5. Status

O módulo UsuarioPerfil não possui campo de status.

---

## 6. Relacionamentos

### Usuario

| Aspecto | Comportamento |
|---------|---------------|
| **Obrigatoriedade** | Obrigatório. `usuario_id` é `NOT NULL` na migration. |
| **Tipo** | `@ManyToOne(fetch = FetchType.LAZY)` |
| **FK** | `usuario_perfil.usuario_id` → `usuario.id` |
| **Criação** | `usuarioId` é obrigatório no `CreateUsuarioPerfilRequest`. O `Usuario` deve existir. |
| **Atualização** | Não há atualização. O vínculo é criado ou removido. |

### Perfil

| Aspecto | Comportamento |
|---------|---------------|
| **Obrigatoriedade** | Obrigatório. `perfil_id` é `NOT NULL` na migration. |
| **Tipo** | `@ManyToOne(fetch = FetchType.LAZY)` |
| **FK** | `usuario_perfil.perfil_id` → `perfil.id` |
| **Criação** | `perfilId` é obrigatório no `CreateUsuarioPerfilRequest`. O `Perfil` deve existir. |
| **Atualização** | Não há atualização. O vínculo é criado ou removido. |

---

## 7. Regras específicas do módulo

- O módulo gerencia apenas a associação entre `Usuario` e `Perfil`.
- Não há relacionamento com `Permissao` neste módulo.
- A entidade não possui timestamps (`criado_em`, `atualizado_em`).
- Não há campo de status.
- A exclusão do vínculo é física: remove a linha da tabela `usuario_perfil`.

---

## 8. Regras de criação

**Endpoint:** `POST /api/v1/usuarios-perfis`

Fluxo:

1. Recebe `CreateUsuarioPerfilRequest` no body.
2. Validação Bean Validation é aplicada automaticamente (`@Valid`).
3. `UsuarioPerfilValidator` valida existência de `usuarioId` e `perfilId`.
4. `UsuarioPerfilValidator` valida unicidade da associação.
5. Busca `Usuario` por `usuarioId`. Se não encontrado, retorna HTTP 404.
6. Busca `Perfil` por `perfilId`. Se não encontrado, retorna HTTP 404.
7. Cria entidade `UsuarioPerfil` com os dados recebidos.
8. Persiste no banco.
9. Retorna HTTP 201 com `UsuarioPerfilResponse`.

**Campos obrigatórios na criação:**
- `usuarioId`
- `perfilId`

**Possíveis conflitos na criação:**
- Usuário não encontrado → HTTP 404
- Perfil não encontrado → HTTP 404
- Associação duplicada → HTTP 409

---

## 9. Regras de atualização

O módulo não possui endpoint de atualização (PUT/PATCH).

O relacionamento é tratado como:
- Criação de vínculo via `POST /api/v1/usuarios-perfis`
- Remoção de vínculo via `DELETE /api/v1/usuarios-perfis/{id}`

---

## 10. Regras de exclusão

**Endpoint:** `DELETE /api/v1/usuarios-perfis/{id}`

| Aspecto | Comportamento |
|---------|---------------|
| **Tipo** | Exclusão física (apenas o vínculo). |
| **Ação** | Remove o registro da tabela `usuario_perfil`. |
| **Resposta HTTP** | `204 No Content` |
| **Registro não existe** | Retorna HTTP 404 (`ResourceNotFoundException`). |

- O `Usuario` e o `Perfil` não são afetados.
- Apenas a associação é removida.

---

## 11. Endpoints

| Método | Endpoint | Finalidade | Regras relevantes |
|--------|----------|------------|-------------------|
| `POST` | `/api/v1/usuarios-perfis` | Criar associação | Usuário e perfil devem existir. Associação única por combinação usuário+perfil. |
| `GET` | `/api/v1/usuarios-perfis` | Listar associações (paginado) | Filtros: `search`, `usuarioId`, `perfilId`. Paginação padrão: 20 por página, ordenado por `id ASC`. |
| `GET` | `/api/v1/usuarios-perfis/summary` | Resumo de associações | Retorna apenas `total`. |
| `GET` | `/api/v1/usuarios-perfis/{id}` | Buscar associação por ID | Retorna dados completos com nomes de usuário e perfil. |
| `DELETE` | `/api/v1/usuarios-perfis/{id}` | Remover associação | Exclusão física do vínculo. Usuário e perfil não são afetados. |

---

## 12. Filtros e paginação

### Filtros disponíveis em `GET /api/v1/usuarios-perfis`

| Parâmetro | Tipo | Descrição | Comportamento |
|-----------|------|-----------|---------------|
| `search` | String | Busca por nome de usuário ou perfil | Pesquisa parcial case-insensitive em `usuario.nome` e `perfil.nome`. |
| `usuarioId` | Long | Filtro por usuário | Busca exata pelo ID do usuário. |
| `perfilId` | Long | Filtro por perfil | Busca exata pelo ID do perfil. |

### Paginação

| Aspecto | Valor |
|---------|-------|
| **Tamanho padrão** | 20 registros por página |
| **Ordenação padrão** | `id ASC` |
| **Parâmetros** | `page`, `size`, `sort` (padrão Spring Data) |

### Comportamento dos filtros

- Filtros são combinados com `AND`.
- Filtros vazios ou nulos são ignorados.
- O filtro `search` usa `OR` entre os nomes de usuário e perfil.

---

## 13. Regras que o Frontend deve respeitar

- `usuarioId` é obrigatório na criação. O frontend deve selecionar um usuário existente.
- `perfilId` é obrigatório na criação. O frontend deve selecionar um perfil existente.
- A mesma combinação de usuário + perfil não pode ser cadastrada mais de uma vez.
- Atualização não existe: para alterar a associação, remova-a e crie uma nova.
- DELETE remove apenas o vínculo. O usuário e o perfil não são afetados.
- Listagem suporta filtros por usuário, perfil e busca por nomes.
- Paginação padrão é de 20 registros por página, ordenados por ID crescente.
- Resumo (`/summary`) retorna apenas o total de associações.

---

## 14. Fontes analisadas

- `src/main/java/br/com/logicore/modules/usuarioperfil/entity/UsuarioPerfil.java`
- `src/main/java/br/com/logicore/modules/usuarioperfil/dto/CreateUsuarioPerfilRequest.java`
- `src/main/java/br/com/logicore/modules/usuarioperfil/dto/UsuarioPerfilResponse.java`
- `src/main/java/br/com/logicore/modules/usuarioperfil/dto/UsuarioPerfilSummary.java`
- `src/main/java/br/com/logicore/modules/usuarioperfil/service/UsuarioPerfilService.java`
- `src/main/java/br/com/logicore/modules/usuarioperfil/controller/UsuarioPerfilController.java`
- `src/main/java/br/com/logicore/modules/usuarioperfil/validator/UsuarioPerfilValidator.java`
- `src/main/java/br/com/logicore/modules/usuarioperfil/repository/UsuarioPerfilRepository.java`
- `src/main/java/br/com/logicore/modules/usuarioperfil/repository/spec/UsuarioPerfilSpecifications.java`
- `src/main/java/br/com/logicore/modules/usuarioperfil/mapper/UsuarioPerfilMapper.java`
- `src/main/resources/db/migration/V11__create_table_usuario_perfil.sql`
