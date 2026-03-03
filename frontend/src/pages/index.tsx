import { createFileRoute } from '@tanstack/react-router'
import { SummaryCard } from '../components/SummaryCard'

export const Route = createFileRoute('/')({
  component: DashboardHome,
})

function DashboardHome() {
  return (
    <div className="p-8 space-y-6">
    
    {/* CARDS DE RESUMO */}
      <div className="grid grid-cols-3 gap-6">
        <SummaryCard 
            title="Total Alunos" 
            value={125} 
            borderColorClass="border-l-blue-500" 
        />
        <SummaryCard 
            title="Inscrições Ativas" 
            value={110} 
            borderColorClass="border-l-green-500" 
        />
        <SummaryCard 
            title="Pagamentos Pendentes" 
            value="R$ 1.450,00" 
            borderColorClass="border-l-orange-500" 
        />
      </div>

      {/* ÁREA DA TABELA E GRÁFICO */}
      <div className="grid grid-cols-3 gap-6">
        <div className="col-span-2 bg-white rounded-xl border border-gray-200 shadow-sm min-h-100 p-6">
            <h3 className="font-semibold text-gray-800 mb-4">Lista de Alunos (Em breve)</h3>
        </div>
        <div className="col-span-1 bg-white rounded-xl border border-gray-200 shadow-sm min-h-100 p-6">
            <h3 className="font-semibold text-gray-800 mb-4">Receita (Em breve)</h3>
        </div>
      </div>

    </div>
  )
}