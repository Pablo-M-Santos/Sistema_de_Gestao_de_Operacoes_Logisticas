# Plano: Módulo Vehicle

## 1. Estado atual
- Backend Java 17 + Spring Boot 4.1.0 + Maven + Flyway.
- Módulos existentes: Department, Cargo, Address, Employee, Client.
- Migrations V1..V5 aplicadas.
- Padrões consolidados: CRUD, paginação, Specifications, status como String ou Boolean, soft delete via status, update parcial em módulos mais recentes.

## 2. Problemas / Decisões

### 2.1 Status
- **Decisão**: `String status` com valores `ACTIVE` / `INACTIVE`.
- **Justificativa**: coerente com Employee e Client (módulos mais recentes); evita criar nova convenção.
- **Default**: `ACTIVE` via `@PrePersist`.

### 2.2 Unicidade
- `placa`: única, obrigatória.
- `renavam`: única, obrigatória.
- Ambas com `length` suficiente para o formato brasileiro.

### 2.3 Tipos e nulabilidade
- `placa`: `String`, `length = 10`, `NOT NULL`, `UNIQUE`.
- `renavam`: `String`, `length = 11`, `NOT NULL`, `UNIQUE`.
- `modelo`: `String`, `length = 100`, `NOT NULL`.
- `fabricante`: `String`, `length = 100`, `NOT NULL`.
- `anoFabricacao`: `Integer`, `NOT NULL`.
- `anoModelo`: `Integer`, `NOT NULL`.
- `capacidadePeso`: `BigDecimal`, `precision = 10, scale = 2`, `NOT NULL`.
- `capacidadeVolume`: `BigDecimal`, `precision = 10, scale = 3`, `NOT NULL`.
- `quilometragem`: `Integer`, `NOT NULL`, `default = 0`.

### 2.4 Relacionamentos
- Nenhum relacionamento com módulos existentes no modelo atual.
- Entidade standalone.

### 2.5 Estratégia de UPDATE
- **Parcial** (igual Cargo, Address, Employee, Client).
- Campos `null` não sobrescrevem.
- Campos `blank` em strings são ignorados.

### 2.6 Estratégia de DELETE
- **Soft delete**: `DELETE /{id}` → `status = "INACTIVE"`.

### 2.7 Summary
- Incluir: `total`, `active`, `inactive`.

## 3. Arquitetura

```
src/main/java/br/com/logicore/modules/vehicle/
├── controller/
│   └── VehicleController.java
├── dto/
│   ├── CreateVehicleRequest.java
│   ├── UpdateVehicleRequest.java
│   ├── VehicleResponse.java
│   └── VehicleSummaryResponse.java
├── entity/
│   └── Vehicle.java
├── enums/
│   └── VehicleStatus.java
├── mapper/
│   └── VehicleMapper.java
├── repository/
│   ├── VehicleRepository.java
│   └── spec/
│       └── VehicleSpecifications.java
├── service/
│   └── VehicleService.java
└── validator/
    └── VehicleValidator.java

src/test/java/br/com/logicore/modules/vehicle/
├── controller/
│   └── VehicleControllerTest.java
├── mapper/
│   └── VehicleMapperTest.java
├── repository/spec/
│   └── VehicleSpecificationsIntegrationTest.java
├── service/
│   └── VehicleServiceTest.java
└── validator/
│   └── VehicleValidatorTest.java
```

## 4. Endpoints

Base path: `/api/v1/vehicles`

| Método | Path | Descrição |
|--------|------|-----------|
| `POST` | `/` | Criar veículo (201) |
| `GET` | `/` | Listar com paginação, search e filtros (200) |
| `GET` | `/summary` | Estatísticas (200) |
| `GET` | `/{id}` | Buscar por ID (200/404) |
| `PUT` | `/{id}` | Atualizar parcialmente (200/404/409) |
| `DELETE` | `/{id}` | Soft delete (204/404) |

### Filtros (query params)
- `search` (placa, renavam, modelo, fabricante)
- `status` (ACTIVE, INACTIVE, ALL)
- `anoFabricacao`
- `anoModelo`
- Paginação padrão: `page=0`, `size=20`, `sort=id`, `direction=ASC`

## 5. Migration

Arquivo: `src/main/resources/db/migration/V6__create_table_vehicle.sql`

