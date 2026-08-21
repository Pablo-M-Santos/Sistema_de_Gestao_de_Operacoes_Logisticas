# Plano: Módulo Employee (Frontend)

Implementar a tela de Employee seguindo o padrão de Department, adaptando apenas onde o contrato real do backend diverge. **Sem refatoração, sem redesign, sem alterar backend.**

## Contrato do backend (Employee) — referência

Base: `/api/v1/employees`

| Método | Path | Body/Params | Resposta |
|--------|------|-------------|----------|
| POST | `/api/v1/employees` | `CreateEmployeeRequest` | `EmployeeResponse` (201) |
| GET | `/api/v1/employees` | `search, nome, cpf, cargoId, departamentoId, pageable` (size padrão 20) | `PageResponse<EmployeeResponse>` |
| GET | `/api/v1/employees/summary` | — | `EmployeeSummaryResponse` |
| GET | `/api/v1/employees/{id}` | path `id` | `EmployeeResponse` |
| PUT | `/api/v1/employees/{id}` | `UpdateEmployeeRequest` | `EmployeeResponse` |
| DELETE | `/api/v1/employees/{id}` | path `id` | 204 (soft-delete → `INACTIVE`) |

**Divergências do contrato de Department (confirmadas com o usuário):**
- **Sem filtro de status** no list (Department tem filtro por status). Filtros reais: `search`, `nome`, `cpf`, `cargoId`, `departamentoId`.
- **DELETE** em vez de `activate/deactivate`. Sem endpoint de reação.
- **Relacionamentos obrigatórios:** `cargoId` (Cargo) + `departamentoId` (Department) obrigatórios; `enderecoId` (Address) opcional.

Listas para selects (já existem no backend):
- `GET /api/v1/cargos` → `CargoResponse { id, nome, descricao, codigo, ativo }`
- `GET /api/v1/departments` → `DepartmentResponse { id, nome, descricao, sigla, status }`
- `GET /api/v1/addresses` → `AddressResponse { id, cep, logradouro, numero, ..., estado }`

## Arquivos a criar

Página + feature (espelhar `app/departments/page.tsx` e `components/departments/`):
- `app/employees/page.tsx` — `"use client"`, orquestração.
- `components/employees/EmployeeTable.tsx` — tabela + toolbar + filtros.
- `components/employees/EmployeeFormModal.tsx` — criar/editar (com selects de Cargo/Department/Address).
- `components/employees/EmployeeDetalheModal.tsx` — visualização.
- `components/employees/EmployeeConfirmModal.tsx` — confirmação de exclusão (substitui o de ativar/inativar).
- `components/employees/index.ts` — barrel.
- `hooks/employees/useEmployees.ts` — lista paginada (debounce 400 ms em busca).
- `hooks/employees/useEmployeeSummary.ts` — totais.
- `hooks/employees/useCreateEmployee.ts`, `useUpdateEmployee.ts`, `useDeleteEmployee.ts` — ações.
- `services/employee.service.ts` — endpoints Employee.
- `services/cargo.service.ts`, `department.service.ts`, `address.service.ts` — listas para selects.
- `services/types.ts` (ou `types/employee.ts`) — tipos TS do Employee + interfaces para Cargo/Department/Address (respostas).
- `data/navigation.ts` — **alterar**: corrigir rótulo/route de `/employees` (hoje aponta para "Funcionários" e a route não existe; ajustar label/ícone se necessário).

## Arquivos existentes a reutilizar (sem alterar)

- `components/layout/*` (AppLayout, Sidebar, SidebarProvider, Header).
- `components/header/PageHeader.tsx`, `components/cards/StatCard.tsx`.
- `components/table/*` (DataTable, TableToolbar, TableActions, TablePagination, StatusBadge, EmptyState).
- `components/modal/*` (Modal, ConfirmModal).
- `components/toast/*` (ToastProvider, useToast).
- `services/api.ts` (instância axios).
- `types/page-response.ts` (`PageResponse<T>`).

> **Nota:** `types/departamento.ts` redefine `PageResponse<T>`. Usar `types/page-response.ts` para Employee (evitar redefinição).

## Hook / Service necessários

### services/employee.service.ts
- `getEmployees(page, search, filters, size=10)` — GET com params `{ search, nome, cpf, cargoId, departamentoId }` (omitir `undefined`).
- `getEmployeeSummary()` — GET `/summary`.
- `getEmployee(id)` — GET `/{id}`.
- `createEmployee(data)` — POST.
- `updateEmployee(id, data)` — PUT `/{id}`.
- `deleteEmployee(id)` — DELETE `/{id}`.

### services/{cargo,department,address}.service.ts
- `getCargos()`, `getDepartments()`, `getAddresses()` — GET da lista (paginação backend; size fixo ex.: 100, ou paginar/select simplificado). Retornam arrays para popular selects.

