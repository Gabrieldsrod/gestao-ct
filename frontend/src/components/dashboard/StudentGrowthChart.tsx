import { Area, AreaChart, CartesianGrid, XAxis, YAxis } from "recharts"
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { type ChartConfig, ChartContainer, ChartTooltip, ChartTooltipContent } from "@/components/ui/chart"
import { type MonthlyChartData } from '../../types/dashboard/MonthlyChartData';

interface StudentGrowthChartProps {
  data: MonthlyChartData[];
}

const chartConfig = {
  activeMembers: { 
    label: "Alunos",
    color: "hsl(var(--chart-orange))", 
  },
} satisfies ChartConfig

export function StudentGrowthChart({ data }: StudentGrowthChartProps) {
  return (
    <Card className="border-gray-100 shadow-sm flex flex-col">
      <CardHeader>
        <CardTitle className="text-base font-semibold text-gray-800">Crescimento de Alunos</CardTitle>
      </CardHeader>
      <CardContent className="flex-1 px-2 pt-0 sm:px-6">
        <ChartContainer config={chartConfig} className="aspect-auto h-62.5 w-full">
          <AreaChart data={data} margin={{ top: 10, right: 10, left: 0, bottom: 0 }}>
            
            {/* O Gradiente Lindo de volta, blindado contra falhas! */}
            <defs>
              <linearGradient id="fillStudents" x1="0" y1="0" x2="0" y2="1">
                <stop offset="5%" stopColor="hsl(var(--chart-orange))" stopOpacity={0.4} />
                <stop offset="95%" stopColor="hsl(var(--chart-orange))" stopOpacity={0.0} />
              </linearGradient>
            </defs>

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
                width={30}
                allowDecimals={false}
                domain={[0, (dataMax: number) => Math.max(dataMax, 5)]} 
            />
            
            <ChartTooltip 
              cursor={false} 
              content={<ChartTooltipContent indicator="line" />} 
            />
            
            <Area
              dataKey="activeMembers" 
              type="monotone"
              stroke="hsl(var(--chart-orange))" 
              fill="url(#fillStudents)"
              strokeWidth={3}
            />
          </AreaChart>
        </ChartContainer>
      </CardContent>
    </Card>
  )
}