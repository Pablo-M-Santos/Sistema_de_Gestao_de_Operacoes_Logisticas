# Postman Collections Plan

## Context
Create Postman collections for 6 implemented modules that are missing documentation:
- Usuario
- Perfil
- Permissao
- Cliente
- Veiculo
- Motorista

Existing collections to preserve (do not modify):
- Address
- Cargo
- Department
- Employee

## Pattern to Follow
All existing collections use Postman Collection v2.1.0 format with:
- `info` block with name, description, schema
- `variable` block with `baseUrl` = `http://localhost:8080/api/v1`
- `item` array with module name and nested request items
- Each request has: `name`, `request.method`, `request.header` (when needed), `request.url`, `request.body` (when needed), `description`
- Response examples under `response` array with `name`, `status`, `code`, `header`, `body`

## Endpoints to Document

### Usuario (`/api/v1/usuarios`)
- POST `/api/v1/usuarios` - Create
- GET `/api/v1/usuarios` - List (filters: search, email, status, funcionarioId, pagination)
- GET `/api/v1/usuarios/summary` - Summary
- GET `/api/v1/usuarios/{id}` - Find By ID
- PUT `/api/v1/usuarios/{id}` - Update
- DELETE `/api/v1/usuarios/{id}` - Delete (soft delete)
- PATCH `/api/v1/usuarios/{id}/activate` - Activate
- PATCH `/api/v1/usuarios/{id}/deactivate` - Deactivate

### Perfil (`/api/v1/perfis`)
- POST `/api/v1/perfis` - Create
- GET `/api/v1/perfis` - List (filters: search, pagination)
- GET `/api/v1/perfis/summary` - Summary
- GET `/api/v1/perfis/{id}` - Find By ID
- PUT `/api/v1/perfis/{id}` - Update
- DELETE `/api/v1/perfis/{id}` - Delete (physical)

### Permissao (`/api/v1/permissoes`)
- POST `/api/v1/permissoes` - Create
- GET `/api/v1/permissoes` - List (filters: search, pagination)
- GET `/api/v1/permissoes/summary` - Summary
- GET `/api/v1/permissoes/{id}` - Find By ID
- PUT `/api/v1/permissoes/{id}` - Update
- DELETE `/api/v1/permissoes/{id}` - Delete (physical)

### Cliente (`/api/v1/clients`)
- POST `/api/v1/clients` - Create
- GET `/api/v1/clients` - List (filters: search, status, enderecoId, pagination)
- GET `/api/v1/clients/summary` - Summary
- GET `/api/v1/clients/{id}` - Find By ID
- PUT `/api/v1/clients/{id}` - Update
- DELETE `/api/v1/clients/{id}` - Delete (soft delete)

### Veiculo (`/api/v1/vehicles`)
- POST `/api/v1/vehicles` - Create
- GET `/api/v1/vehicles` - List (filters: search, status, anoFabricacao, anoModelo, pagination)
- GET `/api/v1/vehicles/summary` - Summary
- GET `/api/v1/vehicles/{id}` - Find By ID
- PUT `/api/v1/vehicles/{id}` - Update
- DELETE `/api/v1/vehicles/{id}` - Delete (soft delete)

### Motorista (`/api/v1/motoristas`)
- POST `/api/v1/motoristas` - Create
- GET `/api/v1/motoristas` - List (filters: search, categoria, funcionarioId, pagination)
- GET `/api/v1/motoristas/summary` - Summary
- GET `/api/v1/motoristas/{id}` - Find By ID
- GET `/api/v1/motoristas/employee/{funcionarioId}` - Find By Employee ID
- PUT `/api/v1/motoristas/{id}` - Update
- DELETE `/api/v1/motoristas/{id}` - Delete (physical)

## Request/Response Bodies
Use exact field names from real DTOs:

### Usuario
- Create: nome, email, senha, funcionarioId
- Update: nome, email, senha, funcionarioId, status
- Response: id, nome, email, status, ultimoAcesso, funcionarioId, criadoEm, atualizadoEm
- Summary: total, active, inactive

### Perfil
- Create: nome, descricao
- Update: nome, descricao
- Response: id, nome, descricao, criadoEm, atualizadoEm
- Summary: total

### Permissao
- Create: nome, descricao
- Update: nome, descricao
- Response: id, nome, descricao, criadoEm, atualizadoEm
- Summary: total

### Cliente
- Create: razaoSocial, nomeFantasia, cnpj, inscricaoEstadual, telefone, email, contatoPrincipal, enderecoId
- Update: razaoSocial, nomeFantasia, cnpj, inscricaoEstadual, telefone, email, contatoPrincipal, enderecoId, status
- Response: id, razaoSocial, nomeFantasia, cnpj, inscricaoEstadual, telefone, email, contatoPrincipal, enderecoId, enderecoCep, enderecoLogradouro, enderecoNumero, enderecoComplemento, enderecoBairro, enderecoCidade, enderecoEstado, enderecoPais, status, criadoEm, atualizadoEm
- Summary: total, active, inactive, withAddress, withoutAddress

### Veiculo
- Create: placa, renavam, modelo, fabricante, anoFabricacao, anoModelo, capacidadePeso, capacidadeVolume, quilometragem
- Update: placa, renavam, modelo, fabricante, anoFabricacao, anoModelo, capacidadePeso, capacidadeVolume, quilometragem, status
- Response: id, placa, renavam, modelo, fabricante, anoFabricacao, anoModelo, capacidadePeso, capacidadeVolume, quilometragem, status, criadoEm, atualizadoEm
- Summary: total, active, inactive

### Motorista
- Create: funcionarioId, cnh, categoria, validadeCnh, observacoes
- Update: cnh, categoria, validadeCnh, observacoes
- Response: id, funcionarioId, funcionarioNome, funcionarioMatricula, cnh, categoria, validadeCnh, observacoes, criadoEm, atualizadoEm
- Summary: total

## Files to Create
- `docs/postman/usuario.collection.json`
- `docs/postman/perfil.collection.json`
- `docs/postman/permissao.collection.json`
- `docs/postman/cliente.collection.json`
- `docs/postman/veiculo.collection.json`
- `docs/postman/motorista.collection.json`

## Validation
1. Verify no existing files in `docs/postman/` were modified
2. Verify all endpoints in collections match real controller methods
3. Verify request bodies match real DTO fields
4. Verify response bodies match real Response DTOs
5. Confirm collections follow existing naming convention: lowercase module name + `.collection.json`
