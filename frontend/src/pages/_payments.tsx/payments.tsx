import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { z } from 'zod'
import { usePendingPayments } from '../../hooks/payments/usePendingPayments'
import { ConfirmPaymentModal } from '../../components/payment/ConfirmPaymentModal'
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Button } from "@/components/ui/button"
import { ChevronLeft, ChevronRight } from "lucide-react"

const paymentsSearchSchema = z.object({
  page: z.number().catch(0),
})

export const Route = createFileRoute('/_payments/tsx/payments')({
  validateSearch: paymentsSearchSchema,
  component: PaymentsPage,
})

function PaymentsPage() {
  const { page } = Route.useSearch()
  const navigate = useNavigate({ from: Route.fullPath })
  
  const { payments, totalPages, totalElements, isLoading, error } = usePendingPayments(page, 10)

  const handlePageChange = (newPage: number) => {
    navigate({ search: { page: newPage } })
  }

  return (
    <div className="p-8 space-y-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Financeiro: Pendências</h2>
          <p className="text-sm text-gray-500">Controle de mensalidades aguardando pagamento.</p>
        </div>
      </div>

      <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-6 flex flex-col min-h-[calc(100vh-240px)]">
        
        {isLoading && payments.length === 0 ? (
          <div className="p-8 text-center text-gray-500">A carregar mensalidades...</div>
        ) : error ? (
          <div className="p-8 text-center text-red-500">Erro: {error}</div>
        ) : payments.length === 0 ? (
          <div className="p-12 text-center text-green-600 bg-green-50 rounded-lg border border-green-100">
            <h3 className="text-lg font-bold">Tudo em dia!</h3>
            <p className="text-sm">Não há nenhum aluno com pagamento pendente.</p>
          </div>
        ) : (
          <>
            <div className="flex-1">
              <Table>
                <TableHeader className="bg-gray-50/50">
                  <TableRow>
                    <TableHead className="font-semibold text-gray-600">Aluno</TableHead>
                    <TableHead className="font-semibold text-gray-600">Plano</TableHead>
                    <TableHead className="font-semibold text-gray-600">Vencimento</TableHead>
                    <TableHead className="font-semibold text-gray-600">Valor</TableHead>
                    <TableHead className="text-right font-semibold text-gray-600">Ação</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {payments.map((p) => (
                    <TableRow key={p.paymentId} className="hover:bg-gray-50/50">
                      <TableCell className="font-medium text-gray-800">{p.memberName}</TableCell>
                      <TableCell className="text-gray-600">{p.planName}</TableCell>
                      <TableCell className="text-red-600 font-medium">{p.dueDate}</TableCell>
                      <TableCell className="text-gray-800 font-semibold">
                        R$ {p.amountDue?.toFixed(2)}
                      </TableCell>
                      <TableCell className="text-right">
                        <ConfirmPaymentModal 
                          paymentId={p.paymentId} 
                          memberName={p.memberName} 
                          paymentValue={p.amountDue} 
                        />
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            </div>

            {totalPages > 0 && (
              <div className="flex items-center justify-between px-6 py-4 border-t border-gray-100 bg-gray-50/30">
                <span className="text-sm text-gray-500 font-medium">
                  Página {page + 1} de {totalPages} ({totalElements} pendências)
                </span>
                <div className="flex gap-2">
                  <Button variant="outline" size="sm" onClick={() => handlePageChange(Math.max(0, page - 1))} disabled={page === 0 || isLoading}>
                    <ChevronLeft className="h-4 w-4 mr-1" /> Anterior
                  </Button>
                  <Button variant="outline" size="sm" onClick={() => handlePageChange(Math.min(totalPages - 1, page + 1))} disabled={page >= totalPages - 1 || isLoading}>
                    Próxima <ChevronRight className="h-4 w-4 ml-1" />
                  </Button>
                </div>
              </div>
            )}
          </>
        )}
      </div>
    </div>
  )
}