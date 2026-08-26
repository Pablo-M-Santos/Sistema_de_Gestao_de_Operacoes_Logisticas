# Regras de Negócio — Client

## 1. Identificação

| Item | Valor |
|------|-------|
| **Módulo** | Client |
| **Entidade** | Client |
| **Tabela** | `client` |
| **Endpoint base** | `/api/v1/clients` |
| **Objetivo** | Gerenciar cadastro de clientes (pessoa jurídica), com dados de empresa, CNPJ, contato e endereço opcional. |

---

## 2. Campos

### Campos de entrada (Create / Update)

| Campo | Tipo | Obrigatório | Tamanho/Formato | Regra | Observação |
|--------|------|-------------|-----------------|-------|------------|
| `razaoSocial` | String | Sim (criação) | Máx. 150 caracteres | — | No update, é opcional (parcial). |
| `nomeFantasia` | String | Não | Máx. 150 caracteres | — | Opcional em criação e atualização. |
| `cnpj` | String | Sim (criação) | Exatamente 14 caracteres | Único no sistema. Validado por `ClientValidator`. | No update, é opcional (parcial). |
| `inscricaoEstadual` | String | Não | Máx. 30 caracteres | — | Opcional em criação e atualização. |
| `telefone` | String | Não | Máx. 20 caracteres | — | Opcional em criação e atualização. |
| `email` | String | Não | Máx. 150 caracteres | Formato de e-mail válido (conforme Bean Validation `@Email`). | Opcional em criação e atualização. |
| `contatoPrincipal` | String | Não | Máx. 150 caracteres | — | Opcional em criação e atualização. |
| `enderecoId` | Long | Não | — | Deve referenciar um `Address` existente, se informado. | Opcional em criação e atualização. |
| `status` | String | Não | — | Valores permitidos: `ACTIVE` ou `INACTIVE`. Validado por `ClientValidator.validateStatus()`. | Apenas no update. |

### Campos somente de resposta (ClientResponse)

| Campo | Tipo | Origem | Observação |
|--------|------|--------|------------|
| `id` | Long | PK gerada pelo banco | Somente leitura. |
| `razaoSocial` | String | Entidade | Retornado na resposta. |
| `nomeFantasia` | String | Entidade | Retornado na resposta. |
| `cnpj` | String | Entidade | Retornado na resposta. |
| `inscricaoEstadual` | String | Entidade | Retornado na resposta. |
| `telefone` | String | Entidade | Retornado na resposta. |
| `email` | String | Entidade | Retornado na resposta. |
| `contatoPrincipal` | String | Entidade | Retornado na resposta. |
| `enderecoId` | Long | FK → Address | Retornado na resposta. |
| `enderecoCep` | String | Address.cep | Retornado na resposta. |
| `enderecoLogradouro` | String | Address.logradouro | Retornado na resposta. |
| `enderecoNumero` | String | Address.numero | Retornado na resposta. |
| `enderecoComplemento` | String | Address.complemento | Retornado na resposta. |
| `enderecoBairro` | String | Address.bairro | Retornado na resposta. |
| `enderecoCidade` | String | Address.cidade | Retornado na resposta. |
| `enderecoEstado` | String | Address.estado | Retornado na resposta. |
| `enderecoPais` | String | Address.pais | Retornado na resposta. |
| `status` | String | Entidade | Retornado na resposta. |
| `criadoEm` | LocalDateTime | Entidade | Timestamp de criação. |
| `atualizadoEm` | LocalDateTime | Entidade | Timestamp de última atualização. |

---

## 3. Regras de validação

### Validações de campos (Bean Validation — DTOs)

- **`razaoSocial`** — obrigatório na criação (`@NotBlank`), máximo 150 caracteres (`@Size(max = 150)`). No update, é opcional.
- **`nomeFantasia`** — opcional, máximo 150 caracteres.
- **`cnpj`** — obrigatório na criação (`@NotBlank`), tamanho exatamente 14 caracteres (`@Size(min = 14, max = 14)`). No update, é opcional.
- **`inscricaoEstadual`** — opcional, máximo 30 caracteres.
- **`telefone`** — opcional, máximo 20 caracteres.
- **`email`** — opcional, máximo 150 caracteres, deve ser um e-mail válido (`@Email`).
- **`contatoPrincipal`** — opcional, máximo 150 caracteres.
- **`enderecoId`** — opcional. Se informado, deve referenciar um endereço existente (validado no Service).
- **`status`** — opcional, apenas no update.

### Validações customizadas (ClientValidator)

