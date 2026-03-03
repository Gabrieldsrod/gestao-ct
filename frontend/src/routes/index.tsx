import { createFileRoute } from '@tanstack/react-router'

export const Route = createFileRoute('/')({
  component: DashboardHome,
})

function DashboardHome() {
  return (
    <div className="p-8 space-y-6">
      
      {/* CARDS DE RESUMO */}
      <div className="grid grid-cols-3 gap-6">
        <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm border-l-4 border-l-blue-500">
          <h3 className="text-sm font-medium text-gray-500">Total Alunos</h3>
          <p className="text-3xl font-bold text-gray-800 mt-2">125</p>
        </div>
        <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm border-l-4 border-l-green-500">
          <h3 className="text-sm font-medium text-gray-500">Inscrições Ativas</h3>
          <p className="text-3xl font-bold text-gray-800 mt-2">110</p>
        </div>
        <div className="bg-white p-6 rounded-xl border border-gray-200 shadow-sm border-l-4 border-l-orange-500">
          <h3 className="text-sm font-medium text-gray-500">Pagamentos Pendentes</h3>
          <p className="text-3xl font-bold text-gray-800 mt-2">R$ 1.450,00</p>
        </div>
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