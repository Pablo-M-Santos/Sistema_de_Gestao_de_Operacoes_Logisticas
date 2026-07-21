import AppLayout from "@/components/layout/AppLayout";

export default function HomePage() {
  return (
    <AppLayout>
      <div className="rounded-xl border border-slate-200 bg-white p-8 shadow-sm">
        <h1 className="text-3xl font-bold text-slate-900">Dashboard</h1>

        <p className="mt-2 text-slate-600">
          Bem-vindo ao Sistema de Gestão de Operações Logísticas.
        </p>
      </div>
    </AppLayout>
  );
}
