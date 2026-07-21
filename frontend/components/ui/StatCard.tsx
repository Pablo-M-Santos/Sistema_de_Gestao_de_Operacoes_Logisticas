import type { LucideIcon } from "lucide-react";

interface Props {
  icon: LucideIcon;

  label: string;

  value: number;

  hint?: string;

  accent?: "brand" | "blue" | "rose" | "ink";

  index?: number;
}

const accentMap = {
  brand: {
    bg: "bg-emerald-500/10",
    text: "text-emerald-600",
    ring: "ring-emerald-500/15",
  },

  blue: {
    bg: "bg-blue-500/10",
    text: "text-blue-600",
    ring: "ring-blue-500/15",
  },

  rose: {
    bg: "bg-rose-500/10",
    text: "text-rose-600",
    ring: "ring-rose-500/15",
  },

  ink: {
    bg: "bg-slate-500/10",
    text: "text-slate-700",
    ring: "ring-slate-500/15",
  },
};

export default function StatCard({
  icon: Icon,

  label,

  value,

  hint,

  accent = "ink",

  index = 0,
}: Props) {
  const color = accentMap[accent];

  return (
    <div
      className="
bg-white
rounded-2xl
border
border-slate-200
p-5
transition-all
duration-300
hover:shadow-lg
hover:-translate-y-1
animate-fade-up
"
      style={{
        animationDelay: `${index * 70}ms`,
      }}
    >
      <div
        className="
flex
items-start
justify-between
"
      >
        <div
          className={`
grid
place-items-center
w-11
h-11
rounded-xl
${color.bg}
${color.text}
ring-1
${color.ring}
`}
        >
          <Icon className="w-5 h-5" />
        </div>
      </div>

      <div className="mt-4">
        <p
          className="
text-sm
font-medium
text-slate-500
"
        >
          {label}
        </p>

        <div
          className="
flex
items-baseline
gap-1.5
mt-1
"
        >
          <span
            className="
text-3xl
font-bold
tracking-tight
text-slate-900
"
          >
            {value}
          </span>
        </div>

        {hint && (
          <p
            className="
text-xs
text-slate-400
mt-2
"
          >
            {hint}
          </p>
        )}
      </div>
    </div>
  );
}