```sql
CREATE TABLE vehicle
(
    id                  BIGSERIAL PRIMARY KEY,
    placa               VARCHAR(10)   NOT NULL UNIQUE,
    renavam             VARCHAR(11)   NOT NULL UNIQUE,
    modelo              VARCHAR(100)  NOT NULL,
    fabricante          VARCHAR(100)  NOT NULL,
    ano_fabricacao      INTEGER       NOT NULL,
    ano_modelo          INTEGER       NOT NULL,
    capacidade_peso     NUMERIC(10,2) NOT NULL,
    capacidade_volume   NUMERIC(10,3) NOT NULL,
    quilometragem       INTEGER       NOT NULL DEFAULT 0,
    status              VARCHAR(30)   NOT NULL,
    criado_em           TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em       TIMESTAMP     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_vehicle_status CHECK (status IN ('ACTIVE', 'INACTIVE'))
);


CREATE INDEX idx_vehicle_placa
    ON vehicle (placa);

CREATE INDEX idx_vehicle_renavam
    ON vehicle (renavam);

CREATE INDEX idx_vehicle_status
    ON vehicle (status);
```

## 6. Decisões que precisam de aprovação

1. **Status**: `String ACTIVE/INACTIVE` (recomendado) ou `Boolean ativo` (como Cargo)?
2. **Quilometragem**: permitir atualização parcial? (recomendado: sim, pois é um campo que evolve com o uso).
3. **Ano fabricação/modelo**: validar ranges (ex: ano_fabricacao >= 1900, ano_modelo >= ano_fabricacao)? (recomendado: não inventar regras além de NOT NULL).
4. **Capacidades**: alguma validação de negócio específica além de NOT NULL? (recomendado: não, seguir schema estrito).
5. **Endpoint `/summary`**: incluir apenas total/active/inactive, ou também métricas de capacidade? (recomendado: apenas total/active/inactive, igual módulos simples).

## 7. Testes

Padrão igual módulos existentes.

- **VehicleControllerTest** (@WebMvcTest): create, findAll, findById, update, delete, summary.
- **VehicleServiceTest** (@ExtendWith MockitoExtension): create, findById, update parcial, delete soft, summary, findAll paginated, placa duplicada, renavam duplicado, status inválido, not found.
- **VehicleMapperTest**: instância real, sem mocks.
- **VehicleValidatorTest**: placa duplicada create/update, renavam duplicado create/update, status inválido.
- **VehicleSpecificationsIntegrationTest** (@DataJpaTest + H2): search, status, anoFabricacao, anoModelo.

## 8. Arquivos a criar

- `src/main/java/br/com/logicore/modules/vehicle/entity/Vehicle.java`
- `src/main/java/br/com/logicore/modules/vehicle/enums/VehicleStatus.java`
- `src/main/java/br/com/logicore/modules/vehicle/dto/CreateVehicleRequest.java`
- `src/main/java/br/com/logicore/modules/vehicle/dto/UpdateVehicleRequest.java`
- `src/main/java/br/com/logicore/modules/vehicle/dto/VehicleResponse.java`
- `src/main/java/br/com/logicore/modules/vehicle/dto/VehicleSummaryResponse.java`
- `src/main/java/br/com/logicore/modules/vehicle/mapper/VehicleMapper.java`
- `src/main/java/br/com/logicore/modules/vehicle/repository/VehicleRepository.java`
- `src/main/java/br/com/logicore/modules/vehicle/repository/spec/VehicleSpecifications.java`
- `src/main/java/br/com/logicore/modules/vehicle/service/VehicleService.java`
- `src/main/java/br/com/logicore/modules/vehicle/validator/VehicleValidator.java`
- `src/main/java/br/com/logicore/modules/vehicle/controller/VehicleController.java`
- `src/test/java/br/com/logicore/modules/vehicle/controller/VehicleControllerTest.java`
- `src/test/java/br/com/logicore/modules/vehicle/service/VehicleServiceTest.java`
- `src/test/java/br/com/logicore/modules/vehicle/mapper/VehicleMapperTest.java`
- `src/test/java/br/com/logicore/modules/vehicle/validator/VehicleValidatorTest.java`
- `src/test/java/br/com/logicore/modules/vehicle/repository/spec/VehicleSpecificationsIntegrationTest.java`
- `src/main/resources/db/migration/V6__create_table_vehicle.sql`

Nenhum arquivo existente será alterado.
