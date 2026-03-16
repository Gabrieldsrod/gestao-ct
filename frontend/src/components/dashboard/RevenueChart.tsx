import { Bar, BarChart, CartesianGrid, XAxis, YAxis } from "recharts"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { type ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"
import { type MonthlyChartData } from '../../types/dashboard/MonthlyChartData';

interface RevenueChartProps {
  data: MonthlyChartData[];
}

const chartConfig = {
  revenue: {
    label: "Receita",
    color: "hsl(var(--chart-blue))",
  },
} satisfies ChartConfig

export function RevenueChart({ data }: RevenueChartProps) {
    
  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
  }

  return (
    <Card className="bg-white border-gray-100 shadow-sm flex flex-col">
      <CardHeader>
        <CardTitle className="text-base font-semibold text-gray-800">Receita Mensal</CardTitle>
      </CardHeader>
      <CardContent className="flex-1 px-2 pt-0 sm:px-6">
        <ChartContainer config={chartConfig} className="aspect-auto h-62.5 w-full">
          <BarChart data={data} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
            <CartesianGrid vertical={false} strokeDasharray="3 3" stroke="#f1f5f9" />
            <XAxis
              dataKey="month"
              tickLine={false}
              axisLine={false}
              tickMargin={12}
              tickFormatter={(value) => value.slice(0, 3)}
              tick={{ fill: '#64748b', fontSize: 12 }}
            />
            <YAxis 
                tickLine={false} 
                axisLine={false} 
                tickMargin={12}
                tick={{ fill: '#64748b', fontSize: 12 }}
                tickFormatter={(value) => `R$${value}`}
                width={65} // Aumentado um pouco para dar espaço ao R$
            />
            <ChartTooltip
              cursor={{ fill: '#f8fafc' }}
              content={
                <ChartTooltipContent 
                  hideLabel 
                  formatter={(value: any) => formatCurrency(Number(value))} // Intercepta e formata a caixa preta!
                />
              }
            />
            <Bar
              dataKey="revenue"
              fill="var(--color-revenue)"
              radius={[6, 6, 0, 0]}
              maxBarSize={50}
            />
          </BarChart>
        </ChartContainer>
      </CardContent>
    </Card>
  )
}