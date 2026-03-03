import { createFileRoute } from '@tanstack/react-router'
import { SummaryCard } from '../components/SummaryCard'
import { RevenueChart } from '../components/RevenueChart'
import { StudentsGrowthChart } from '../components/member/MembersGrowthChart' // <-- Novo import

export const Route = createFileRoute('/')({
  component: DashboardHome,
  head: () => ({
    meta: [
      {
        title: 'Dashboard - Academia',
      },
    ],
  }),
})

function DashboardHome() {
  return (
    <div className="p-8 space-y-6">
      
      {/* CARDS DE RESUMO */}
      <div className="grid grid-cols-3 gap-6">
        <SummaryCard title="Total Alunos" value={125} borderColorClass="border-l-blue-500" />
        <SummaryCard title="Inscrições Ativas" value={110} borderColorClass="border-l-green-500" />
        <SummaryCard title="Pagamentos Pendentes" value="R$ 1.450,00" borderColorClass="border-l-orange-500" />
      </div>

      {/* ÁREA DOS DOIS GRÁFICOS */}
      <div className="grid grid-cols-2 gap-6">
        
        {/* COLUNA 1: Crescimento de Alunos */}
        <div className="bg-white rounded-xl border border-gray-200 shadow-sm min-h-100 p-6 flex flex-col">
            <h3 className="font-semibold text-gray-800 mb-6">Crescimento de Alunos</h3>
            <div className="flex-1">
              <StudentsGrowthChart />
            </div>
        </div>
        
        {/* COLUNA 2: Receita Mensal */}
        <div className="bg-white rounded-xl border border-gray-200 shadow-sm min-h-100 p-6 flex flex-col">
            <h3 className="font-semibold text-gray-800 mb-6">Receita Mensal</h3>
            <div className="flex-1">
              <RevenueChart />
            </div>
        </div>

      </div>
    </div>
  )
}