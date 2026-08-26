# Regras de Negócio — Vehicle

## 1. Identificação

| Item | Valor |
|------|-------|
| **Módulo** | Vehicle |
| **Entidade** | Vehicle |
| **Tabela** | `vehicle` |
| **Endpoint base** | `/api/v1/vehicles` |
| **Objetivo** | Gerenciar cadastro de veículos da frota, com dados de placa, RENAVAM, modelo, fabricante, capacidades e status. |

---

## 2. Campos

### Campos de entrada (Create / Update)

| Campo | Tipo | Obrigatório | Tamanho/Formato | Regra | Observação |
|--------|------|-------------|-----------------|-------|------------|
| `placa` | String | Sim (criação) | Máx. 10 caracteres | Única no sistema. Validado por `VehicleValidator`. | No update, é opcional (parcial). |
| `renavam` | String | Sim (criação) | Máx. 11 caracteres | Único no sistema. Validado por `VehicleValidator`. | No update, é opcional (parcial). |
| `modelo` | String | Sim (criação) | Máx. 100 caracteres | — | No update, é opcional (parcial). |
| `fabricante` | String | Sim (criação) | Máx. 100 caracteres | — | No update, é opcional (parcial). |
| `anoFabricacao` | Integer | Sim (criação) | — | — | No update, é opcional (parcial). |
| `anoModelo` | Integer | Sim (criação) | — | — | No update, é opcional (parcial). |
| `capacidadePeso` | BigDecimal | Sim (criação) | Precisão 10, escala 2 | — | No update, é opcional (parcial). |
| `capacidadeVolume` | BigDecimal | Sim (criação) | Precisão 10, escala 3 | — | No update, é opcional (parcial). |
| `quilometragem` | Integer | Não | — | Default: 0 (definido na migration). | No update, é opcional (parcial). |
| `status` | String | Não | — | Valores permitidos: `ACTIVE` ou `INACTIVE`. Validado por `VehicleValidator.validateStatus()`. | Apenas no update. |

### Campos somente de resposta (VehicleResponse)

| Campo | Tipo | Origem | Observação |
|--------|------|--------|------------|
| `id` | Long | PK gerada pelo banco | Somente leitura. |
| `placa` | String | Entidade | Retornado na resposta. |
| `renavam` | String | Entidade | Retornado na resposta. |
| `modelo` | String | Entidade | Retornado na resposta. |
| `fabricante` | String | Entidade | Retornado na resposta. |
| `anoFabricacao` | Integer | Entidade | Retornado na resposta. |
| `anoModelo` | Integer | Entidade | Retornado na resposta. |
| `capacidadePeso` | BigDecimal | Entidade | Retornado na resposta. |
| `capacidadeVolume` | BigDecimal | Entidade | Retornado na resposta. |
| `quilometragem` | Integer | Entidade | Retornado na resposta. |
| `status` | String | Entidade | Retornado na resposta. |
| `criadoEm` | LocalDateTime | Entidade | Timestamp de criação. |
| `atualizadoEm` | LocalDateTime | Entidade | Timestamp de última atualização. |

---

## 3. Regras de validação

### Validações de campos (Bean Validation — DTOs)

- **`placa`** — obrigatório na criação (`@NotBlank`), máximo 10 caracteres (`@Size(max = 10)`). No update, é opcional.
- **`renavam`** — obrigatório na criação (`@NotBlank`), máximo 11 caracteres (`@Size(max = 11)`). No update, é opcional.
- **`modelo`** — obrigatório na criação (`@NotBlank`), máximo 100 caracteres (`@Size(max = 100)`). No update, é opcional.
- **`fabricante`** — obrigatório na criação (`@NotBlank`), máximo 100 caracteres (`@Size(max = 100)`). No update, é opcional.
- **`anoFabricacao`** — obrigatório na criação (`@NotNull`). No update, é opcional.
- **`anoModelo`** — obrigatório na criação (`@NotNull`). No update, é opcional.
- **`capacidadePeso`** — obrigatório na criação (`@NotNull`). No update, é opcional.
- **`capacidadeVolume`** — obrigatório na criação (`@NotNull`). No update, é opcional.
- **`quilometragem`** — opcional. Default na migration: 0.

### Validações customizadas (VehicleValidator)

