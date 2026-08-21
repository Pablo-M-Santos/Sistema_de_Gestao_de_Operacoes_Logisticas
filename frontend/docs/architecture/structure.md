# Estrutura do Frontend

Organização real de diretórios do frontend LogiCore (caminho base `frontend/`).

## Árvore de diretórios

```
frontend/
├── app/                      # Páginas e layout (App Router)
├── components/               # Componentes de UI (genéricos + por feature)
│   ├── cards/                # StatCard
│   ├── departments/          # Componentes da feature Departamentos
│   ├── header/               # PageHeader
│   ├── layout/               # AppLayout, Sidebar, SidebarProvider, Header
│   ├── modal/                # Modal, ConfirmModal
│   ├── table/                # DataTable e componentes auxiliares
│   └── toast/                # Toast/ToastProvider/useToast
├── data/                     # Metadados de navegação (menu)
├── hooks/
│   └── departments/          # Hooks de domínio de Departamentos
├── services/                 # Client HTTP (axios) e funções de API
├── types/                    # Modelos TypeScript (DTOs do backend)
├── public/                   # Assets estáticos públicos
└── docs/                     # Documentação
```

## Responsabilidade de cada diretório

### `app/` — Páginas e layout
Rotas do App Router. `layout.tsx` é Server Component e provê `ToastProvider` + `SidebarProvider` no `<body>`. As demais são páginas `"use client"`.

Arquivos reais:
- `layout.tsx` — layout raiz ( fontes Geist, providers globais).
- `page.tsx` — Dashboard (placeholder estático).
- `departments/page.tsx` — tela de Departamentos (CRUD + summary).
- `globals.css` — temas Tailwind v4 (CSS vars, `animate-fade-up`).

Rotas declaradas em `data/navigation.ts` mas **sem página** no App Router: `/deliveries`, `/drivers`, `/employees`, `/customers`, `/vehicles`, `/routes`, `/reports`, `/settings`.

### `components/` — Componentes de UI
Todos `"use client"`. Cada subpacote possui `index.ts` de barrel.

| Pacote | Conteúdo | Responsabilidade |
|--------|----------|------------------|
| `cards/` | `StatCard` | Card de estatística com ícone, valor e destaque de cor. |
| `header/` | `PageHeader` | Cabeçalho de página (título, subtítulo, breadcrumbs, ícone, ação). |
| `layout/` | `AppLayout`, `Sidebar`, `SidebarProvider`, `Header` | Estrutura visual (sidebar colapsável, topo, área principal). |
| `modal/` | `Modal`, `ConfirmModal`, `types` | Diálogo modal genérico e confirmação de ação. |
| `table/` | `DataTable`, `TableToolbar`, `TableActions`, `TablePagination`, `StatusBadge`, `EmptyState`, `types`, `utils` | Tabela genérica (colunas, ações, filtros, busca, paginação, estado vazio). |
| `toast/` | `Toast`, `ToastContainer`, `ToastProvider`, `useToast`, `types` | Sistema de notificações (success/error/warning/info). |
| `departments/` | `DepartamentoTable`, `DepartamentoFormModal`, `DepartamentoDetalheModal`, `DepartamentoConfirmModal`, `ConfirmDialog` | Componentes específicos da feature Departamentos. |

Os componentes de `departments/` reutilizam os genéricos (`Modal`, `ConfirmModal`, `DataTable`, `StatusBadge`). `ConfirmDialog.tsx` está vazio.

### `hooks/departments/` — Hooks de domínio
Todos `"use client"`, com estado local (`useState`/`useEffect`). Nomenclatura `use<Acao><Entidade>`.

| Hook | Função |
|------|--------|
| `useDepartments` | Lista paginada (debounce de 400 ms em busca/status); expõe `{ departments, pagination, loading, refresh }`. |
| `useDepartmentSummary` | Cards de totais; expõe `{ data, loading, refresh }`. |
| `useCreateDepartment` | Ação de criar; expõe `{ create, loading }`. |
| `useUpdateDepartment` | Ação de atualizar; expõe `{ update, loading }`. |
| `useToggleDepartmentStatus` | Ativar/inativar; expõe `{ toggleStatus, loading }`. |

Erros são tratados apenas com `console.error` (sem propagação para a UI).

### `services/` — Chamadas HTTP
- `api.ts` — instância única do axios (`baseURL` de `NEXT_PUBLIC_API_URL`, `Content-Type: application/json`).
- `department.service.ts` — funções que consomem a API de Departamentos (`getDepartments`, `createDepartment`, `updateDepartment`, `activateDepartment`, `deactivateDepartment`, `getDepartmentSummary`).

### `types/` — Modelos TypeScript
Espelham os DTOs/response do backend.
- `departamento.ts` — `Departamento`, `DepartmentSummary`, `DepartmentStatus`, `CreateDepartmentRequest`, `UpdateDepartmentRequest` (e `PageResponse<T>`).
- `page-response.ts` — `PageResponse<T>` (duplicado em relação a `departamento.ts`).

### `data/` — Metadados de navegação
- `navigation.ts` — itens do menu (`navMain`, `navSecondary`) consumidos pela Sidebar. Rotas listadas nem todas possuem página.

### `public/` — Assets estáticos
Arquivos SVG/ícone padrão do Next.js (não usados pela aplicação).

## Principais módulos/componentes

**Feature implementada:** apenas **Departamentos** (página + 4 componentes + 5 hooks + 1 service).

**Componentes base reutilizáveis** (prontos): `AppLayout`, `Sidebar`/`SidebarProvider`, `Header`, `PageHeader`, `StatCard`, `Modal`, `ConfirmModal`, `DataTable`, `TableToolbar`, `TablePagination`, `TableActions`, `StatusBadge`, `EmptyState`, `Toast`/`ToastProvider`/`useToast`.

**Módulos backend sem frontend:** `address`, `cargo`, `employee`, `health`.
