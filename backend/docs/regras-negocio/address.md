# Regras de Negócio — Address

## 1. Identificação

| Item | Valor |
|------|-------|
| **Módulo** | Address |
| **Entidade** | Address |
| **Tabela** | `address` |
| **Endpoint base** | `/api/v1/addresses` |
| **Objetivo** | Gerenciar cadastro de endereços, com dados de CEP, logradouro, número, complemento, bairro, cidade, estado, país, latitude e longitude. |

---

## 2. Campos

### Campos de entrada (Create / Update)

| Campo | Tipo | Obrigatório | Tamanho/Formato | Regra | Observação |
|--------|------|-------------|-----------------|-------|------------|
| `cep` | String | Sim (criação) | Exatamente 8 caracteres | — | No update, é opcional (parcial). |
| `logradouro` | String | Sim (criação) | Máx. 200 caracteres | — | No update, é opcional (parcial). |
| `numero` | String | Sim (criação) | Máx. 20 caracteres | — | No update, é opcional (parcial). |
| `complemento` | String | Não | Máx. 200 caracteres | — | Opcional em criação e atualização. |
| `bairro` | String | Sim (criação) | Máx. 150 caracteres | — | No update, é opcional (parcial). |
| `cidade` | String | Sim (criação) | Máx. 150 caracteres | — | No update, é opcional (parcial). |
| `estado` | String | Sim (criação) | Exatamente 2 caracteres | Validado por `AddressValidator.validateState()`. | No update, é opcional (parcial). |
| `pais` | String | Não | Máx. 100 caracteres | Default: `Brasil` (definido no `AddressMapper`). | No update, é opcional (parcial). |
| `latitude` | BigDecimal | Não | Precisão 10, escala 8. Intervalo: -90 a 90. | Validado por `AddressValidator.validateLatitude()`. | Opcional em criação e atualização. |
| `longitude` | BigDecimal | Não | Precisão 11, escala 8. Intervalo: -180 a 180. | Validado por `AddressValidator.validateLongitude()`. | Opcional em criação e atualização. |

### Campos somente de resposta (AddressResponse)

| Campo | Tipo | Origem | Observação |
|--------|------|--------|------------|
| `id` | Long | PK gerada pelo banco | Somente leitura. |
| `cep` | String | Entidade | Retornado na resposta. |
| `logradouro` | String | Entidade | Retornado na resposta. |
| `numero` | String | Entidade | Retornado na resposta. |
| `complemento` | String | Entidade | Retornado na resposta. |
| `bairro` | String | Entidade | Retornado na resposta. |
| `cidade` | String | Entidade | Retornado na resposta. |
| `estado` | String | Entidade | Retornado na resposta. |
| `pais` | String | Entidade | Retornado na resposta. |
| `latitude` | BigDecimal | Entidade | Retornado na resposta. |
| `longitude` | BigDecimal | Entidade | Retornado na resposta. |
| `criadoEm` | LocalDateTime | Entidade | Timestamp de criação. |
| `atualizadoEm` | LocalDateTime | Entidade | Timestamp de última atualização. |

---

## 3. Regras de validação

### Validações de campos (Bean Validation — DTOs)

- **`cep`** — obrigatório na criação (`@NotBlank`), exatamente 8 caracteres (`@Size(min = 8, max = 8)`). No update, é opcional.
- **`logradouro`** — obrigatório na criação (`@NotBlank`), máximo 200 caracteres (`@Size(max = 200)`). No update, é opcional.
- **`numero`** — obrigatório na criação (`@NotBlank`), máximo 20 caracteres (`@Size(max = 20)`). No update, é opcional.
- **`complemento`** — opcional, máximo 200 caracteres.
- **`bairro`** — obrigatório na criação (`@NotBlank`), máximo 150 caracteres (`@Size(max = 150)`). No update, é opcional.
- **`cidade`** — obrigatório na criação (`@NotBlank`), máximo 150 caracteres (`@Size(max = 150)`). No update, é opcional.
- **`estado`** — obrigatório na criação (`@NotBlank`), exatamente 2 caracteres (`@Size(min = 2, max = 2)`). No update, é opcional.
- **`pais`** — opcional, máximo 100 caracteres.
- **`latitude`** — opcional. Se informado, deve estar entre -90 e 90.
- **`longitude`** — opcional. Se informado, deve estar entre -180 e 180.