- **`validateUniquePlaca(placa)`** — Garante que a placa informada não existe em outro veículo. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniquePlacaForUpdate(placa, id)`** — Na atualização, garante que a placa informada não pertence a outro veículo diferente do atual. Dispara `DuplicateResourceException` se conflitar.
- **`validateUniqueRenavam(renavam)`** — Garante que o RENAVAM informado não existe em outro veículo. Dispara `DuplicateResourceException` se já existir. (Usado na criação.)
- **`validateUniqueRenavamForUpdate(renavam, id)`** — Na atualização, garante que o RENAVAM informado não pertence a outro veículo diferente do atual. Dispara `DuplicateResourceException` se conflitar.
- **`validateStatus(status)`** — Garante que o status é exatamente `ACTIVE` ou `INACTIVE`. Dispara `BusinessException` para qualquer outro valor.

---

## 4. Unicidade

| Campo | Regra | Onde é garantida | Comportamento na duplicidade |
|--------|-------|------------------|------------------------------|
| `placa` | UNIQUE | Migration (`V6__create_table_vehicle.sql`) + `VehicleValidator.validateUniquePlaca()` + `VehicleValidator.validateUniquePlacaForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |
| `renavam` | UNIQUE | Migration (`V6__create_table_vehicle.sql`) + `VehicleValidator.validateUniqueRenavam()` + `VehicleValidator.validateUniqueRenavamForUpdate()` | HTTP 409 (`DuplicateResourceException`) na aplicação; erro de constraint no banco como fallback. |

---

## 5. Status

### Valores possíveis

| Valor | Significado |
|-------|-------------|
| `ACTIVE` | Veículo ativo. |
| `INACTIVE` | Veículo inativo (soft delete). |

### Comportamento

- **Criação:** O valor padrão definido em `@PrePersist` é `ACTIVE`.
- **Atualização:** O campo `status` é opcional. Se informado, deve ser `ACTIVE` ou `INACTIVE`. Qualquer outro valor dispara `BusinessException` (HTTP 400).
- **DELETE:** Não é exclusão física. O DELETE marca o veículo como `INACTIVE` (soft delete).
- **Reativação:** Não existe endpoint de reativação (`activate`/`deactivate`) no Vehicle.
- **Endpoint específico para status:** Não existe endpoint dedicado para alterar status. A alteração de status ocorre apenas via `PUT /api/v1/vehicles/{id}`.

---

## 6. Relacionamentos

O módulo Vehicle não possui relacionamentos com outras entidades no backend.

---

## 7. Regras específicas do módulo

- O campo `quilometragem` tem default `0` na migration.
- O summary atualmente retorna `active` como `repository.count()` (todos os registros) e `inactive` como `0`, independentemente do status real. Isso indica que o summary pode não refletir a realidade dos dados.
- A atualização é parcial: campos não enviados são preservados.

---

## 8. Regras de criação

**Endpoint:** `POST /api/v1/vehicles`

Fluxo:

1. Recebe `CreateVehicleRequest` no body.
2. Validação Bean Validation é aplicada automaticamente (`@Valid`).
3. `VehicleValidator` valida unicidade de `placa` e `renavam`.
4. Cria entidade `Vehicle` com os dados recebidos. O campo `status` é definido como `ACTIVE` por padrão via `@PrePersist`.
5. Persiste no banco.
6. Retorna HTTP 201 com `VehicleResponse`.

**Campos obrigatórios na criação:**
- `placa`
- `renavam`
- `modelo`
- `fabricante`
- `anoFabricacao`
- `anoModelo`
- `capacidadePeso`
- `capacidadeVolume`

**Possíveis conflitos na criação:**
- Placa duplicada → HTTP 409
- RENAVAM duplicado → HTTP 409

---

## 9. Regras de atualização

**Endpoint:** `PUT /api/v1/vehicles/{id}`

Fluxo:

1. Recebe `UpdateVehicleRequest` no body.
2. Busca veículo por ID. Se não encontrado, retorna HTTP 404.
3. Atualização é **parcial**: apenas campos não nulos são atualizados.
4. Se `placa` for informada e diferente da atual, valida unicidade.
5. Se `renavam` for informado e diferente do atual, valida unicidade.
6. Se `modelo` for informado, atualiza.
7. Se `fabricante` for informado, atualiza.
8. Se `anoFabricacao` for informado, atualiza.
9. Se `anoModelo` for informado, atualiza.
10. Se `capacidadePeso` for informada, atualiza.
11. Se `capacidadeVolume` for informada, atualiza.
12. Se `quilometragem` for informada, atualiza.
13. Se `status` for informado, valida e atualiza.
14. Persiste alterações.
15. Retorna HTTP 200 com `VehicleResponse`.

