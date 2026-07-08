# 🚚 Logistics Management System (LMS)

> Sistema completo para gestão logística de entregas, motoristas, veículos, centros de distribuição e operações.

---

# 📖 Sobre o Projeto

O Logistics Management System (LMS) é um sistema desenvolvido para gerenciar todo o processo logístico de uma empresa, desde o cadastro de clientes e funcionários até o controle completo das entregas.

O objetivo é simular um sistema corporativo real, aplicando boas práticas de arquitetura, desenvolvimento Full Stack, segurança, escalabilidade e organização de código.

Este projeto possui foco em aprendizado de tecnologias modernas utilizadas pelo mercado.

---

# 🎯 Objetivos

- Desenvolver uma aplicação Full Stack moderna.
- Aplicar arquitetura em camadas.
- Utilizar boas práticas de desenvolvimento.
- Trabalhar autenticação e autorização.
- Implementar documentação automática.
- Construir uma API REST robusta.
- Desenvolver uma interface moderna em React.
- Simular um ambiente corporativo real.

---

# 🛠 Stack Tecnológica

## Backend

- Java 21
- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- Flyway
- Swagger / OpenAPI
- Maven
- Docker

---

## Frontend

- React
- TypeScript
- Vite
- Tailwind CSS
- Axios
- React Router
- TanStack Query

---

## Infraestrutura

- Docker
- Docker Compose
- PostgreSQL
- pgAdmin (Opcional)

---

# 📁 Estrutura do Projeto

```
logistics-management-system/

backend/
frontend/
database/
docker/
docs/

README.md
docker-compose.yml
```

---

# 🚀 Roadmap

## ✅ Fase 1

Preparação do ambiente.

### Backend

- [ ] Spring Boot
- [ ] Java 21
- [ ] PostgreSQL
- [ ] Flyway
- [ ] Swagger
- [ ] Docker

### Frontend

- [ ] React
- [ ] TypeScript
- [ ] Vite
- [ ] Tailwind
- [ ] Axios
- [ ] React Router
- [ ] TanStack Query

### Infra

- [ ] Docker Compose
- [ ] PostgreSQL
- [ ] pgAdmin

### Entrega

Projeto sobe completamente utilizando:

- Backend
- Frontend
- PostgreSQL
- Docker Compose

---

# 🧩 Módulos do Sistema

## 1. Segurança e Controle de Acesso

Responsável pela autenticação e autorização dos usuários.

### Usuário

| Campo |
|--------|
| id |
| nome |
| email |
| senha |
| status |
| último acesso |
| funcionário_id |

### Perfil (Role)

| Campo |
|--------|
| id |
| nome |
| descrição |

Perfis iniciais:

- ADMIN
- SUPERVISOR
- OPERADOR
- MOTORISTA

### Permissões

(Evolução futura)

### Departamento

Exemplos:

- Logística
- Administrativo
- Financeiro
- Comercial
- RH

---

## 2. Funcionários

Cadastro completo dos colaboradores.

### Funcionário

- matrícula
- nome
- CPF
- RG
- data nascimento
- telefone
- email
- cargo
- departamento
- endereço
- data admissão
- status

### Cargo

Exemplos:

- Motorista
- Supervisor
- Gerente
- Analista
- Assistente

---

## 3. Clientes

Cadastro de clientes.

Informações:

- Razão Social
- Nome Fantasia
- CNPJ
- Inscrição Estadual
- Telefone
- Email
- Contato Principal
- Endereço

---

## 4. Endereços

Entidade compartilhada por diversos módulos.

Campos:

- CEP
- Logradouro
- Número
- Complemento
- Bairro
- Cidade
- Estado
- País
- Latitude
- Longitude

Utilizado por:

- Funcionários
- Clientes
- Centros de Distribuição
- Origem da Entrega
- Destino da Entrega

---

## 5. Veículos

Cadastro da frota.

Campos principais:

- Placa
- Renavam
- Modelo
- Fabricante
- Ano
- Capacidade
- Quilometragem

Status:

- Disponível
- Em rota
- Em manutenção
- Inativo

---

## 6. Motoristas

Especialização de Funcionário.

Informações adicionais:

- CNH
- Categoria
- Validade
- Observações

---

## 7. Operações Logísticas

Módulo principal do sistema.

### Entrega

Campos principais:

- Código
- Cliente
- Motorista
- Veículo
- Origem
- Destino
- Peso
- Volume
- Valor da carga
- Datas
- Status

Status possíveis:

- Criada
- Em preparação
- Carregada
- Em rota
- Entregue
- Cancelada
- Atrasada

---

## 8. Histórico da Entrega

Registra todas as mudanças de status da entrega.

---

## 9. Ocorrências

Tipos:

- Cliente ausente
- Veículo quebrado
- Trânsito
- Endereço incorreto
- Mercadoria danificada
- Outro

---

## 10. Documentos

Arquivos relacionados à entrega.

---

## 11. Comprovantes

- Foto
- Assinatura
- Documento Fiscal

---

## 12. Centros de Distribuição

Cadastro dos CDs da empresa.

---

## 13. Rotas

Gerenciamento das rotas.

Informações:

- Origem
- Destino
- Distância
- Tempo estimado

---

## 14. Manutenção de Veículos

Controle das manutenções.

Informações:

- Tipo
- Período
- Custo
- Fornecedor

---

## 15. Dashboard

Indicadores principais:

- Total de entregas
- Entregas em andamento
- Entregas atrasadas
- Entregas concluídas
- Motoristas ativos
- Veículos disponíveis
- Clientes ativos

---

## 16. Relatórios

Relatórios previstos:

- Entregas por período
- Entregas por cliente
- Entregas por motorista
- Utilização da frota
- Ocorrências
- Custos Operacionais

---

## 17. Auditoria

Registro completo das alterações do sistema.

Informações:

- Tabela
- Registro
- Ação
- Valor anterior
- Valor novo
- Usuário
- Data

---

## 18. Notificações

Comunicação interna do sistema.

Campos:

- Usuário
- Título
- Mensagem
- Tipo
- Lida
- Data de criação

---

# 🏗 Arquitetura

```
Frontend (React)

↓

API REST

↓

Spring Boot

↓

Service

↓

Repository

↓

PostgreSQL
```

---

# 📦 Banco de Dados

Banco principal:

- PostgreSQL

Controle de versões:

- Flyway

---

# 📚 Documentação da API

A API será documentada utilizando:

- Swagger UI
- OpenAPI 3

---

# 🔐 Segurança

Será implementado:

- JWT
- Spring Security
- Controle por Roles
- Controle por Permissões
- Refresh Token (futuro)

---

# 🐳 Docker

O projeto será executado através do Docker Compose contendo:

- Backend
- Frontend
- PostgreSQL
- pgAdmin

---

# 📈 Evoluções Futuras

- Upload de arquivos para S3
- Integração com Google Maps
- Rastreamento em tempo real
- Mensageria com RabbitMQ
- Cache com Redis
- WebSocket
- Dashboard em tempo real
- Testes Automatizados
- CI/CD
- Kubernetes

---

# 👨‍💻 Autor

Projeto desenvolvido para estudo e evolução profissional utilizando tecnologias modernas do ecossistema Java e React.
