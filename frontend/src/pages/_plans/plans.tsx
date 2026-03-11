import { createFileRoute } from '@tanstack/react-router'
import { usePageTitle } from '../../hooks/usePageTitle'
import { PlansTable } from '@/components/plan/PlansTable'; 
import { PageHeader } from '@/components/dashboard/PageHeader';

export const Route = createFileRoute('/_plans/plans')({
  component: PlansPage,
})

function PlansPage() {
  usePageTitle("Planos");

  return (
    <div className="p-8 space-y-6 max-w-3xl">
      <div className="flex justify-between items-center mb-px">
        <div>
          <PageHeader
            title="Gestão de Planos"
            subtitle="Gerencie os planos de treinamento disponíveis para os alunos."
          />
        </div>
        
        {/* Aqui no futuro podemos colocar um <Button> + Novo Plano </Button> */}
      </div>
      <PlansTable />
    </div>
  )
}