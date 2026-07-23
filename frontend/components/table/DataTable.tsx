"use client";

import type { TableColumn, TableAction } from "./types";
import TableActions from "./TableActions";
import EmptyState from "./EmptyState";

type DataTableProps<T> = {
  data: T[];
  columns: TableColumn<T>[];

  actions?: TableAction<T>[];

  getRowIdAction: (row: T) => string | number;

  emptyTitle?: string;
  emptyDescription?: string;
};

export default function DataTable<T>({
  data,
  columns,
  actions = [],
  getRowIdAction,
  emptyTitle,
  emptyDescription,
}: DataTableProps<T>) {
  if (!data.length) {
    return (
      <EmptyState
        title={emptyTitle}
        description={emptyDescription}
      />
    );
  }

  return (
    <div className="overflow-hidden rounded-2xl border border-slate-200 bg-white shadow-sm">
      <div className="overflow-x-auto">
        <table className="w-full min-w-[900px]">
          <thead>
            <tr className="border-b border-slate-100 text-left">
              {columns.map((column) => (
                <th
                  key={column.key.toString()}
                  style={{
                    width: column.width,
                  }}
                  className={[
                    "px-5 py-3 text-xs font-semibold uppercase text-slate-400",
                    column.headerClassName ?? "",
                  ].join(" ")}
                >
                  {column.title}
                </th>
              ))}

              {actions.length > 0 && (
                <th className="px-5 py-3 text-right text-xs font-semibold uppercase text-slate-400">
                  Ações
                </th>
              )}
            </tr>
          </thead>

          <tbody>
            {data.map((row) => (
              <tr
                key={getRowIdAction(row)}
                className="border-b border-slate-100 transition hover:bg-slate-50"
              >
                {columns.map((column) => (
                  <td
                    key={column.key.toString()}
                    className={[
                      "px-5 py-4 text-sm text-slate-700",
                      column.cellClassName ?? "",
                    ].join(" ")}
                  >
                    {column.render
                      ? column.render(row)
                      : String(row[column.key as keyof T] ?? "")}
                  </td>
                ))}

                {actions.length > 0 && (
                  <td className="px-5 py-4 text-right">
                    <TableActions
                      row={row}
                      actions={actions}
                    />
                  </td>
                )}
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
}