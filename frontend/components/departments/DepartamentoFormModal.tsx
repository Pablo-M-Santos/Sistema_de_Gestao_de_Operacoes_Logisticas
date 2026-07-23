"use client";

import { useEffect, useState } from "react";

import { Modal } from "@/components/modal";

import type { Departamento } from "@/types/departamento";

type Props = {
  open: boolean;

  departamento?: Departamento | null;

  onCloseAction: () => void;

  onSaveAction: (data: DepartamentoFormData) => void;
};

export type DepartamentoFormData = {
  nome: string;
  sigla: string;
  descricao: string;
};

export default function DepartamentoFormModal({
  open,
  departamento,
  onCloseAction,
  onSaveAction,
}: Props) {
  const [form, setForm] = useState<DepartamentoFormData>({
    nome: "",
    sigla: "",
    descricao: "",
  });

  const isEditing = !!departamento;

  useEffect(() => {
    if (departamento) {
      setForm({
        nome: departamento.nome,
        sigla: departamento.sigla,
        descricao: departamento.descricao,
      });
    } else {
      setForm({
        nome: "",
        sigla: "",
        descricao: "",
      });
    }
  }, [departamento, open]);

  function handleChange(field: keyof DepartamentoFormData, value: string | boolean) {
    setForm((prev) => ({
      ...prev,
      [field]: value,
    }));
  }

  function handleSubmit() {
    onSaveAction(form);
  }

  return (
    <Modal
      open={open}
      title={isEditing ? "Editar Departamento" : "Novo Departamento"}
      size="lg"
      onClose={onCloseAction}
    >
      <div className="space-y-6">
        <div className="grid grid-cols-1 gap-5 md:grid-cols-3">
          <div className="md:col-span-2">
            <label className="mb-1.5 block text-sm font-semibold text-slate-700">
              Nome do departamento
            </label>

            <p className="mb-2 text-xs text-slate-400">
              Informe o nome completo da área organizacional.
            </p>

            <input
              value={form.nome}
              onChange={(e) => handleChange("nome", e.target.value)}
              className="h-11 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 text-sm text-slate-800 transition outline-none placeholder:text-slate-400 focus:border-emerald-400 focus:bg-white focus:ring-4 focus:ring-emerald-500/10"
              placeholder="Ex: Tecnologia da Informação"
            />
          </div>

          <div>
            <label className="mb-1.5 block text-sm font-semibold text-slate-700">Sigla</label>

            <p className="mb-2 text-xs text-slate-400">Identificação curta.</p>

            <input
              value={form.sigla}
              onChange={(e) => handleChange("sigla", e.target.value.toUpperCase())}
              maxLength={20}
              className="h-11 w-full rounded-xl border border-slate-200 bg-slate-50 px-4 text-sm font-semibold tracking-wide text-slate-800 uppercase transition outline-none placeholder:text-slate-400 focus:border-emerald-400 focus:bg-white focus:ring-4 focus:ring-emerald-500/10"
              placeholder="TI"
            />
          </div>
        </div>

        <div>
          <div className="mb-1.5 flex items-center justify-between">
            <label className="text-sm font-semibold text-slate-700">Descrição</label>

            <span className="text-xs text-slate-400">{form.descricao.length}/500</span>
          </div>

          <p className="mb-2 text-xs text-slate-400">
            Explique a finalidade e responsabilidades do departamento.
          </p>

          <textarea
            value={form.descricao}
            onChange={(e) => handleChange("descricao", e.target.value.slice(0, 500))}
            rows={5}
            className="w-full resize-none rounded-xl border border-slate-200 bg-slate-50 p-4 text-sm leading-6 text-slate-800 transition outline-none placeholder:text-slate-400 focus:border-emerald-400 focus:bg-white focus:ring-4 focus:ring-emerald-500/10"
            placeholder="Ex: Responsável pela gestão dos sistemas, infraestrutura e suporte tecnológico."
          />
        </div>

        <div className="flex justify-end gap-3 border-t border-slate-100 pt-5">
          <button
            type="button"
            onClick={onCloseAction}
            className="h-11 rounded-xl border border-slate-200 px-5 text-sm font-semibold text-slate-600 transition hover:bg-slate-50"
          >
            Cancelar
          </button>

          <button
            type="button"
            onClick={handleSubmit}
            className="h-11 rounded-xl bg-emerald-600 px-6 text-sm font-semibold text-white shadow-sm transition hover:bg-emerald-700 hover:shadow-md"
          >
            {isEditing ? "Salvar alterações" : "Criar departamento"}
          </button>
        </div>
      </div>
    </Modal>
  );
}
