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

    icon: LucideIcon;

    onClick: () => void;
  };
};

export default function PageHeader({
  title,

  subtitle,

  crumbs,

  icon: Icon,

  action,
}: Props) {
  return (
    <div className="animate-fade-up mb-6 flex flex-col gap-4 sm:flex-row sm:items-end sm:justify-between">
      <div>
        {crumbs && crumbs.length > 0 && (
          <div className="mb-1.5 flex items-center gap-2 text-xs font-semibold text-slate-400">
            {crumbs.map((crumb, index) => (
              <span key={index} className="flex items-center gap-2">
                {index > 0 && <span className="text-slate-300">/</span>}

                {crumb.href ? (
                  <a href={crumb.href} className="transition-colors hover:text-slate-700">
                    {crumb.label}
                  </a>
                ) : (
                  <span className="text-slate-600">{crumb.label}</span>
                )}
              </span>
            ))}
          </div>
        )}

        <div className="flex items-center gap-3">
          {Icon && (
            <div className="grid h-10 w-10 place-items-center rounded-xl bg-emerald-500/10 text-emerald-600">
              <Icon className="h-5 w-5" />
            </div>
          )}

          <h1 className="text-2xl font-extrabold tracking-tight text-slate-900 sm:text-3xl">
            {title}
          </h1>
        </div>

        {subtitle && <p className="mt-1 text-sm text-slate-500">{subtitle}</p>}
      </div>

      {action && (
        <button
          onClick={action.onClick}
          className="flex h-10 items-center gap-2 self-start rounded-xl bg-emerald-600 px-4 text-sm font-semibold text-white shadow-lg transition-colors hover:bg-emerald-700 sm:self-auto"
        >
          <action.icon className="h-4 w-4" />

          {action.label}
        </button>
      )}
    </div>
  );
}
