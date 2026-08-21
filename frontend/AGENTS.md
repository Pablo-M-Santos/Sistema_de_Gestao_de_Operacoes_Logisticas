<!-- BEGIN:nextjs-agent-rules -->
# This is NOT the Next.js you know

This version has breaking changes — APIs, conventions, and file structure may all differ from your training data. Read the relevant guide in `node_modules/next/dist/docs/` before writing any code. Heed deprecation notices.
<!-- END:nextjs-agent-rules -->

# LogiCore Frontend

Documentação de referência do estado **real** do frontend. Não invente funcionalidades; o único módulo de negócio implementado é **Departamentos**.

## Stack

- **Next.js 16.2.11** (App Router) + **React 19.2.4** + **TypeScript 5**
- **Tailwind CSS v4** (`@tailwindcss/postcss`) + **shadcn** (estilo `base-nova`, alias `@/components/ui`)
- **axios** para o cliente HTTP
- **lucide-react** (ícones), **react-hook-form** + **@hookform/resolvers** + **zod** (formulários/validação)
- **framer-motion** (indiretamente via `tw-animate-css` / classes de animação)
- **date-fns**, **class-variance-authority**, **clsx**, **tailwind-merge**
- ESLint 9 via `eslint-config-next` (core-web-vitals + typescript), Prettier com `prettier-plugin-tailwindcss`

Caminho base: `frontend/`. `npm run dev | build | start | lint`. Path alias `@/*` → raiz do frontend (tsconfig).

## Arquitetura

App Router do Next.js com layout raiz e páginas em `app/`. Estado e efeitos são gerenciados com `useState`/`useEffect` locais dentro de hooks de domínio; **não há um store global**.

Fluxo: **página (`app/...`) → hook de domínio (`hooks/departments/*.ts`) → service (`services/*.service.ts`) → `axios` → backend**. A UI consome os hooks e delega ações (criar/editar/toggle) a eles; o hook chama o service, que faz a requisição HTTP.

Camadas:
- `app/` — páginas e layout; orquestram hooks e componentes.
- `components/` — componentes de UI (layout, tabela, modal, toast) e componentes de feature (`departments/`).
- `hooks/departments/` — orquestram chamadas, estado de loading e ação; camada de domínio do frontend.
- `services/` — `api.ts` (instância axios) e funções que consomem a API REST.
- `types/` — modelos TypeScript espelhando os DTOs do backend.
- `data/` — metadados de navegação.

## Estrutura

Ver `docs/architecture/structure.md`.

## Fluxo da aplicação (exemplo: Departamentos)

1. `app/departments/page.tsx` (`"use client"`) monta a tela e usa:
   - `useDepartmentSummary()` → cards de totais
   - `useDepartments(page, search, status)` → lista paginada
   - `useCreateDepartment()` / `useUpdateDepartment()` / `useToggleDepartmentStatus()` → ações
2. Hooks chamam `services/department.service.ts` (`getDepartments`, `createDepartment`, `updateDepartment`, `activateDepartment`, `deactivateDepartment`, `getDepartmentSummary`).
3. Services usam a instância `api` (axios, `baseURL = process.env.NEXT_PUBLIC_API_URL`).
4. Páginas comunicam erros/sucesso via `useToast()` (success/error/warning/info).

Busca por texto e filtro de status recarregam a lista com debounce de 400 ms; paginação e `refresh` manuais após mutações.

## Integração com backend

- `services/api.ts` cria o axios com `baseURL: process.env.NEXT_PUBLIC_API_URL` e `Content-Type: application/json`.
- `.env`: `NEXT_PUBLIC_API_URL=http://localhost:8080/api/v1`.
- Backend é Spring Boot em `backend/`, módulos por contexto REST em `/api/v1/<recurso>`:
  - **Departments (implementado):**
    - `GET /api/v1/departments?search&status&page&size` (pageable, size padrão 20 no backend, 10 no frontend)
    - `GET /api/v1/departments/summary`
    - `GET /api/v1/departments/{id}`
    - `POST /api/v1/departments`
    - `PUT /api/v1/departments/{id}`
    - `PATCH /api/v1/departments/{id}/activate` e `/deactivate` (204)
  - Módulos backend existentes (ainda **sem** frontend): `address`, `cargo`, `employee`, `health`.
- Contrato de paginação (DTO `PageResponse` do backend): `content`, `page`, `size`, `totalElements`, `totalPages`.