### hooks/employees/*.ts
- `useEmployees(page, filters)` → `{ employees, pagination, loading, refresh }`. Debounce 400 ms em `search`/`nome`/`cpf`; filtros `cargoId`/`departamentoId` recarregam sem debounce.
- `useEmployeeSummary()` → `{ data, loading, refresh }`.
- `useCreateEmployee()` → `{ create, loading }`.
- `useUpdateEmployee()` → `{ update, loading }`.
- `useDeleteEmployee()` → `{ remove, loading }`.

## Types (espelhar DTOs)

```ts
type EmployeeStatus = "ACTIVE" | "INACTIVE";

interface Employee {
  id: number; matricula: string; nome: string; cpf: string; rg?: string;
  dataNascimento?: string; telefone?: string; email?: string;
  cargoId: number; cargoNome?: string; cargoCodigo?: string;
  departamentoId: number; departamentoNome?: string; departamentoSigla?: string;
  enderecoId?: number; enderecoCep?: string; enderecoLogradouro?: string;
  enderecoNumero?: string; enderecoComplemento?: string; enderecoBairro?: string;
  enderecoCidade?: string; enderecoEstado?: string; enderecoPais?: string;
  dataAdmissao: string; status: EmployeeStatus;
  criadoEm: string; atualizadoEm: string;
}

interface EmployeeSummary { total: number; active: number; inactive: number; withAddress: number; withoutAddress: number; }

// selects
interface CargoOption { id: number; nome: string; codigo: string; }
interface DepartmentOption { id: number; nome: string; sigla: string; }
interface AddressOption { id: number; cep?: string; logradouro?: string; cidade?: string; estado?: string; }
```

## Fluxo da tela (espelhar Department)

`AppLayout` → `PageHeader` (ícone `Users`, ação "Novo Funcionário") → `StatCard` (grid 5 colunas: total, ativos, inativos, com endereço, sem endereço) → `EmployeeTable` → 3 modais.

- **Tabela:** colunas ID, matrícula, nome, CPF, cargo (nome), departamento (sigla), situação (`StatusBadge`). Ações: Visualizar, Editar, Excluir.
- **Filtros:** busca (nome/cpf/matrícula) + selects de Cargo e Departamento (botão "Limpar"). Sem filtro de status.
- **Paginação:** `TablePagination` (size 10), contador `{start}-{end} de {totalElements}`.
- **Empty:** `EmptyState` quando sem dados.
- **Feedback:** `toast.success` em criar/editar/excluir; `toast.error` genérico + `console.error` em falhas (padrão Department).
- **Refresh:** após mutação, `refreshEmployees()` + `employeeSummary.refresh()` em `Promise.all`.

## Formulário (EmployeeFormModal)

Campos (validação manual inline, padrão DepartamentoFormModal):
- matrícula (obrigatório), nome (obrigatório), CPF (obrigatório, 11 chars), RG (opcional),
- dataNascimento, telefone, email (`@Email`),
- **Cargo** (select obrigatório), **Departamento** (select obrigatório), **Endereço** (select opcional),
- dataAdmissao (obrigatório).
- Modo edição: `useEffect` carrega dados; update envia só campos alterados.

## Integração por endpoint

| Ação | Hook | Service | Endpoint |
|------|------|---------|----------|
| Listar | useEmployees | getEmployees | GET `/api/v1/employees` |
| Resumo | useEmployeeSummary | getEmployeeSummary | GET `/summary` |
| Criar | useCreateEmployee | createEmployee | POST |
| Editar | useUpdateEmployee | updateEmployee | PUT `/{id}` |
| Excluir | useDeleteEmployee | deleteEmployee | DELETE `/{id}` |
| Select Cargo | (hook interno da página/form) | getCargos | GET `/api/v1/cargos` |
| Select Department | | getDepartments | GET `/api/v1/departments` |
| Select Address | | getAddresses | GET `/api/v1/addresses` |

## Testes

Nenhuma estrutura de teste existe no frontend. **Fora de escopo** nesta etapa.

## Riscos / divergências

- **Sem filtro de status** (divergência Department) — decidido omitir na UI; filtros reais serão busca + Cargo + Departamento.
- **DELETE sem reação** — a ação na tabela será "Excluir" (não "Inativar"); sem botão de reativar.
- **Tamanho de página:** frontend usa 10, backend usa 20 — manter 10 no frontend (padrão Department).
- **Selects de依赖:** Employee é o primeiro módulo com relacionamentos; os serviços de Cargo/Department/Address serão criados agora e reutilizados por futuros módulos.
- **Erro de rede:** sem estado de erro dedicado (padrão Department — `toast.error` genérico).
- **sem health check** — primeiro request já valida disponibilidade.

## Validação

1. `npm run lint` sem erros.
2. `npm run build` passa.
3. `npm run dev` → `/employees`: lista, busca, filtros Cargo/Departamento, paginação, criar, editar, visualizar, excluir (toast + refresh).
4. Conferir console sem erros de tipos; selects populando do backend.