- **`validateUniqueCnpj(cnpj)`** — Garante que o CNPJ informado não existe em outro cliente. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueCnpjForUpdate(cnpj, id)`** — Na atualização, verifica se o CNPJ já existe em outro cliente. **Atenção:** o método atual não lança exceção; apenas executa a consulta. (Comportamento encontrado no código.)
- **`validateStatus(status)`** — Garante que o status é exatamente `ACTIVE` ou `INACTIVE`. Dispara `BusinessException` para qualquer outro valor.

### Validações de existência no Service

- **`enderecoId`** — Se informado na criação ou atualização, o `Address` referenciado deve existir. Caso contrário, retorna HTTP 404 (`ResourceNotFoundException`).

---

## 4. Unicidade

| Campo | Regra | Onde é garantida | Comportamento na duplicidade |
|--------|-------|------------------|------------------------------|
| `cnpj` | UNIQUE | Migration (`V5__create_table_client.sql`) + `ClientValidator.validateUniqueCnpj()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |

> **Observação:** `validateUniqueCnpjForUpdate()` existe no validator, mas atualmente não lança exceção (apenas consulta o repository). A proteção na atualização depende da constraint UNIQUE do banco ou de lógica adicional não implementada.

---

## 5. Status

### Valores possíveis

| Valor | Significado |
|-------|-------------|
| `ACTIVE` | Cliente ativo. |
| `INACTIVE` | Cliente inativo (soft delete). |

### Comportamento

- **Criação:** Se `status` não for informado, o valor padrão definido em `@PrePersist` é `ACTIVE`.
- **Atualização:** O campo `status` é opcional. Se informado, deve ser `ACTIVE` ou `INACTIVE`. Qualquer outro valor dispara `BusinessException` (HTTP 400).
- **DELETE:** Não é exclusão física. O DELETE marca o cliente como `INACTIVE` (soft delete).
- **Reativação:** Não existe endpoint de reativação (`activate`/`deactivate`) no Client.
- **Endpoint específico para status:** Não existe endpoint dedicado para alterar status. A alteração de status ocorre apenas via `PUT /api/v1/clients/{id}`.

---

## 6. Relacionamentos

### Address

| Aspecto | Comportamento |
|---------|---------------|
| **Obrigatoriedade** | Opcional. `endereco_id` permite `NULL` na migration. |
| **Tipo** | `@OneToOne(fetch = FetchType.LAZY)` |
| **FK** | `client.endereco_id` → `address.id` |
| **Criação** | `enderecoId` é opcional no `CreateClientRequest`. Se informado, o `Address` deve existir. |
| **Atualização** | `enderecoId` é opcional no `UpdateClientRequest`. Se informado, o novo `Address` deve existir. |

**Comportamento quando não há endereço:**
- O cliente pode ser cadastrado sem `enderecoId`.
- Na resposta, todos os campos de endereço retornam `null`.

---

## 7. Regras específicas do módulo

- O summary atualmente retorna `active` como `repository.count()` (todos os registros) e `inactive` como `0`, independentemente do status real. `withAddress` também retorna `0`. Isso indica que o summary pode não refletir a realidade dos dados.
- A atualização é parcial: campos não enviados são preservados.

---

## 8. Regras de criação

**Endpoint:** `POST /api/v1/clients`

Fluxo:

1. Recebe `CreateClientRequest` no body.
2. Validação Bean Validation é aplicada automaticamente (`@Valid`).
3. `ClientValidator` valida unicidade de `cnpj`.
4. Se `enderecoId` for informado, busca `Address`. Se não encontrado, retorna HTTP 404.
5. Cria entidade `Client` com os dados recebidos. O campo `status` é definido como `ACTIVE` por padrão via `@PrePersist`.
6. Persiste no banco.
7. Retorna HTTP 201 com `ClientResponse`.

**Campos obrigatórios na criação:**
- `razaoSocial`
- `cnpj`

**Possíveis conflitos na criação:**
- CNPJ duplicado → HTTP 409

---

## 9. Regras de atualização

**Endpoint:** `PUT /api/v1/clients/{id}`

Fluxo:

1. Recebe `UpdateClientRequest` no body.
2. Busca cliente por ID. Se não encontrado, retorna HTTP 404.
3. Atualização é **parcial**: apenas campos não nulos são atualizados.
4. Se `razaoSocial` for informado, atualiza.
5. Se `nomeFantasia` for informado, atualiza.
6. Se `cnpj` for informado e diferente do atual, valida unicidade (via `validateUniqueCnpjForUpdate` — atualmente sem efeito prático).
7. Se `inscricaoEstadual` for informada, atualiza.
8. Se `telefone` for informado, atualiza.
9. Se `email` for informado, atualiza.
10. Se `contatoPrincipal` for informado, atualiza.
11. Se `enderecoId` for informado, busca o `Address` e atualiza o vínculo. Se não encontrado, retorna HTTP 404.
12. Se `status` for informado, valida e atualiza.
13. Persiste alterações.
14. Retorna HTTP 200 com `ClientResponse`.

