import { createFileRoute } from '@tanstack/react-router'
import { z } from 'zod'
import { PaymentsTable } from '../../components/payment/PaymentsTable'
import { usePageTitle } from '@/hooks/usePageTitle'
import { PageHeader } from '@/components/dashboard/PageHeader'
import { PaymentFeesConfigModal } from '@/components/fee/EditPaymentFeeModal'

const paymentsSearchSchema = z.object({
  page: z.number().catch(0),
})

export const Route = createFileRoute('/_payments/payments')({
  validateSearch: paymentsSearchSchema,
  component: PaymentsPage,
})

function PaymentsPage() {
  usePageTitle('Pagamentos');

  const { page } = Route.useSearch()

  return (
    <div className="p-8 space-y-6">

      <div className="flex justify-between items-start mb-px">
        <PageHeader
          title="Gestão de Pagamentos"
          subtitle="Controle de mensalidades e recebimentos."
        />
        
        <div className="flex gap-3">
            <PaymentFeesConfigModal />
        </div>
      </div>

      <PaymentsTable currentPage={page} />
    </div>
  )
}