### Validações customizadas (AddressValidator)

- **`validateState(estado)`** — Garante que o estado tem exatamente 2 caracteres. Dispara `BusinessException` se inválido.
- **`validateLatitude(latitude)`** — Garante que a latitude está entre -90 e 90. Dispara `BusinessException` se inválida.
- **`validateLongitude(longitude)`** — Garante que a longitude está entre -180 e 180. Dispara `BusinessException` se inválida.

### Validações no Service

- No update, se `estado` for informado, `validateState()` é chamado.
- No update, se `latitude` for informada, `validateLatitude()` é chamado.
- No update, se `longitude` for informada, `validateLongitude()` é chamado.

---

## 4. Unicidade

O módulo Address não possui regras de unicidade além da chave primária `id`.

Não há constraint UNIQUE para `cep` ou outros campos.

---

## 5. Status

O módulo Address não possui campo de status.

---

## 6. Relacionamentos

O módulo Address é referenciado por outras entidades, mas não possui relacionamentos de propriedade no backend:

| Entidade referenciadora | Tipo de relacionamento | Campo FK | Obrigatoriedade |
|-------------------------|------------------------|----------|-----------------|
| `Employee` | `@OneToOne` | `endereco_id` | Opcional (`NULL` permitido). |
| `Client` | `@OneToOne` | `endereco_id` | Opcional (`NULL` permitido). |

> **Observação:** A regra de exclusão de endereço quando referenciado por Employee ou Client é tratada no módulo Address via `DataIntegrityViolationException` no Service.

---

## 7. Regras específicas do módulo

- O campo `pais` tem default `Brasil` definido no `AddressMapper.toEntity()` quando não informado ou em branco.
- A exclusão física de um endereço que está referenciado por Employee ou Client resulta em erro `BusinessException` com mensagem: "Address cannot be deleted because it is referenced by other records." (HTTP 400).
- O summary calcula `withCoordinates` (latitude e longitude não nulos) e `withoutCoordinates` (total menos com coordenadas).

---

## 8. Regras de criação

**Endpoint:** `POST /api/v1/addresses`

Fluxo:

1. Recebe `CreateAddressRequest` no body.
2. Validação Bean Validation é aplicada automaticamente (`@Valid`).
3. `AddressValidator` valida `estado`, `latitude` e `longitude` (se informados).
4. Cria entidade `Address`. Se `pais` não for informado ou estiver em branco, define como `Brasil`.
5. Persiste no banco.
6. Retorna HTTP 201 com `AddressResponse`.

**Campos obrigatórios na criação:**
- `cep`
- `logradouro`
- `numero`
- `bairro`
- `cidade`
- `estado`

**Possíveis conflitos na criação:**
- Nenhum conflito de unicidade específico.

---

## 9. Regras de atualização

**Endpoint:** `PUT /api/v1/addresses/{id}`

Fluxo:

1. Recebe `UpdateAddressRequest` no body.
2. Busca endereço por ID. Se não encontrado, retorna HTTP 404.
3. Atualização é **parcial**: apenas campos não nulos são atualizados.
4. Se `cep` for informado, atualiza.
5. Se `logradouro` for informado, atualiza.
6. Se `numero` for informado, atualiza.
7. Se `complemento` for informado (mesmo que string vazia), atualiza.
8. Se `bairro` for informado, atualiza.
9. Se `cidade` for informado, atualiza.
10. Se `estado` for informado, valida e atualiza.
11. Se `pais` for informado, atualiza.
12. Se `latitude` for informada, valida e atualiza.
13. Se `longitude` for informada, valida e atualiza.
14. Persiste alterações.
15. Retorna HTTP 200 com `AddressResponse`.

**Comportamento quando Address não existe:**
- Retorna HTTP 404 (`ResourceNotFoundException`).

---

## 10. Regras de exclusão

**Endpoint:** `DELETE /api/v1/addresses/{id}`

