# 🧪 Documentação de Testes Automatizados

## Objetivo

Este documento apresenta a estratégia de testes automatizados aplicada no backend do Sistema de Gestão de Operações Logísticas.

O objetivo é garantir a confiabilidade das regras de negócio, validações, serviços e endpoints da aplicação através de testes unitários e testes de integração.

---

#  Tecnologias Utilizadas

- JUnit 5
- Mockito
- Spring Boot Test
- Spring Data JPA Test
- H2 Database (ambiente de testes)
- JaCoCo (análise de cobertura)

---

#  Cobertura de Testes

A cobertura é acompanhada utilizando o JaCoCo, que gera relatórios contendo:

- Cobertura de linhas
- Cobertura de métodos
- Cobertura de classes
- Cobertura de branches


<img width="1206" height="643" alt="Captura de tela de 2026-07-24 16-56-19" src="https://github.com/user-attachments/assets/66350b9a-cddf-427d-9c93-b7cceae1e5f2" />

---

Última análise:

| Módulo | Cobertura | Status |
|-|-|-|
| Department | 100% | ✅ Concluído |
| Cargo | - | 🚧 Em desenvolvimento |
| Employee | - | 🚧 Em desenvolvimento |
| Address | - | 🚧 Em desenvolvimento |

---

#  Módulo Department

## Objetivo dos Testes

Validar todas as regras relacionadas ao gerenciamento de departamentos:

- Criação
- Consulta
- Atualização
- Alteração de status
- Validações de unicidade
- Filtros e pesquisas


---

#  Endpoints Testados

| Método | Endpoint | Cenário Testado | Tipo |
|-|-|-|-|
| POST | `/api/v1/departments` | Criação de departamento | Unitário |
| GET | `/api/v1/departments` | Listagem paginada com filtros | Integração |
| GET | `/api/v1/departments/{id}` | Busca por identificador | Unitário |
| PUT | `/api/v1/departments/{id}` | Atualização de dados | Unitário |
| PATCH | `/api/v1/departments/{id}/activate` | Ativação do departamento | Unitário |
| PATCH | `/api/v1/departments/{id}/deactivate` | Desativação do departamento | Unitário |

---

#  Camadas Testadas

## Controller

Responsável por validar os endpoints REST do módulo de departamentos, garantindo que as requisições HTTP sejam encaminhadas corretamente para a camada de serviço e que as respostas retornem os status esperados.

---

### Endpoints Cobertos

| Endpoint | O que está sendo testado | Status |
|-|-|-|
| POST `/api/v1/departments` | Recebimento da requisição de criação, retorno HTTP 201 e integração com o serviço de criação | ✅ |
| GET `/api/v1/departments` | Consulta de departamentos e encaminhamento dos filtros e paginação para o serviço | ✅ |
| GET `/api/v1/departments/{id}` | Busca de departamento por identificador e retorno HTTP 200 | ✅ |
| PUT `/api/v1/departments/{id}` | Atualização de departamento, envio dos dados e retorno atualizado | ✅ |
| PATCH `/api/v1/departments/{id}/activate` | Ativação do departamento e retorno HTTP 204 | ✅ |
| PATCH `/api/v1/departments/{id}/deactivate` | Desativação do departamento e retorno HTTP 204 | ✅ |
| GET `/api/v1/departments/summary` | Retorno dos dados estatísticos do módulo de departamentos | ✅ |

---

### Comportamentos Validados

- Recebimento correto de payload JSON
- Serialização e desserialização dos DTOs
- Retorno dos códigos HTTP esperados
- Validação da chamada dos métodos da camada Service
- Retorno correto dos dados no corpo da resposta
- Encaminhamento correto dos parâmetros para os serviços
- 
## Service

Responsável por validar as regras de negócio do módulo de departamentos.

---

### Endpoints Cobertos

| Endpoint | O que está sendo testado | Status |
|-|-|-|
| POST `/api/v1/departments` | Criação de departamento, validação de nome único, persistência e retorno do objeto criado | ✅ |
| GET `/api/v1/departments/{id}` | Busca de departamento por identificador e tratamento de departamento inexistente | ✅ |
| PUT `/api/v1/departments/{id}` | Atualização dos dados do departamento e validação de alteração de nome | ✅ |
| PATCH `/api/v1/departments/{id}/activate` | Alteração do status do departamento para ACTIVE | ✅ |
| PATCH `/api/v1/departments/{id}/deactivate` | Alteração do status do departamento para INACTIVE | ✅ |
| GET `/api/v1/departments/summary` | Retorno dos indicadores de quantidade total, ativos e inativos | ✅ |
| GET `/api/v1/departments` | Listagem paginada, filtros por status e tratamento de filtros inválidos | ✅ |

---

### Regras de Negócio Validadas

