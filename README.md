# 🚚 Sistema de Gestão de Operações Logísticas

<div align="center">

![Status](https://img.shields.io/badge/status-em%20desenvolvimento-yellow)
</div>

> Sistema completo para gestão logística de entregas, motoristas, veículos, centros de distribuição e operações.

---

# 📖 Sobre o Projeto

O Sistema de Gestão de Operações Logísticas é um sistema desenvolvido para gerenciar todo o processo logístico de uma empresa, desde o cadastro de clientes e funcionários até o controle completo das entregas.

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

# 🗂️ Modelagem do Banco de Dados

O modelo inicial do banco foi criado utilizando DB Diagram.

🔗 [📌 Ver Diagrama do Banco de Dados](https://dbdiagram.io/d/Sistema-de-Gestao-de-Operacoes-Logisticas-6a4fda1136d348d120a910dc)

# 🎨 Protótipo da Interface

A interface inicial do sistema foi desenvolvida como protótipo para validar a experiência do usuário e a organização dos módulos.

🔗 [📌 Ver Protótipo do Sistema](https://zp1v56uxy8rdx5ypatb0ockcb9tr6a-oci3--5173--639e0ff1.local-credentialless.webcontainer-api.io/)

---

# 🏗 Arquitetura

```mermaid
erDiagram
    %% ==========================================
    %% MÓDULO 1: SEGURANÇA E ACESSO
    %% ==========================================
    Usuario {
        int id PK
        string nome
        string email
        string senha
        string status
        datetime ultimo_acesso
        int funcionario_id FK
    }
    Perfil {
        int id PK
        string nome
        string descricao
    }
    Permissao {
        int id PK
        string nome
        string descricao
    }
    AreaDepartamento {
        int id PK
        string nome
        string descricao
    }

    %% ==========================================
    %% MÓDULO 2: FUNCIONÁRIOS
    %% ==========================================
    Funcionario {
        int id PK
        string matricula
        string nome
        string CPF
        string RG
        date data_nascimento
        string telefone
        string email
        int cargo_id FK
        int departamento_id FK
        int endereco_id FK
        date data_admissao
        string status
    }
    Cargo {
        int id PK
        string nome
        string descricao
    }

    %% ==========================================
    %% MÓDULO 3: CLIENTES
    %% ==========================================
    Cliente {
        int id PK
        string razao_social
        string nome_fantasia
        string CNPJ
        string inscricao_estadual
        string telefone
        string email
        string contato_principal
        int endereco_id FK
        string status
    }

    %% ==========================================
    %% MÓDULO 4: ENDEREÇOS
    %% ==========================================
    Endereco {
        int id PK
        string CEP
        string logradouro
        string numero
        string complemento
        string bairro
        string cidade
        string estado
        string pais
        decimal latitude
        decimal longitude
    }

    %% ==========================================
    %% MÓDULO 5: VEÍCULOS & MÓDULO 14: MANUTENÇÃO
    %% ==========================================
    Veiculo {
        int id PK
        string placa
        string renavam
        string modelo
        string fabricante
        int ano_fabricacao
        int ano_modelo
        decimal capacidade_peso
        decimal capacidade_volume
        int quilometragem
        string status
    }
    Manutencao {
        int id PK
        int veiculo_id FK
        string tipo
        string descricao
        date data_inicio
        date data_fim
        decimal custo
        string fornecedor
    }

    %% ==========================================
    %% MÓDULO 6: MOTORISTAS
    %% ==========================================
    Motorista {
        int id PK
        int funcionario_id FK
        string CNH
        string categoria
        date validade_CNH
        text observacoes
    }

    %% ==========================================
    %% MÓDULO 7: OPERAÇÕES DE ENTREGA (PRINCIPAL)
    %% ==========================================
    Entrega {
        int id PK
        string codigo
        int cliente_id FK
        int motorista_id FK
        int veiculo_id FK
        int origem_endereco_id FK
        int destino_endereco_id FK
        decimal peso
        decimal volume
        decimal valor_carga
        datetime data_criacao
        datetime previsao_entrega
        datetime data_entrega
        string status
        text observacoes
    }

    %% ==========================================
    %% MÓDULOS 8, 9, 10, 11: RELACIONADOS À ENTREGA
    %% ==========================================
    HistoricoEntrega {
        int id PK
        int entrega_id FK
        string status_anterior
        string status_novo
        int usuario_id FK
        datetime data_alteracao
        text observacao
    }
    Ocorrencia {
        int id PK
        int entrega_id FK
        string tipo
        string descricao
        datetime data_ocorrencia
        int usuario_id FK
        string status
    }
    Documento {
        int id PK
        int entrega_id FK
        string nome_arquivo
        string tipo_arquivo
        string URL
        datetime data_upload
        int usuario_id FK
    }
    Comprovante {
        int id PK
        int entrega_id FK
        string foto_entrega
        string assinatura_cliente
        string documento_fiscal
        datetime data_envio
    }

    %% ==========================================
    %% MÓDULO 12: CENTROS DE DISTRIBUIÇÃO
    %% ==========================================
    CentroDistribuicao {
        int id PK
        string nome
        string codigo
        int endereco_id FK
        string telefone
        int gerente_id FK
        string status
    }

    %% ==========================================
    %% MÓDULO 13: ROTAS
    %% ==========================================
    Rota {
        int id PK
        string nome
        string origem
        string destino
        decimal distancia
        string tempo_estimado
        string status
    }

    %% ==========================================
    %% MÓDULOS COMUNS (17: AUDITORIA & 18: NOTIFICAÇÕES)
    %% ==========================================
    Auditoria {
        int id PK
        string tabela
        int registro_id
        string acao
        text valor_anterior
        text valor_novo
        int usuario_id FK
        datetime data
    }
    Notificacao {
        int id PK
        int usuario_id FK
        string titulo
        string mensagem
        string tipo
        boolean lida
        datetime data_criacao
    }

    %% ==========================================
    %% RELACIONAMENTOS (CARDINALIDADES)
    %% ==========================================
    
    %% Segurança e Acesso
    Funcionario ||--o| Usuario : "possui conta"
    Usuario }o--o| Perfil : "tem"
    Usuario }o--o| AreaDepartamento : "pertence"
    
    %% Funcionários
    Cargo ||--o{ Funcionario : "alocado"
    AreaDepartamento ||--o{ Funcionario : "alocado"
    Endereco ||--o{ Funcionario : "reside"

    %% Motoristas
    Funcionario ||--o| Motorista : "eh"

    %% Clientes e CDs
    Endereco ||--o{ Cliente : "localizado"
    Endereco ||--o| CentroDistribuicao : "localizado"
    Funcionario ||--o{ CentroDistribuicao : "gerencia"

    %% Veículos e Manutenção
    Veiculo ||--o{ Manutencao : "sofre"

    %% Entrega e seus Endereços (Origem / Destino)
    Endereco ||--o{ Entrega : "origem"
    Endereco ||--o{ Entrega : "destino"
    
    %% Vínculos da Entrega
    Cliente ||--o{ Entrega : "solicita"
    Motorista ||--o{ Entrega : "transporta"
    Veiculo ||--o{ Entrega : "utilizado"

    %% Satélites da Entrega
    Entrega ||--o{ HistoricoEntrega : "registra"
    Entrega ||--o{ Ocorrencia : "sofre"
    Entrega ||--o{ Documento : "contem"
    Entrega ||--o{ Comprovante : "gera"

    %% Auditoria, Notificações e Ações de Usuários
    Usuario ||--o{ HistoricoEntrega : "modificou"
    Usuario ||--o{ Ocorrencia : "registrou"
    Usuario ||--o{ Documento : "enviou"
    Usuario ||--o{ Auditoria : "realizou"
    Usuario ||--o{ Notificacao : "recebe"
```

---

# 🧪 Qualidade e Testes

O projeto possui uma suíte de testes automatizados para garantir a qualidade das regras de negócio e dos componentes principais da aplicação.

Tecnologias utilizadas:

- JUnit 5
- Mockito
- Spring Boot Test
- JaCoCo (análise de cobertura de testes)

A cobertura dos testes é acompanhada através de relatórios gerados automaticamente pelo JaCoCo.

## Status dos Testes

| Módulo | Status |
|-|-|
| Department | ✅ 100% Coberto |
| Cargo | 🚧 Em desenvolvimento |
| Employee | 🚧 Em desenvolvimento |
| Address | 🚧 Em desenvolvimento |


📊 Documentação completa dos testes, métricas de cobertura e evidências:

➡️ [Ver documentação de testes automatizados](./backend/docs/README.md)

---

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
