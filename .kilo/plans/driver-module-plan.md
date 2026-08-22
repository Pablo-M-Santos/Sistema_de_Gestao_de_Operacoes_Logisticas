# Plano: Módulo Motorista

## 1. Estado atual
- Backend Java 17 + Spring Boot 4.1.0 + Maven + Flyway.
- Migrations V1..V6 aplicadas.
- Employee exists com status (`String ACTIVE/INACTIVE`) e relacionamentos com Cargo, Department, Address.
- Nenhum módulo referencia Employee atualmente.
- Padrões estabelecidos: CRUD, paginação, Specifications, soft delete, partial update.

## 2. Modelagem proposta

### 2.1 Entidade Motorista
- `id` (Long, PK, identity)
- `funcionarioId` (Long, NOT NULL, UNIQUE, FK → employee.id)
- `cnh` (String, NOT NULL, UNIQUE, length 20)
- `categoria` (String, NOT NULL, length 5)
- `validadeCnh` (LocalDate, NOT NULL)
- `observacoes` (String, nullable, length 500)
- `criadoEm` (LocalDateTime, NOT NULL, updatable=false)
- `atualizadoEm` (LocalDateTime, NOT NULL)

**Sem campo `status` próprio.** O status do Motorista é derivado do Employee relacionado.

### 2.2 Relacionamento Motorista → Employee
- 1:1 (`funcionario_id` NOT NULL + UNIQUE)
- `funcionario_id` é FK obrigatória para `employee(id)`
- Um Employee pode ter no máximo um Motorista
- Motorista não pode existir sem Employee

## 3. Regras de negócio

### 3.1 Categorias CNH permitidas
- **A, B, C, D, E, AB, AC, AD, AE** (categorias brasileiras oficiais).
- Armazenadas em maiúsculo.
- Validadas no Service/Validator contra a lista permitida.

### 3.2 Unicidade
- `cnh`: única, obrigatória.
- `funcionarioId`: única, obrigatória.

### 3.3 Validações
- **Create**: `funcionarioId` deve existir em Employee; `cnh` não pode estar duplicada.
- **Update**: se `cnh` alterada, valida duplicidade; se `funcionarioId` alterado, valida que novo Employee existe e não tem outro Motorista.
- **CNH vencida**: não bloqueia operações, apenas campo informativo.

### 3.4 Estratégia de UPDATE
- **Parcial** (igual Employee, Address, Vehicle, Client).
- Campos `null` não sobrescrevem valores existentes.
- Campos `blank` em strings são ignorados.

### 3.5 Estratégia de DELETE
- **NÃO implementar soft delete no Motorista**.
- Motorista deve ser excluído via referência ao Employee.
- Se o Employee for soft-deleted (`status = "INACTIVE"`), o Motorista permanece.
- Deleção direta do Motorista: `DELETE /api/v1/motoristas/{id}` → hard delete (apenas se FK não for protegida por ON DELETE RESTRICT).
- **Aguardando confirmação**: Motorista deve permitir hard delete independente do status do Employee, ou bloquear se Employee estiver INACTIVE?

### 3.6 Regra de negócio pendente
- **Motorista pode ser criado para Employee INACTIVE?** (recomendado: sim, pois cadastro de motorista é independente do status atual do funcionário).

## 4. Endpoints

Base path: `/api/v1/motoristas`

| Método | Path | Descrição |
|--------|------|-----------|
| `POST` | `/` | Criar motorista (201) |
| `GET` | `/` | Listar com paginação, search e filtros (200) |
| `GET` | `/{id}` | Buscar por ID (200/404) |
| `GET` | `/employee/{funcionarioId}` | Buscar por funcionário (200/404) |
| `PUT` | `/{id}` | Atualizar parcialmente (200/404/409) |
| `DELETE` | `/{id}` | Deletar (204/404) |

### Filtros (query params)
- `search` (cnh, categoria, observacoes, nome do funcionário)
- `categoria` (A, B, C, D, E, AB, AC, AD, AE)
- `funcionarioId`
- Paginação padrão: `page=0`, `size=20`, `sort=id`, `direction=ASC`

## 5. Arquivos a criar

```
src/main/java/br/com/logicore/modules/driver/
├── controller/
│   └── DriverController.java
├── dto/
│   ├── CreateDriverRequest.java
│   ├── UpdateDriverRequest.java
│   ├── DriverResponse.java
│   └── DriverSummaryResponse.java
├── entity/
│   └── Driver.java
├── enums/
│   └── DriverCnhCategory.java
├── mapper/
│   └── DriverMapper.java
├── repository/
│   ├── DriverRepository.java
│   └── spec/
│       └── DriverSpecifications.java
├── service/
│   └── DriverService.java
└── validator/
    └── DriverValidator.java

src/test/java/br/com/logicore/modules/driver/
├── controller/
│   └── DriverControllerTest.java
├── mapper/
│   └── DriverMapperTest.java
├── repository/spec/
│   └── DriverSpecificationsIntegrationTest.java
├── service/
│   └── DriverServiceTest.java
└── validator/
│   └── DriverValidatorTest.java
```

**Nenhum arquivo existente será alterado.**

## 6. Migration

Arquivo: `src/main/resources/db/migration/V7__create_table_driver.sql`

```sql
CREATE TABLE driver
(
    id              BIGSERIAL PRIMARY KEY,
    funcionario_id  BIGINT      NOT NULL UNIQUE,
    cnh             VARCHAR(20) NOT NULL UNIQUE,
    categoria       VARCHAR(5)  NOT NULL,
    validade_cnh    DATE        NOT NULL,
    observacoes     VARCHAR(500),
    criado_em       TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_driver_employee
        FOREIGN KEY (funcionario_id)
            REFERENCES employee (id)
);
```

## 7. Estratégia de testes

Padrão igual módulos existentes.

- **DriverControllerTest** (@WebMvcTest): create, findAll, findById, findByFuncionarioId, update, delete.
- **DriverServiceTest** (@ExtendWith MockitoExtension): create, findById, findByFuncionarioId, update parcial, delete, findAll paginated, CNH duplicada, funcionarioId duplicado, categoria inválida, Employee não encontrado.
- **DriverMapperTest**: instância real, sem mocks.
- **DriverValidatorTest**: CNH duplicada create/update, funcionarioId duplicado create/update, categoria inválida.
- **DriverSpecificationsIntegrationTest** (@DataJpaTest + H2): search, categoria, funcionarioId.

## 8. Decisões que precisam de aprovação

1. **Categorias CNH**: `A, B, C, D, E, AB, AC, AD, AE` (recomendado) — correta?
2. **Exclusão**: `DELETE` deve ser hard delete sempre, ou bloquear se Employee estiver INACTIVE? (recomendado: hard delete sempre, sem bloqueio por status do Employee).
3. **Search**: incluir nome do Employee no search? (recomendado: sim, via JOIN/Specification com `employee.nome`).
4. **Summary endpoint**: incluir apenas total, ou também breakdown por categoria? (recomendado: apenas total, igual módulos simples).
5. **Endpoint `/employee/{funcionarioId}`**: necessário ou apenas `/{id}` e filtro `funcionarioId` na listagem? (recomendado: sim, pois 1:1 é lookup comum).
