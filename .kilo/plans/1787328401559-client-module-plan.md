# Plano: Módulo Cliente

## 1. Objetivo
Criar o módulo `client` seguindo rigorosamente os padrões existentes de Department, Cargo, Address e Employee.

## 2. Decisões de domínio

### 2.1 Status
- **Estratégia**: `String status` (igual Employee).
- **Valores válidos**: `ACTIVE` e `INACTIVE`.
- **Default**: `ACTIVE` via `@PrePersist`.
- **Justificativa**: Employee já usa `String status` com esses valores; manter coerência interna do projeto.
- **Endpoints adicionais**: Nenhum. Não há `activate`/`deactivate` separados. O update já permite alterar `status` quando necessário.

### 2.2 Relacionamento Cliente → Address
- **Cardinalidade**: `@OneToOne(fetch = FetchType.LAZY)`.
- **Coluna**: `endereco_id` opcional (nullable = true).
- **FK**: `cliente.endereco_id → endereco.id`.
- **Padrão**: igual Employee (`endereco_id` opcional, busca por ID no service).

### 2.3 CNPJ
- **Coluna**: `cnpj varchar(14) not null unique`.
- **Validação**:
  - Create: obrigatório, não vazio, tamanho 11? Aguardando confirmação do usuário sobre regra de negócio para CNPJ. O schema define 14 caracteres. Vou armazenar somente números.
  - Update: valida duplicidade apenas se o CNPJ for alterado.
- **Constraint**: unique no banco.

### 2.4 Estratégia de UPDATE
- **Parcial** (igual Cargo, Address, Employee).
- Campos `null` não sobrescrevem valores existentes.
- Campos `blank` (nome, razaoSocial, etc.) são ignorados no update.

### 2.5 Estratégia de DELETE
- **Soft delete**: `DELETE /api/v1/clients/{id}` marca `status = "INACTIVE"`.
- Justificativa: Employee já faz isso; Cliente tem campo `status`.

### 2.6 Filtros e Search
- **Search geral**: `razaoSocial`, `nomeFantasia`, `cnpj`, `email`, `telefone`, `contatoPrincipal`.
- **Filtros específicos**: `status` (ACTIVE/INACTIVE/ALL), `enderecoId`.
- **Paginação**: padrão `page=0, size=20, sort=id, direction=ASC`.

## 3. Arquitetura do módulo

### 3.1 Pacotes
```
src/main/java/br/com/logicore/modules/client/
├── controller/
│   └── ClientController.java
├── dto/
│   ├── CreateClientRequest.java
│   ├── UpdateClientRequest.java
│   ├── ClientResponse.java
│   └── ClientSummaryResponse.java
├── entity/
│   └── Client.java
├── enums/
│   └── ClientStatus.java
├── mapper/
│   └── ClientMapper.java
├── repository/
│   ├── ClientRepository.java
│   └── spec/
│       └── ClientSpecifications.java
├── service/
│   └── ClientService.java
└── validator/
    └── ClientValidator.java

src/test/java/br/com/logicore/modules/client/
├── controller/
│   └── ClientControllerTest.java
├── mapper/
│   └── ClientMapperTest.java
├── repository/spec/
│   └── ClientSpecificationsIntegrationTest.java
├── service/
│   └── ClientServiceTest.java
└── validator/
│   └── ClientValidatorTest.java
```

### 3.2 Entidade
- `id` (Long, PK, identity)
- `razaoSocial` (String, not null, length 150)
- `nomeFantasia` (String, nullable, length 150)
- `cnpj` (String, not null, unique, length 14)
- `inscricaoEstadual` (String, nullable, length 30)
- `telefone` (String, nullable, length 20)
- `email` (String, nullable, length 150)
- `contatoPrincipal` (String, nullable, length 150)
- `endereco` (Address, @OneToOne LAZY, nullable)
- `status` (String, not null, length 30, default ACTIVE)
- `criadoEm` (LocalDateTime, not null, updatable=false)
- `atualizadoEm` (LocalDateTime, not null)

### 3.3 DTOs
- **CreateClientRequest**: razaoSocial (@NotBlank), nomeFantasia, cnpj (@NotBlank, @Size 14), inscricaoEstadual, telefone, email, contatoPrincipal, enderecoId (nullable), dataAdmissao? Aguardando confirmação — o schema fornecido NÃO tem `data_admissao`. Vou seguir estritamente o schema.
- **UpdateClientRequest**: todos os campos opcionais (parcial).
- **ClientResponse**: espelha entidade + dados do Address (igual EmployeeResponse).
- **ClientSummaryResponse**: total, active, inactive, withAddress, withoutAddress (igual EmployeeSummaryResponse).

### 3.4 Controller
Base path: `/api/v1/clients`