**Campos permitidos no update (todos opcionais):**
- `razaoSocial`
- `nomeFantasia`
- `cnpj`
- `inscricaoEstadual`
- `telefone`
- `email`
- `contatoPrincipal`
- `enderecoId`
- `status`

**Comportamento quando Client não existe:**
- Retorna HTTP 404 (`ResourceNotFoundException`).

---

## 10. Regras de exclusão

**Endpoint:** `DELETE /api/v1/clients/{id}`

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
| `POST` | `/api/v1/clients` | Criar cliente | CNPJ único. Campos obrigatórios: razão social, CNPJ. Status default `ACTIVE`. |
| `GET` | `/api/v1/clients` | Listar clientes (paginado) | Filtros: `search`, `status`, `enderecoId`. Paginação padrão: 20 por página, ordenado por `id ASC`. |
| `GET` | `/api/v1/clients/summary` | Resumo de clientes | Retorna totais: `total`, `active`, `inactive`, `withAddress`, `withoutAddress`. |
| `GET` | `/api/v1/clients/{id}` | Buscar cliente por ID | Retorna dados completos com endereço (se existir). |
| `PUT` | `/api/v1/clients/{id}` | Atualizar cliente | Atualização parcial. CNPJ validado para unicidade. Status validado (`ACTIVE`/`INACTIVE`). |
| `DELETE` | `/api/v1/clients/{id}` | Remover cliente | Soft delete: define `status = INACTIVE`. |

---

## 12. Filtros e paginação

### Filtros disponíveis em `GET /api/v1/clients`

| Parâmetro | Tipo | Descrição | Comportamento |
|-----------|------|-----------|---------------|
| `search` | String | Busca geral | Pesquisa em: `razaoSocial`, `nomeFantasia`, `cnpj`, `email`, `telefone`, `contatoPrincipal` (case-insensitive, parcial). |
| `status` | String | Filtro por status | Aceita `ACTIVE`, `INACTIVE` ou `ALL`. Se `ALL` ou vazio, não filtra. |
| `enderecoId` | Long | Filtro por endereço | Busca exata pelo ID do endereço. |

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

- `razaoSocial` é obrigatório na criação.
- `cnpj` é obrigatório na criação, deve ter exatamente 14 caracteres e é único.
- `nomeFantasia`, `inscricaoEstadual`, `telefone`, `email`, `contatoPrincipal` são opcionais. Se `email` for informado, deve ser um e-mail válido.
- `enderecoId` é opcional. Se informado, deve referenciar um endereço existente.
- Atualização é parcial: enviar apenas campos que devem ser alterados.
- Na atualização, se `cnpj` for alterado, a unicidade é validada.
- `status` pode ser alterado no update, mas apenas para `ACTIVE` ou `INACTIVE`.
- DELETE não remove o registro: marca o cliente como `INACTIVE`.
- Não existe endpoint de reativação.
- Listagem suporta filtros por status, endereço e busca geral.
- Paginação padrão é de 20 registros por página, ordenados por ID crescente.
- Resumo (`/summary`) retorna contadores de total, ativos, inativos, com endereço e sem endereço.

---

## 14. Fontes analisadas

- `src/main/java/br/com/logicore/modules/client/entity/Client.java`
- `src/main/java/br/com/logicore/modules/client/enums/ClientStatus.java`
- `src/main/java/br/com/logicore/modules/client/dto/CreateClientRequest.java`
- `src/main/java/br/com/logicore/modules/client/dto/UpdateClientRequest.java`
- `src/main/java/br/com/logicore/modules/client/dto/ClientResponse.java`
- `src/main/java/br/com/logicore/modules/client/dto/ClientSummaryResponse.java`
- `src/main/java/br/com/logicore/modules/client/service/ClientService.java`
- `src/main/java/br/com/logicore/modules/client/controller/ClientController.java`
- `src/main/java/br/com/logicore/modules/client/validator/ClientValidator.java`
- `src/main/java/br/com/logicore/modules/client/repository/ClientRepository.java`
- `src/main/java/br/com/logicore/modules/client/repository/spec/ClientSpecifications.java`
- `src/main/java/br/com/logicore/modules/client/mapper/ClientMapper.java`
- `src/main/resources/db/migration/V5__create_table_client.sql`