## Módulos existentes

- **Layout/base (prontos e reutilizáveis):** `AppLayout`, `Sidebar`, `SidebarProvider`, `Header`, `PageHeader`.
- **Componentes genéricos de UI:** tabela (`DataTable`, `TableToolbar`, `TableActions`, `TablePagination`, `StatusBadge`, `EmptyState`), modal (`Modal`, `ConfirmModal`), toast (`Toast`/`ToastProvider`/`useToast`), `StatCard`.
- **Features:** apenas **Departamentos** (`DepartamentoTable`, `DepartamentoFormModal`, `DepartamentoDetalheModal`, `DepartamentoConfirmModal` + 5 hooks).
- **Menu (`data/navigation.ts`):** declara rotas que **ainda não existem** no App Router — `/deliveries`, `/drivers`, `/employees`, `/customers`, `/vehicles`, `/routes`, `/reports`, `/settings`. Apenas `/` (Dashboard) e `/departments` possuem página real. Entregas possui badge `"32"` hardcoded.

## Convenções principais

- Componentes de UI são `"use client"`; `app/layout.tsx` é Server Component e provê `ToastProvider` + `SidebarProvider` no `<body>`.
- Tailwind v4 com CSS variables (`globals.css`); classe utilitária `animate-fade-up` usada em animações de entrada.
- Hooks de domínio são nomeados `use<Acao><Entidade>` e expõem `{ data, loading, refresh }` ou `{ <acao>, loading }` — `refresh` é manual (não há invalidation automática).
- Nomes de prop de callback em componentes: `on<acao>Action` (ex.: `onSaveAction`, `onPageChangeAction`).
- Cada pacote de componentes possui `index.ts` de barrel.
- Tipos espelham os DTOs/response do backend (`types/departamento.ts`).
- shadcn configurado, mas os componentes de UI foram construídos à mão (modal, tabela, toast) em vez de usar os gerados em `components/ui`.

## Testes

- **Nenhum teste automatizado existe no frontend** (sem `*test.*`/`*spec.*` fora de `node_modules`).
- Backend possui testes (JUnit) por módulo em `backend/src/test/...`.

## Problemas conhecidos

- `@tanstack/react-query` e `@tanstack/react-table` estão em `package.json` mas **não são usados** no código — chamadas e tabela são implementadas manualmente com `useState`/`useEffect`.
- Tipo `PageResponse<T>` **duplicado**: definido em `types/page-response.ts` e redefinido em `types/departamento.ts`.
- `hooks/departments/*.ts` e o service **não tratam erros** — apenas `console.error`; falhas HTTP não são propagadas para a UI (nenhum toast de erro de rede, só de lógica de negócio manual em `page.tsx`).
- ConfirmDialog do react-hook-form/zod está instalado, mas o formulário de departamento faz validação manual inline (`DepartamentoFormModal`).
- Sidebar usa usuário hardcoded (`João Martins`, iniciais `JM`) — sem autenticação/contexto de usuário real.

## CURRENT vs PLANNED

| Item | Estado atual | Observação |
|------|--------------|-----------|
| Dashboard (`/`) | página estática_placeholder | sem dados dinâmicos |
| Departamentos (`/departments`) | CRUD completo + summary | único módulo funcional |
| Entregas, Motoristas, Funcionantes, Clientes, Veículos, Rotas, Relatórios, Configurações | rotas declaradas no menu, **páginas inexistentes** | backend possui `address`, `cargo`, `employee`, `health` sem tela |
| `DepartamentoFormModal` validação | manual inline | react-hook-form/zod disponíveis mas não usados aqui |
| Autenticação / sessão | ausente | usuário hardcoded na UI |
| Testes frontend | ausentes | — |

## Regras de desenvolvimento

- Não altere código de produção sem necessidade; prefira componentes reutilizáveis de `components/`.
- Siga o fluxo página → hook → service → axios; adicione novos tipos em `types/` espelhando o backend.
- Novas features: criar componentes em `components/<feature>/`, hooks em `hooks/<feature>/` e página em `app/<feature>/page.tsx` (`"use client"`).
- Toda comunicação HTTP passa por `services/api.ts`; não crie novas instâncias axios.
- Documentar no AGENTS.md / `docs/` o estado real; não promova planejamento futuro a funcionalidade existente.
