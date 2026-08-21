# Arquitetura do Frontend

Visão geral da arquitetura real do frontend LogiCore, construída sobre Next.js App Router.

## Visão geral

O frontend é uma SPA em **Next.js 16 (App Router)** com páginas em `app/`. A interface é Server Component na raiz (`app/layout.tsx`), e cada página funcional é um **Client Component** (`"use client"`) que orquestra hooks de domínio e componentes de UI.

**Não há store global.** Estado de UI e de dados é gerenciado localmente com `useState`/`useEffect` dentro de hooks de domínio. Chamadas de dados e paginação são manuais (não há biblioteca de data-fetching em uso).

## Fluxo da aplicação

```
Página (app/<feature>/page.tsx, "use client")
  │  compõe a tela, mantém estado de UI (modais, página, filtros)
  │  delega dados/ações a hooks de domínio
  ▼
Hook de domínio (hooks/<feature>/use<Acao><Entidade>.ts)
  │  useState + useEffect; chama o service; expõe { data, loading, refresh }
  │  ou { <acao>, loading }
  ▼
Service (services/<feature>.service.ts)
  │  funções assíncronas que chamam a instância `api` (axios)
  ▼
services/api.ts  (instância única axios)
  │  baseURL = process.env.NEXT_PUBLIC_API_URL
  ▼
Backend Spring Boot (http://localhost:8080/api/v1)
```

Exemplo concreto (Departamentos):

- `app/departments/page.tsx` consome `useDepartmentSummary`, `useDepartments`, `useCreateDepartment`, `useUpdateDepartment` e `useToggleDepartmentStatus`.
- Esses hooks chamam `services/department.service.ts` (`getDepartments`, `createDepartment`, ...).
- O service usa `services/api.ts` (axios) para atingir `/api/v1/departments`.

A página monta a tela combinando componentes genéricos de UI (`PageHeader`, `StatCard`, `DataTable`, `Modal`) com componentes de feature (`DepartamentoTable`, `DepartamentoFormModal`, `DepartamentoDetalheModal`, `DepartamentoConfirmModal`).

## Responsabilidades das camadas

| Camada | Local | Responsabilidade |
|--------|-------|------------------|
| Páginas | `app/` | Orquestram hooks e componentes; mantêm estado de UI (abertura de modais, página atual, filtros). |
| Componentes de UI | `components/`, `components/{feature}/` | Apresentação pura; recebem callbacks via props `on<Acao>Action`. |
| Hooks de domínio | `hooks/{feature}/` | Estado de dados/loading e ações; pontua entre UI e services. |
| Services | `services/` | Chamadas HTTP contra a API REST; retorno dos DTOs do backend. |
| Types | `types/` | Modelos TypeScript espelhando os DTOs/response do backend. |
| Data | `data/` | Metadados estáticos (itens de navegação do menu). |

Componentes de UI são genéricos e reutilizáveis (`Modal`, `ConfirmModal`, `DataTable`, `TableToolbar`, `TablePagination`, `StatusBadge`, `EmptyState`, `StatCard`, `Toast`). Os componentes em `components/departments/` são específicos da feature e reutilizam os genéricos.

## Integração com backend

- **Client HTTP único:** `services/api.ts` exporta uma instância `axios` com `baseURL` vindo de `process.env.NEXT_PUBLIC_API_URL` e `Content-Type: application/json`. **Todas** as requisições devem passar por essa instância.
- **Variável de ambiente:** `.env` define `NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1`.
- **Contrato REST:** endpoints versionados em `/api/v1/<recurso>`. Paginação segue o DTO `PageResponse` do backend: `{ content, page, size, totalElements, totalPages }`. Busca/filtro são enviados como query params (`search`, `status`, `page`, `size`).
- **Formato de dados:** nomes em inglês no contrato (`nome`, `sigla`, `descricao`, `status`), com datas como strings ISO (`criadoEm`, `atualizadoEm`).
- **Módulo implementado:** apenas **Departments** (`/api/v1/departments` + `/summary` + `/{id}` + `/activate`/`deactivate`). Demais módulos backend (`address`, `cargo`, `employee`, `health`) ainda não possuem tela.
- **Tratamento de erro:** os services/hooks tratam erros apenas com `console.error`; falhas de rede não geram feedback na UI (toasts são disparados pela página apenas em lógica de negócio local).
