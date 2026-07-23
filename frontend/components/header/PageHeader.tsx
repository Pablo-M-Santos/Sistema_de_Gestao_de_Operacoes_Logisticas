import type { LucideIcon } from "lucide-react";

type Crumb = {
  label: string;
  href?: string;
};

type Props = {
  title: string;

  subtitle?: string;

  crumbs?: Crumb[];

  icon?: LucideIcon;

  action?: {
    label: string;
    icon?: LucideIcon;
    onClick: () => void;
  };
};

export default function PageHeader({ title, subtitle, crumbs, icon: Icon, action }: Props) {
  return (
    <div className="animate-fade-up mb-6 flex flex-col gap-5 lg:flex-row lg:items-center lg:justify-between">
      <div>
        {crumbs && crumbs.length > 0 && (
          <div className="mb-3 flex items-center gap-2 text-xs font-medium text-slate-400">
            {crumbs.map((crumb, index) => (
              <div key={index} className="flex items-center gap-2">
                {index > 0 && <span className="text-slate-300">/</span>}

                {crumb.href ? (
                  <a href={crumb.href} className="transition hover:text-slate-700">
                    {crumb.label}
                  </a>
                ) : (
                  <span className="text-slate-600">{crumb.label}</span>
                )}
              </div>
            ))}
          </div>
        )}

        <div className="flex items-center gap-4">
          {Icon && (
            <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-emerald-50 text-emerald-600 ring-1 ring-emerald-100">
              <Icon className="h-6 w-6" />
            </div>
          )}

          <div>
            <h1 className="text-2xl font-bold tracking-tight text-slate-900 sm:text-3xl">
              {title}
            </h1>

            {subtitle && <p className="mt-1 text-sm text-slate-500">{subtitle}</p>}
          </div>
        </div>
      </div>

      {action && (
        <button
          onClick={action.onClick}
          className="flex h-11 items-center justify-center gap-2 rounded-xl bg-emerald-600 px-5 text-sm font-semibold text-white shadow-sm transition-all hover:bg-emerald-700 hover:shadow-md active:scale-95"
        >
          {action.icon && <action.icon className="h-4 w-4" />}

          {action.label}
        </button>
      )}
    </div>
  );
}
