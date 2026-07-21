"use client";

import { useMemo, useState } from "react";
import {
  Search,
  MoreVertical,
  Pencil,
  Eye,
  Power,
  Plus,
  X,
  ChevronDown,
} from "lucide-react";
import { Departamento, departamentos } from "@/data/navigation";

type Props = {
  onNew: () => void;
  onView: (d: Departamento) => void;
  onEdit: (d: Departamento) => void;
  onToggle: (d: Departamento) => void;
};

export default function DepartamentoTable({
  onNew,
  onView,
  onEdit,
  onToggle,
}: Props) {
  const [query, setQuery] = useState("");
  const [status, setStatus] = useState("all");
  const [menuId, setMenuId] = useState<number | null>(null);

  const filtered = useMemo(() => {
    return departamentos.filter((d) => {
      const search =
        !query ||
        d.nome.toLowerCase().includes(query.toLowerCase()) ||
        d.sigla.toLowerCase().includes(query.toLowerCase());

      const statusMatch = status === "all" || d.status === status;

      return search && statusMatch;
    });
  }, [query, status]);

  const hasFilters = query !== "" || status !== "all";

  return (
    <div
      className="
bg-white
rounded-2xl
border
border-slate-200
shadow-sm
overflow-hidden
animate-fade-up
"
    >
      {/* HEADER DA TABELA */}

      <div
        className="
p-5
border-b
border-slate-100
"
      >
        <div
          className="
flex
flex-col
xl:flex-row
gap-3
"
        >
          {/* BUSCA */}

          <div
            className="
relative
flex-1
"
          >
            <Search
              className="
absolute
left-3
top-1/2
-translate-y-1/2
w-4
h-4
text-slate-400
"
            />

            <input
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              placeholder="Buscar por nome ou sigla..."
              className="
w-full
h-10
pl-10
pr-4
rounded-xl
bg-slate-50
border
border-slate-200
text-sm
focus:outline-none
focus:ring-4
focus:ring-emerald-500/10
focus:border-emerald-400
"
            />
          </div>

          <div
            className="
flex
gap-2
"
          >
            <Select value={status} onChange={setStatus} />

            {hasFilters && (
              <button
                onClick={() => {
                  setQuery("");
                  setStatus("all");
                }}
                className="
flex
items-center
gap-2
h-10
px-3
rounded-xl
border
border-slate-200
text-sm
text-slate-600
hover:bg-slate-50
"
              >
                <X className="w-4 h-4" />
                Limpar
              </button>
            )}
          </div>
        </div>

        <p
          className="
mt-3
text-sm
text-slate-500
"
        >
          <span
            className="
font-semibold
text-slate-900
"
          >
            {filtered.length}
          </span>{" "}
          departamento(s) encontrado(s)
        </p>
      </div>

      {/* TABELA */}

      <div className="overflow-x-auto">
        <table
          className="
w-full
min-w-[900px]
"
        >
          <thead>
            <tr
              className="
border-b
border-slate-100
text-left
"
            >
              {[
                "ID",
                "Nome",
                "Sigla",
                "Descrição",
                "Status",
                "Criado em",
                "Atualizado em",
                "Ações",
              ].map((header) => (
                <th
                  key={header}
                  className="
px-5
py-3
text-xs
font-semibold
text-slate-400
uppercase
"
                >
                  {header}
                </th>
              ))}
            </tr>
          </thead>

          <tbody>
            {filtered.map((d) => (
              <tr
                key={d.id}
                className="
border-b
border-slate-100
hover:bg-slate-50
transition
"
              >
                <td className="px-5 py-4 text-sm text-slate-500 font-mono">
                  #{String(d.id).padStart(3, "0")}
                </td>

                <td className="px-5 py-4">
                  <div
                    className="
flex
items-center
gap-3
"
                  >
                    <div
                      className="
w-8
h-8
rounded-lg
bg-emerald-100
text-emerald-700
flex
items-center
justify-center
text-xs
font-bold
"
                    >
                      {d.sigla}
                    </div>

                    <span
                      className="
text-sm
font-semibold
text-slate-800
"
                    >
                      {d.nome}
                    </span>
                  </div>
                </td>

                <td className="px-5 py-4 text-sm font-medium">{d.sigla}</td>

                <td className="px-5 py-4 text-sm text-slate-500 max-w-xs truncate">
                  {d.descricao}
                </td>

                <td className="px-5 py-4">
                  <StatusBadge status={d.status} />
                </td>

                <td className="px-5 py-4 text-sm text-slate-500">
                  {formatDate(d.createdAt)}
                </td>

                <td className="px-5 py-4 text-sm text-slate-500">
                  {formatDate(d.updatedAt)}
                </td>

                <td className="px-5 py-4 text-right">
                  <RowActions
                    d={d}
                    open={menuId === d.id}
                    setOpen={() => setMenuId(menuId === d.id ? null : d.id)}
                    onView={onView}
                    onEdit={onEdit}
                    onToggle={onToggle}
                  />
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}

function RowActions({ d, open, setOpen, onView, onEdit, onToggle }: any) {
  return (
    <div className="relative">
      <button
        onClick={setOpen}
        className="
w-8
h-8
rounded-lg
hover:bg-slate-100
text-slate-400
flex
items-center
justify-center
"
      >
        <MoreVertical size={18} />
      </button>

      {open && (
        <div
          className="
absolute
right-0
top-9
z-20
w-44
bg-white
rounded-xl
border
border-slate-200
shadow-xl
py-2
"
        >
          <MenuItem icon={Eye} label="Visualizar" onClick={() => onView(d)} />

          <MenuItem icon={Pencil} label="Editar" onClick={() => onEdit(d)} />

          <MenuItem
            icon={Power}
            label={d.status === "active" ? "Inativar" : "Ativar"}
            onClick={() => onToggle(d)}
          />
        </div>
      )}
    </div>
  );
}

function MenuItem({ icon: Icon, label, onClick }: any) {
  return (
    <button
      onClick={onClick}
      className="
w-full
flex
items-center
gap-2
px-3
py-2
text-sm
hover:bg-slate-50
text-slate-700
"
    >
      <Icon size={16} />

      {label}
    </button>
  );
}

function Select({ value, onChange }: any) {
  return (
    <div className="relative">
      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="
h-10
px-3
pr-8
rounded-xl
border
border-slate-200
text-sm
bg-white
"
      >
        <option value="all">Status</option>

        <option value="active">Ativo</option>

        <option value="inactive">Inativo</option>
      </select>

      <ChevronDown
        className="
absolute
right-2
top-3
w-4
h-4
text-slate-400
pointer-events-none
"
      />
    </div>
  );
}

function StatusBadge({ status }: { status: "active" | "inactive" }) {
  return (
    <span
      className={`
px-2.5
py-1
rounded-full
text-xs
font-semibold
${
  status === "active"
    ? "bg-emerald-100 text-emerald-700"
    : "bg-slate-100 text-slate-600"
}
`}
    >
      {status === "active" ? "Ativo" : "Inativo"}
    </span>
  );
}

function formatDate(date: string) {
  const [y, m, d] = date.split("-");

  return `${d}/${m}/${y}`;
}
