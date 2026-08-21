# Employee Module (Módulo de Funcionário)

## Overview

O módulo de Funcionário integra os módulos de **Cargo**, **Departamento** e **Endereço** para gerenciar informações completas de funcionários da organização.

## Architecture

### Estrutura do Módulo

```
employee/
├── controller/      # Controllers REST API
├── dto/            # Data Transfer Objects (Request/Response)
├── entity/         # JPA Entity
├── mapper/         # Mapper para conversão entity <-> DTO
├── repository/     # Data Access Layer
│   └── spec/       # JPA Specifications para filtros
├── service/        # Business Logic Layer
└── validator/      # Validações de negócio
```

## Integration Points

### 1. **Integração com Cargo**
- Campo: `cargo` (Many-to-One)
- Validação: Verifica existência do cargo ao criar/atualizar
- Retorno: Inclui ID, nome e código do cargo

### 2. **Integração com Departamento**
- Campo: `departamento` (Many-to-One)
- Validação: Verifica existência do departamento ao criar/atualizar
- Retorno: Inclui ID, nome e sigla do departamento

### 3. **Integração com Endereço**
- Campo: `endereco` (One-to-One, Opcional)
- Validação: Verifica existência do endereço ao criar/atualizar
- Retorno: Inclui todos os dados do endereço (CEP, logradouro, coordenadas, etc.)

## Data Model

### Employee Entity

```java
- id: Long (PK)
- matricula: String (unique) - Número de matrícula
- nome: String - Nome do funcionário
- cpf: String (unique) - CPF
- rg: String - RG
- dataNascimento: LocalDate
- telefone: String
- email: String
- cargo: Cargo (FK) - Relacionamento Many-to-One
- departamento: Department (FK) - Relacionamento Many-to-One
- endereco: Address (FK) - Relacionamento One-to-One (opcional)
- dataAdmissao: LocalDate
- status: String (ACTIVE/INACTIVE)
- criadoEm: LocalDateTime
- atualizadoEm: LocalDateTime
```

## API Endpoints

### Base URL: `/api/v1/employees`

#### 1. Create Employee
```
POST /api/v1/employees
Content-Type: application/json

{
  "matricula": "EMP001",
  "nome": "João Silva",
  "cpf": "12345678901",
  "rg": "123456789",
  "dataNascimento": "1990-01-01",
  "telefone": "11999999999",
  "email": "joao@example.com",
  "cargoId": 1,
  "departamentoId": 1,
  "enderecoId": 1,
  "dataAdmissao": "2024-01-01"
}
```

#### 2. List Employees (Paginated)
```
GET /api/v1/employees?search=João&cargoId=1&departamentoId=1&page=0&size=20&sort=id,asc
```

**Query Parameters:**
- `search`: Busca por nome, CPF, matrícula, email ou telefone
- `nome`: Filtro por nome (like)
- `cpf`: Filtro exato por CPF
- `cargoId`: Filtro por cargo
- `departamentoId`: Filtro por departamento
- `page`: Número da página (padrão: 0)
- `size`: Tamanho da página (padrão: 20)
- `sort`: Ordenação (padrão: id,asc)

#### 3. Get Employee Summary
```
GET /api/v1/employees/summary

Response:
{
  "total": 100,
  "active": 95,
  "inactive": 5,
  "withAddress": 80,
  "withoutAddress": 20
}
```

#### 4. Get Employee by ID
```
GET /api/v1/employees/{id}
```

#### 5. Update Employee
```
PUT /api/v1/employees/{id}
Content-Type: application/json

{
  "nome": "João Silva Updated",
  "email": "joao.updated@example.com",
  "cargoId": 2,
  "departamentoId": 2,
  "enderecoId": 2,
  "status": "ACTIVE"
}
```

#### 6. Delete Employee
```
DELETE /api/v1/employees/{id}

Note: Deleta logicamente, marcando status como INACTIVE
```

## DTOs

### CreateEmployeeRequest
```java
- matricula: String (required, unique)
- nome: String (required)
- cpf: String (required, unique, 11 chars)
- rg: String
- dataNascimento: LocalDate
- telefone: String
- email: String (valid email)
- cargoId: Long (required)
- departamentoId: Long (required)
- enderecoId: Long (optional)
- dataAdmissao: LocalDate (required)
```

