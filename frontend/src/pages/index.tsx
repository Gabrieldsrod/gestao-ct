import { createFileRoute } from '@tanstack/react-router'
import { useDashboard } from '../hooks/dashboard/useDashboard'
import { PageHeader } from '../components/dashboard/PageHeader'
import { DashboardStatCards } from '../components/dashboard/DashboardStatCards'
import { MembersPerPlanChart } from '../components/dashboard/MembersPerPlanChart'
import { RevenueChart } from '../components/dashboard/RevenueChart'
import { usePageTitle } from '@/hooks/usePageTitle'

export const Route = createFileRoute('/')({
  component: DashboardPage,
})

function DashboardPage() {
  usePageTitle('Visão Geral');

  const { summary, isLoading, error } = useDashboard()

  if (isLoading) {
    return (
      <div className="p-8 flex flex-col items-center justify-center min-h-[calc(100vh-100px)]">
        <div className="w-8 h-8 border-4 border-blue-600 border-t-transparent rounded-full animate-spin mb-4"></div>
        <p className="text-gray-500 font-medium">A carregar visão geral do CT...</p>
      </div>
    )
  }

  if (error || !summary) {
    return (
      <div className="p-8 text-center min-h-[calc(100vh-100px)] flex flex-col items-center justify-center">
        <p className="text-red-500 font-bold mb-2">Erro ao carregar o painel</p>
        <p className="text-gray-500 text-sm">{error || 'Dados indisponíveis do servidor'}</p>
      </div>
    )
  }

  return (
    <div className="p-8 space-y-6">
      <PageHeader 
        title="Visão Geral do CT" 
        subtitle="Visão geral do desempenho do CT, finanças e crescimento dos alunos."
      />

      <DashboardStatCards 
        activeMembers={summary.activeMembers}
        delinquentMembers={summary.delinquentMembers}
        totalIncome={summary.finance.totalIncome}
        netBalance={summary.finance.netBalance}
      />

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6 pt-4">
        <MembersPerPlanChart />

        <RevenueChart data={summary.chartData} />
      </div>
    </div>
  )
}