import { Bar, BarChart, CartesianGrid, XAxis, YAxis } from "recharts"
import { type ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"
import { useGetPlansCountedMembers } from "@/hooks/plan/useGetPlans"; 


const chartConfig = {
  members: {
    label: "Alunos Ativos",
    color: "#eb9c25", 
  },
} satisfies ChartConfig

export function MembersPerPlanChart() {
  const { plans, isLoading, error } = useGetPlansCountedMembers();

  if (isLoading) {
    return (
      <div className="bg-white p-6 rounded-xl border border-gray-100 shadow-sm flex h-[350px] items-center justify-center">
        <span className="text-gray-500 text-sm animate-pulse">A carregar dados do gráfico...</span>
      </div>
    )
  }

  if (error) {
    return (
      <div className="bg-white p-6 rounded-xl border border-gray-100 shadow-sm flex h-[350px] items-center justify-center">
        <span className="text-red-500 text-sm">Erro ao carregar o gráfico.</span>
      </div>
    )
  }

  const chartData = plans.map(plan => ({
    name: plan.name,
    members: plan.activeMembers || 0 
  }));

  return (
    <div className="bg-white p-6 rounded-xl border border-gray-100 shadow-sm flex flex-col h-[350px]">
      <div className="mb-4">
        <h3 className="text-sm font-medium text-gray-800">Distribuição por Planos</h3>
        <p className="text-xs text-gray-500">Quantidade de alunos ativos em cada plano</p>
      </div>
      
      <ChartContainer config={chartConfig} className="min-h-[200px] w-full flex-1">
        <BarChart accessibilityLayer data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
          <CartesianGrid vertical={false} strokeDasharray="3 3" stroke="#f3f4f6" />
          <XAxis
            dataKey="name"
            tickLine={false}
            tickMargin={10}
            axisLine={false}
            tick={{ fill: '#6b7280', fontSize: 12 }}
          />
          <YAxis
            tickLine={false}
            axisLine={false}
            tickMargin={10}
            allowDecimals={false}
            tick={{ fill: '#6b7280', fontSize: 12 }}
          />
          <ChartTooltip 
            cursor={false} 
            content={<ChartTooltipContent />} 
          />
          <Bar 
            dataKey="members" 
            fill="var(--color-members)" 
            radius={[4, 4, 0, 0]} 
            barSize={40} 
          />
        </BarChart>
      </ChartContainer>
    </div>
  )
}