Endpoints:
1. `POST /` — create (201)
2. `GET /` — findAll com search, status, enderecoId, page, size, sort (200)
3. `GET /summary` — summary (200)
4. `GET /{id}` — findById (200/404)
5. `PUT /{id}` — update (200/404/409)
6. `DELETE /{id}` — soft delete (204/404)

OpenAPI: anotações `@Tag`, `@Operation`, `@ApiResponses` em todos os endpoints.

### 3.5 Service
- `create`: valida CNPJ único, busca Address se `enderecoId != null`, salva.
- `findAll`: Specifications + Pageable → PageResponse.
- `findById`: busca entidade ou lança ResourceNotFoundException.
- `update`: atualiza campos não-nulos/blank, valida CNPJ duplicidade se alterado, busca Address se `enderecoId` fornecido.
- `delete`: busca entidade, seta `status = "INACTIVE"`, salva.
- `summary`: counts.

### 3.6 Validator
- `validateUniqueCnpj(String cnpj)`
- `validateUniqueCnpjForUpdate(String cnpj, Long id)`
- `validateStatus(String status)`

### 3.7 Specifications
- `withSearch(String search)`: like em razaoSocial, nomeFantasia, cnpj, email, telefone, contatoPrincipal.
- `withStatus(String status)`: igual Employee (parse ACTIVE/INACTIVE/ALL).
- `withEnderecoId(Long enderecoId)`.

### 3.8 Migration
Arquivo: `src/main/resources/db/migration/V5__create_table_client.sql`

Conteúdo esperado:
```sql
CREATE TABLE client (
    id BIGINT PRIMARY KEY GENERATED ALWAYS AS IDENTITY,
    razao_social VARCHAR(150) NOT NULL,
    nome_fantasia VARCHAR(150),
    cnpj VARCHAR(14) NOT NULL UNIQUE,
    inscricao_estadual VARCHAR(30),
    telefone VARCHAR(20),
    email VARCHAR(150),
    contato_principal VARCHAR(150),
    endereco_id BIGINT,
    status VARCHAR(30) NOT NULL,
    criado_em TIMESTAMP NOT NULL,
    atualizado_em TIMESTAMP NOT NULL,
    CONSTRAINT fk_client_endereco FOREIGN KEY (endereco_id) REFERENCES address (id)
);
```

## 4. Testes

Padrão: igual módulos existentes.

- **ClientControllerTest** (@WebMvcTest): create, findAll, findById, update, delete, summary.
- **ClientServiceTest** (@ExtendWith MockitoExtension): create, findById, update (parcial), delete (soft), summary, findAll paginated, CNPJ duplicado, status inválido, Address inexistente.
- **ClientMapperTest**: instância real, sem mocks.
- **ClientValidatorTest**: CNPJ duplicado create/update, status inválido.
- **ClientSpecificationsIntegrationTest** (@DataJpaTest + H2): search, status, enderecoId.

## 5. Arquivos criados/alterados

### Criados
- `src/main/java/br/com/logicore/modules/client/entity/Client.java`
- `src/main/java/br/com/logicore/modules/client/enums/ClientStatus.java`
- `src/main/java/br/com/logicore/modules/client/dto/CreateClientRequest.java`
- `src/main/java/br/com/logicore/modules/client/dto/UpdateClientRequest.java`
- `src/main/java/br/com/logicore/modules/client/dto/ClientResponse.java`
- `src/main/java/br/com/logicore/modules/client/dto/ClientSummaryResponse.java`
- `src/main/java/br/com/logicore/modules/client/mapper/ClientMapper.java`
- `src/main/java/br/com/logicore/modules/client/repository/ClientRepository.java`
- `src/main/java/br/com/logicore/modules/client/repository/spec/ClientSpecifications.java`
- `src/main/java/br/com/logicore/modules/client/service/ClientService.java`
- `src/main/java/br/com/logicore/modules/client/validator/ClientValidator.java`
- `src/main/java/br/com/logicore/modules/client/controller/ClientController.java`
- `src/test/java/br/com/logicore/modules/client/controller/ClientControllerTest.java`
- `src/test/java/br/com/logicore/modules/client/service/ClientServiceTest.java`
- `src/test/java/br/com/logicore/modules/client/mapper/ClientMapperTest.java`
- `src/test/java/br/com/logicore/modules/client/validator/ClientValidatorTest.java`
- `src/test/java/br/com/logicore/modules/client/repository/spec/ClientSpecificationsIntegrationTest.java`
- `src/main/resources/db/migration/V5__create_table_client.sql`

### Nenhum arquivo existente será alterado.

## 6. Validação
- `mvn test -pl .` (testes do módulo client)
- `mvn test` (suíte completa)
- Validar migration: `ddl-auto=validate` no Hibernate já valida.

## 7. Dependências
- Nenhuma nova dependência.
- Usa dependências já existentes: Spring Data JPA, Hibernate, Flyway, Lombok, Springdoc, H2 (testes), JUnit/Mockito/AssertJ.
