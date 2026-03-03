// src/components/StudentsGrowthChart.tsx
import { Area, AreaChart, CartesianGrid, XAxis } from "recharts"
import {
  type ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart"

// Dados simulados do crescimento (No futuro, virão do Spring Boot)
const chartData = [
  { month: "Jan", students: 85 },
  { month: "Fev", students: 92 },
  { month: "Mar", students: 104 },
  { month: "Abr", students: 115 },
  { month: "Mai", students: 120 },
  { month: "Jun", students: 125 },
]

// Configuração: vamos usar um tom de verde para diferenciar do financeiro
const chartConfig = {
  students: {
    label: "Total de Alunos",
    color: "#f0631d", 
  },
} satisfies ChartConfig

export function StudentsGrowthChart() {
  return (
    <ChartContainer config={chartConfig} className="h-75 w-full">
      <AreaChart accessibilityLayer data={chartData} margin={{ left: 12, right: 12 }}>
        <CartesianGrid vertical={false} strokeDasharray="3 3" />
        <XAxis
          dataKey="month"
          tickLine={false}
          axisLine={false}
          tickMargin={8}
          tickFormatter={(value) => value.slice(0, 3)}
        />
        <ChartTooltip cursor={false} content={<ChartTooltipContent />} />
        <Area
          dataKey="students"
          type="monotone" // Deixa a linha curvada e suave
          fill="var(--color-students)"
          fillOpacity={0.2} // Dá aquele efeito de transparência legal
          stroke="var(--color-students)"
          strokeWidth={2}
        />
      </AreaChart>
    </ChartContainer>
  )
}