| Aspecto | Comportamento |
|---------|---------------|
| **Tipo** | Exclusão física. |
| **Ação** | Remove o registro do banco de dados. |
| **Resposta HTTP** | `204 No Content` |
| **Dependências** | Se o endereço estiver referenciado por Employee ou Client, retorna HTTP 400 (`BusinessException`) com mensagem explicativa. |
| **Registro não existe** | Retorna HTTP 404 (`ResourceNotFoundException`). |

---

## 11. Endpoints

| Método | Endpoint | Finalidade | Regras relevantes |
|--------|----------|------------|-------------------|
| `POST` | `/api/v1/addresses` | Criar endereço | Campos obrigatórios: CEP (8 chars), logradouro, número, bairro, cidade, estado (2 chars). País default `Brasil`. |
| `GET` | `/api/v1/addresses` | Listar endereços (paginado) | Filtros: `search`, `cep`, `cidade`, `estado`, `pais`. Paginação padrão: 20 por página, ordenado por `id ASC`. |
| `GET` | `/api/v1/addresses/summary` | Resumo de endereços | Retorna totais: `total`, `withCoordinates`, `withoutCoordinates`. |
| `GET` | `/api/v1/addresses/{id}` | Buscar endereço por ID | Retorna dados completos. |
| `PUT` | `/api/v1/addresses/{id}` | Atualizar endereço | Atualização parcial. Estado validado (2 chars). Latitude/longitude validados se informados. |
| `DELETE` | `/api/v1/addresses/{id}` | Remover endereço | Exclusão física. Falha se referenciado por Employee ou Client. |

---

## 12. Filtros e paginação

### Filtros disponíveis em `GET /api/v1/addresses`

| Parâmetro | Tipo | Descrição | Comportamento |
|-----------|------|-----------|---------------|
| `search` | String | Busca geral | Pesquisa em: `cep`, `logradouro`, `numero`, `complemento`, `bairro`, `cidade`, `estado`, `pais` (case-insensitive, parcial). |
| `cep` | String | Filtro por CEP | Busca exata. |
| `cidade` | String | Filtro por cidade | Busca exata case-insensitive. |
| `estado` | String | Filtro por estado | Busca exata case-insensitive. |
| `pais` | String | Filtro por país | Busca exata case-insensitive. |

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

- `cep` é obrigatório na criação e deve ter exatamente 8 caracteres.
- `logradouro` é obrigatório na criação.
- `numero` é obrigatório na criação.
- `bairro` é obrigatório na criação.
- `cidade` é obrigatório na criação.
- `estado` é obrigatório na criação e deve ter exatamente 2 caracteres.
- `complemento` é opcional.
- `pais` é opcional. Se não informado, o backend define `Brasil`.
- `latitude` é opcional. Se informada, deve estar entre -90 e 90.
- `longitude` é opcional. Se informada, deve estar entre -180 e 180.
- Atualização é parcial: enviar apenas campos que devem ser alterados.
- DELETE é físico. O frontend deve garantir que o endereço não está em uso antes de remover, pois o backend retorna erro 400 se houver dependência.
- Listagem suporta busca geral e filtros por CEP, cidade, estado e país.
- Paginação padrão é de 20 registros por página, ordenados por ID crescente.
- Resumo (`/summary`) retorna contadores de total, com coordenadas e sem coordenadas.

---

## 14. Fontes analisadas

- `src/main/java/br/com/logicore/modules/address/entity/Address.java`
- `src/main/java/br/com/logicore/modules/address/dto/CreateAddressRequest.java`
- `src/main/java/br/com/logicore/modules/address/dto/UpdateAddressRequest.java`
- `src/main/java/br/com/logicore/modules/address/dto/AddressResponse.java`
- `src/main/java/br/com/logicore/modules/address/dto/AddressSummaryResponse.java`
- `src/main/java/br/com/logicore/modules/address/service/AddressService.java`
- `src/main/java/br/com/logicore/modules/address/controller/AddressController.java`
- `src/main/java/br/com/logicore/modules/address/validator/AddressValidator.java`
- `src/main/java/br/com/logicore/modules/address/repository/AddressRepository.java`
- `src/main/java/br/com/logicore/modules/address/repository/spec/AddressSpecifications.java`
- `src/main/java/br/com/logicore/modules/address/mapper/AddressMapper.java`
- `src/main/resources/db/migration/V3__create_table_endereco.sql`
