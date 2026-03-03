// src/components/RevenueChart.tsx
import { Bar, BarChart, CartesianGrid, XAxis } from "recharts"
import {
  type ChartConfig,
  ChartContainer,
  ChartTooltip,
  ChartTooltipContent,
} from "@/components/ui/chart"

// 1. Nossos dados simulados (No futuro, virão do seu Spring Boot)
const chartData = [
  { month: "Jan", revenue: 4500 },
  { month: "Fev", revenue: 5200 },
  { month: "Mar", revenue: 4800 },
  { month: "Abr", revenue: 6100 },
  { month: "Mai", revenue: 5900 },
  { month: "Jun", revenue: 6800 },
]

// 2. Configuração de cores e rótulos do Shadcn
const chartConfig = {
  revenue: {
    label: "Receita (R$)",
    color: "#2563eb", // Azul para combinar com o seu tema atual
  },
} satisfies ChartConfig

export function RevenueChart() {
  return (
    // O ChartContainer aplica as cores do config no gráfico
    <ChartContainer config={chartConfig} className="h-75 w-full">
      <BarChart accessibilityLayer data={chartData}>
        {/* Linhas de grade horizontais sutis */}
        <CartesianGrid vertical={false} strokeDasharray="3 3" />
        
        {/* Eixo X com os meses */}
        <XAxis
          dataKey="month"
          tickLine={false}
          tickMargin={10}
          axisLine={false}
          tickFormatter={(value) => value.slice(0, 3)}
        />
        
        {/* Tooltip super elegante do Shadcn */}
        <ChartTooltip cursor={false} content={<ChartTooltipContent />} />
        
        {/* As barras de faturamento */}
        <Bar dataKey="revenue" fill="var(--color-revenue)" radius={4} />
      </BarChart>
    </ChartContainer>
  )
}