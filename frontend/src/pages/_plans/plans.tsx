import { createFileRoute } from '@tanstack/react-router'
import { usePageTitle } from '../../hooks/usePageTitle'
import { PlansTable } from '@/components/plan/PlansTable';
import { PageHeader } from '@/components/dashboard/PageHeader';
import { CreatePlanModal } from '@/components/plan/CreatePlanModal';
import { useGetPlansCountedMembers } from '@/hooks/plan/useGetPlans'; 

export const Route = createFileRoute('/_plans/plans')({
  component: PlansPage,
})

function PlansPage() {
  usePageTitle("Planos");
  const { plans, isLoading, error, refetch } = useGetPlansCountedMembers();

  return (
    <div className="p-8 space-y-6 max-w-4xl">
      <div className="flex justify-between items-center mb-px">
        <div>
          <PageHeader
            title="Gestão de Planos"
            subtitle="Gerencie os planos de treinamento disponíveis para os alunos."
          />
        </div>
        <div className="flex gap-3">
          <CreatePlanModal onCreateSuccess={refetch} />
        </div>
      </div>
      
      <PlansTable plans={plans} isLoading={isLoading} error={error} refetch={refetch} />
    </div>
  )
}