**Campos permitidos no update (todos opcionais):**
- `placa`
- `renavam`
- `modelo`
- `fabricante`
- `anoFabricacao`
- `anoModelo`
- `capacidadePeso`
- `capacidadeVolume`
- `quilometragem`
- `status`

**Comportamento quando Vehicle não existe:**
- Retorna HTTP 404 (`ResourceNotFoundException`).

---

## 10. Regras de exclusão

**Endpoint:** `DELETE /api/v1/vehicles/{id}`

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
| `POST` | `/api/v1/vehicles` | Criar veículo | Placa e RENAVAM únicos. Campos obrigatórios: placa, renavam, modelo, fabricante, ano fabricação, ano modelo, capacidade peso, capacidade volume. Status default `ACTIVE`. |
| `GET` | `/api/v1/vehicles` | Listar veículos (paginado) | Filtros: `search`, `status`, `anoFabricacao`, `anoModelo`. Paginação padrão: 20 por página, ordenado por `id ASC`. |
| `GET` | `/api/v1/vehicles/summary` | Resumo de veículos | Retorna totais: `total`, `active`, `inactive`. |
| `GET` | `/api/v1/vehicles/{id}` | Buscar veículo por ID | Retorna dados completos. |
| `PUT` | `/api/v1/vehicles/{id}` | Atualizar veículo | Atualização parcial. Placa e RENAVAM validados para unicidade. Status validado (`ACTIVE`/`INACTIVE`). |
| `DELETE` | `/api/v1/vehicles/{id}` | Remover veículo | Soft delete: define `status = INACTIVE`. |

---

## 12. Filtros e paginação

### Filtros disponíveis em `GET /api/v1/vehicles`

| Parâmetro | Tipo | Descrição | Comportamento |
|-----------|------|-----------|---------------|
| `search` | String | Busca geral | Pesquisa em: `placa`, `renavam`, `modelo`, `fabricante` (case-insensitive, parcial). |
| `status` | String | Filtro por status | Aceita `ACTIVE`, `INACTIVE` ou `ALL`. Se `ALL` ou vazio, não filtra. |
| `anoFabricacao` | Integer | Filtro por ano de fabricação | Busca exata. |
| `anoModelo` | Integer | Filtro por ano de modelo | Busca exata. |

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

- `placa` é obrigatório na criação e único.
- `renavam` é obrigatório na criação e único.
- `modelo` é obrigatório na criação.
- `fabricante` é obrigatório na criação.
- `anoFabricacao` é obrigatório na criação.
- `anoModelo` é obrigatório na criação.
- `capacidadePeso` é obrigatório na criação.
- `capacidadeVolume` é obrigatório na criação.
- `quilometragem` é opcional. Se não informado, o default é 0.
- Atualização é parcial: enviar apenas campos que devem ser alterados.
- Na atualização, se `placa` ou `renavam` forem alterados, a unicidade é validada contra outros veículos.
- `status` pode ser alterado no update, mas apenas para `ACTIVE` ou `INACTIVE`.
- DELETE não remove o registro: marca o veículo como `INACTIVE`.
- Não existe endpoint de reativação.
- Listagem suporta filtros por status, ano de fabricação, ano de modelo e busca geral.
- Paginação padrão é de 20 registros por página, ordenados por ID crescente.
- Resumo (`/summary`) retorna contadores de total, ativos e inativos.

---

## 14. Fontes analisadas

- `src/main/java/br/com/logicore/modules/vehicle/entity/Vehicle.java`
- `src/main/java/br/com/logicore/modules/vehicle/enums/VehicleStatus.java`
- `src/main/java/br/com/logicore/modules/vehicle/dto/CreateVehicleRequest.java`
- `src/main/java/br/com/logicore/modules/vehicle/dto/UpdateVehicleRequest.java`
- `src/main/java/br/com/logicore/modules/vehicle/dto/VehicleResponse.java`
- `src/main/java/br/com/logicore/modules/vehicle/dto/VehicleSummaryResponse.java`
- `src/main/java/br/com/logicore/modules/vehicle/service/VehicleService.java`
- `src/main/java/br/com/logicore/modules/vehicle/controller/VehicleController.java`
- `src/main/java/br/com/logicore/modules/vehicle/validator/VehicleValidator.java`
- `src/main/java/br/com/logicore/modules/vehicle/repository/VehicleRepository.java`
- `src/main/java/br/com/logicore/modules/vehicle/repository/spec/VehicleSpecifications.java`
- `src/main/java/br/com/logicore/modules/vehicle/mapper/VehicleMapper.java`
- `src/main/resources/db/migration/V6__create_table_vehicle.sql`