### UpdateEmployeeRequest
```java
- matricula: String (optional)
- nome: String (optional)
- cpf: String (optional, unique)
- rg: String (optional)
- dataNascimento: LocalDate (optional)
- telefone: String (optional)
- email: String (optional)
- cargoId: Long (optional)
- departamentoId: Long (optional)
- enderecoId: Long (optional)
- dataAdmissao: LocalDate (optional)
- status: String (optional)
```

### EmployeeResponse
```java
- id: Long
- matricula: String
- nome: String
- cpf: String
- rg: String
- dataNascimento: LocalDate
- telefone: String
- email: String
- cargoId: Long
- cargoNome: String
- cargoCodigo: String
- departamentoId: Long
- departamentoNome: String
- departamentoSigla: String
- enderecoId: Long
- enderecoCep: String
- enderecoLogradouro: String
- enderecoNumero: String
- enderecoComplemento: String
- enderecoBairro: String
- enderecoCidade: String
- enderecoEstado: String
- enderecoPais: String
- enderecoLatitude: BigDecimal
- enderecoLongitude: BigDecimal
- dataAdmissao: LocalDate
- status: String
- criadoEm: LocalDateTime
- atualizadoEm: LocalDateTime
```

## Validations

### Validator (EmployeeValidator)
- `validateUniqueCpf`: Verifica duplicação de CPF na criação
- `validateUniqueMatricula`: Verifica duplicação de matrícula na criação
- `validateUniqueCpfForUpdate`: Verifica duplicação de CPF na atualização
- `validateUniqueMatriculaForUpdate`: Verifica duplicação de matrícula na atualização

### Exception Handling
- `ResourceNotFoundException`: Quando recurso não é encontrado
- `DuplicateResourceException`: Quando há violação de unicidade
- `BusinessException`: Para outros erros de negócio

## Repository Features

### JpaSpecificationExecutor
Implementa filtros avançados através de Specifications:

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee>
```

### Custom Query Methods
- `findByCpf(String cpf)`: Encontra por CPF
- `findByMatricula(String matricula)`: Encontra por matrícula
- `existsByCpf(String cpf)`: Verifica existência por CPF
- `existsByMatricula(String matricula)`: Verifica existência por matrícula
- `countActive()`: Conta funcionários ativos
- `countInactive()`: Conta funcionários inativos
- `countWithAddress()`: Conta funcionários com endereço

## Business Rules

1. **Matrícula e CPF são únicos** - Sistema não permite duplicação
2. **Cargo e Departamento obrigatórios** - Todo funcionário deve ter cargo e departamento
3. **Endereço é opcional** - Funcionário pode ou não ter endereço registrado
4. **Status padrão** - Ao criar, status é definido como "ACTIVE"
5. **Soft Delete** - Ao deletar, status é marcado como "INACTIVE"
6. **Criação e Atualização com Timestamp** - Registra quando foi criado e atualizado

## Example Workflow

```
1. Criar Endereço (opcional)
   POST /api/v1/addresses

2. Criar Cargo (se não existir)
   POST /api/v1/cargos

3. Criar Departamento (se não existir)
   POST /api/v1/departments

4. Criar Funcionário
   POST /api/v1/employees
   - Referenciar cargoId
   - Referenciar departamentoId
   - Referenciar enderecoId (opcional)

5. Consultar Funcionários
   GET /api/v1/employees?search=termo&cargoId=1

6. Atualizar Funcionário
   PUT /api/v1/employees/{id}

7. Deletar Funcionário (inativa)
   DELETE /api/v1/employees/{id}
```

## Performance Considerations

- **Lazy Loading**: Cargo, Departamento e Endereço usam FetchType.LAZY
- **Paginação**: Sempre use paginação para listagens
- **Índices**: Recomenda-se criar índices em:
  - `cpf` (UNIQUE)
  - `matricula` (UNIQUE)
  - `cargo_id` (FK)
  - `departamento_id` (FK)

## Testing

Endpoints testados em:
- `br.com.logicore.modules.employee.controller.EmployeeControllerTest`
- `br.com.logicore.modules.employee.service.EmployeeServiceTest`
- `br.com.logicore.modules.employee.validator.EmployeeValidatorTest`
- `br.com.logicore.modules.employee.mapper.EmployeeMapperTest`

