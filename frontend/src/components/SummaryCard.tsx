// src/components/SummaryCard.tsx
interface SummaryCardProps {
  title: string;
  value: string | number;
  borderColorClass: string;
}

export function SummaryCard({ title, value, borderColorClass }: SummaryCardProps) {
  return (
    <div className={`bg-white p-6 rounded-xl border border-gray-200 shadow-sm border-l-4 ${borderColorClass}`}>
      <h3 className="text-sm font-medium text-gray-500">{title}</h3>
      <p className="text-3xl font-bold text-gray-800 mt-2">{value}</p>
    </div>
  )
}