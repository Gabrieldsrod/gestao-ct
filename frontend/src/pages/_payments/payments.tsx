import { createFileRoute } from '@tanstack/react-router'
import { z } from 'zod'
import { PaymentsTable } from '../../components/payment/PaymentsTable'

const paymentsSearchSchema = z.object({
  page: z.number().catch(0),
})

export const Route = createFileRoute('/_payments/payments')({
  validateSearch: paymentsSearchSchema,
  component: PaymentsPage,
})

function PaymentsPage() {
  const { page } = Route.useSearch()

  return (
    <div className="p-8 space-y-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Financeiro</h2>
          <p className="text-sm text-gray-500">Controle de mensalidades e recebimentos.</p>
        </div>
      </div>

      <PaymentsTable currentPage={page} />
    </div>
  )
}