- Criação de departamentos com dados válidos
- Bloqueio de atualização quando o departamento não existe
- Atualização sem validação de nome quando não houve alteração
- Alteração de status evitando mudanças desnecessárias
- Conversão correta entre Entity e Response
- Aplicação de filtros de pesquisa e status

#  Mapper

Responsável pela conversão entre objetos de entrada (DTO), entidades persistidas e objetos de resposta da API.

Os testes garantem que os dados sejam transformados corretamente entre as diferentes camadas da aplicação.

---

## Componentes Testados

| Componente | O que está sendo testado | Status |
|-|-|-|
| `CreateDepartmentRequest → Department` | Conversão dos dados recebidos pela API para entidade de domínio | ✅ |
| `Department → DepartmentResponse` | Conversão da entidade para objeto de resposta retornado pela API | ✅ |

---

## Cenários Validados

- Conversão correta dos campos básicos do departamento:
    - Nome
    - Descrição
    - Sigla

- Preservação das informações da entidade:
    - Identificador
    - Status
    - Data de criação
    - Data de atualização

- Garantia de que o mapper retorna objetos corretamente preenchidos

---

## Estratégia de Teste

O teste é realizado utilizando instância real do `DepartmentMapper`.

Não são utilizados mocks, pois o objetivo é validar a implementação da conversão dos objetos.

Tecnologia utilizada:

- JUnit 5
- AssertJ

#  Validator

Responsável por validar regras de negócio relacionadas aos dados do departamento antes da persistência ou atualização.

Os testes garantem que regras como unicidade de nome sejam respeitadas, evitando inconsistências no banco de dados.

---

## Componentes Testados

| Validação | O que está sendo testado | Status |
|-|-|-|
| `validateUniqueName()` | Verificação de existência de departamento com o mesmo nome antes da criação | ✅ |
| `validateUniqueNameForUpdate()` | Verificação de conflito de nome durante atualização de departamento | ✅ |

---

## Regras de Negócio Validadas

- Permite criação quando o nome do departamento está disponível
- Bloqueia criação quando já existe um departamento com o mesmo nome
- Permite atualização quando o nome informado não pertence a outro departamento
- Bloqueia atualização quando outro departamento já utiliza o nome informado

---

## Estratégia de Teste

O teste utiliza mocks para simular o comportamento do repositório.

O objetivo é validar somente a regra de negócio do validator, isolando a dependência de banco de dados.

Tecnologias utilizadas:

- JUnit 5
- Mockito
- AssertJ

---

## Exceções Testadas

| Cenário | Exceção esperada | Status |
|-|-|-|
| Nome duplicado na criação | `DuplicateResourceException` | ✅ |
| Nome duplicado durante atualização | `DuplicateResourceException` | ✅ |

#  Repository Specification

Responsável pela criação de filtros dinâmicos utilizados nas consultas de departamentos.

Os testes garantem que a aplicação consiga realizar buscas combinadas utilizando nome, sigla e status, mantendo o comportamento esperado dos filtros da API.

---

## Componentes Testados

| Specification | O que está sendo testado | Status |
|-|-|-|
| `withSearch()` | Busca de departamentos pelo nome ou sigla utilizando filtro parcial e case insensitive | ✅ |
| `withStatus()` | Filtro de departamentos por situação (`ACTIVE` / `INACTIVE`) | ✅ |

---

## Cenários Validados

| Cenário | Resultado esperado | Status |
|-|-|-|
| Busca pelo nome do departamento | Retorna departamentos compatíveis com o termo informado | ✅ |
| Busca pela sigla do departamento | Retorna departamentos utilizando a sigla como filtro | ✅ |
| Busca ignorando diferença entre maiúsculas e minúsculas | Retorna resultados corretamente | ✅ |
| Pesquisa vazia | Não aplica filtro de pesquisa | ✅ |
| Pesquisa contendo apenas espaços | Não aplica filtro de pesquisa | ✅ |
| Pesquisa nula | Não aplica filtro de pesquisa | ✅ |
| Filtro por status ACTIVE | Retorna somente departamentos ativos | ✅ |
| Filtro por status INACTIVE | Retorna somente departamentos inativos | ✅ |
| Status nulo | Não aplica filtro de status | ✅ |

---

## Estratégia de Teste

Os testes utilizam uma abordagem de integração utilizando banco em memória.

O comportamento da `Specification` é validado através do `DepartmentRepository`, garantindo que os filtros realmente funcionem durante a execução das consultas JPA.

Tecnologias utilizadas:

- JUnit 5
- Spring Boot Test
- Spring Data JPA
- H2 Database
- AssertJ

---

## Cobertura Garantida

A camada de Specification possui cobertura completa dos cenários implementados:

- Construção de filtros dinâmicos
- Busca textual
- Filtro por enum de status
- Tratamento de parâmetros vazios ou nulos
- Execução real da query através do Repository

Status:

100% Coberto pelo JaCoCo
