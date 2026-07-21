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
    <div
      className="
flex
flex-col
sm:flex-row
sm:items-end
sm:justify-between
gap-4
mb-6
animate-fade-up
"
    >
      <div>
        {crumbs && crumbs.length > 0 && (
          <div
            className="
flex
items-center
gap-2
text-xs
font-semibold
text-slate-400
mb-1.5
"
          >
            {crumbs.map((crumb, index) => (
              <span
                key={index}
                className="
flex
items-center
gap-2
"
              >
                {index > 0 && <span className="text-slate-300">/</span>}

                {crumb.href ? (
                  <a
                    href={crumb.href}
                    className="
hover:text-slate-700
transition-colors
"
                  >
                    {crumb.label}
                  </a>
                ) : (
                  <span
                    className="
text-slate-600
"
                  >
                    {crumb.label}
                  </span>
                )}
              </span>
            ))}
          </div>
        )}

        <div
          className="
flex
items-center
gap-3
"
        >
          {Icon && (
            <div
              className="
grid
place-items-center
w-10
h-10
rounded-xl
bg-emerald-500/10
text-emerald-600
"
            >
              <Icon
                className="
w-5
h-5
"
              />
            </div>
          )}

          <h1
            className="
text-2xl
sm:text-3xl
font-extrabold
tracking-tight
text-slate-900
"
          >
            {title}
          </h1>
        </div>

        {subtitle && (
          <p
            className="
text-sm
text-slate-500
mt-1
"
          >
            {subtitle}
          </p>
        )}
      </div>

      {action && (
        <button
          onClick={action.onClick}
          className="
flex
items-center
gap-2
h-10
px-4
rounded-xl
bg-emerald-600
text-white
text-sm
font-semibold
shadow-lg
hover:bg-emerald-700
transition-colors
self-start
sm:self-auto
"
        >
          <action.icon
            className="
w-4
h-4
"
          />

          {action.label}
        </button>
      )}
    </div>
  );
}
