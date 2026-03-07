import { useState } from "react"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { ChevronLeft, ChevronRight } from "lucide-react"
import { useNavigate } from "@tanstack/react-router"
import { useGetPayments } from "../../hooks/payments/useGetPayments"
import { ConfirmPaymentModal } from "./ConfirmPaymentModal"

function getPaymentStatusBadge(status: string) {
    switch (status) {
        case 'PAID':
            return <Badge className="bg-green-100 text-green-800 hover:bg-green-100 border-green-200">Pago</Badge>
        case 'PENDING':
            return <Badge className="bg-yellow-100 text-yellow-800 hover:bg-yellow-100 border-yellow-200">Pendente</Badge>
        case 'OVERDUE':
            return <Badge className="bg-red-100 text-red-800 hover:bg-red-100 border-red-200">Atrasado</Badge>
        case 'CANCELED':
            return <Badge className="bg-gray-100 text-gray-800 hover:bg-gray-100 border-gray-200">Cancelado</Badge>
        default:
            return <Badge>{status}</Badge>
    }
}

interface PaymentsTableProps {
    currentPage: number;
}

export function PaymentsTable({ currentPage }: PaymentsTableProps) {
    const navigate = useNavigate({ from: '/payments' })
    const [statusFilter, setStatusFilter] = useState<string>("ALL")
    const { payments, totalPages, totalElements, isLoading, error } = useGetPayments(currentPage, 10, statusFilter)

    const handlePageChange = (newPage: number) => {
        navigate({
            search: (prev: any) => ({ ...prev, page: newPage })
        })
    }

    const handleTabChange = (newStatus: string) => {
        setStatusFilter(newStatus);
        navigate({
            search: (prev: any) => ({ ...prev, page: 0 })
        });
    }

    return (
        <div className="flex flex-col gap-4">
            <div className="flex space-x-2 overflow-x-auto pb-1">
                {[
                    { id: "ALL", label: "Todos" },
                    { id: "PENDING", label: "Pendentes" },
                    { id: "PAID", label: "Pagos" },
                    { id: "OVERDUE", label: "Atrasados" }
                ].map((tab) => (
                    <button
                        key={tab.id}
                        onClick={() => handleTabChange(tab.id)}
                        className={`px-4 py-2 text-sm font-medium rounded-full transition-colors whitespace-nowrap
                            ${statusFilter === tab.id
                                ? 'bg-blue-600 text-white shadow-sm'
                                : 'bg-white border border-gray-200 text-gray-600 hover:bg-gray-50'
                            }`}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>

            {/* TABELA DENTRO DO CARD BRANCO */}
            <div className="rounded-md border border-gray-100 bg-white flex flex-col min-h-[calc(100vh-300px)] shadow-sm">
                
                {isLoading && payments.length === 0 ? (
                    <div className="p-8 text-center text-gray-500 flex-1 flex items-center justify-center">A carregar mensalidades...</div>
                ) : error ? (
                    <div className="p-8 text-center text-red-500 flex-1 flex items-center justify-center">Erro: {error}</div>
                ) : payments.length === 0 ? (
                    <div className="p-8 text-center text-gray-500 flex-1 flex items-center justify-center">
                        Nenhum pagamento encontrado nesta categoria.
                    </div>
                ) : (
                    <div className="flex-1">
                        <Table>
                            <TableHeader className="bg-gray-50/50">
                                <TableRow>
                                    <TableHead className="font-semibold text-gray-600">Aluno</TableHead>
                                    <TableHead className="font-semibold text-gray-600">Plano</TableHead>
                                    <TableHead className="font-semibold text-gray-600">Vencimento</TableHead>
                                    <TableHead className="font-semibold text-gray-600">Pagamento</TableHead>
                                    <TableHead className="font-semibold text-gray-600">Valor</TableHead>
                                    <TableHead className="font-semibold text-gray-600 text-center">Status</TableHead>
                                    <TableHead className="text-right font-semibold text-gray-600">Ação</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {payments.map((p) => (
                                    <TableRow key={p.id} className="border-gray-100 hover:bg-gray-50/50 transition-colors">
                                        <TableCell className="font-medium text-gray-800">{p.memberName}</TableCell>
                                        <TableCell className="text-gray-600">{p.planName}</TableCell>
                                        <TableCell className="text-gray-600">{p.dueDate}</TableCell>
                                        <TableCell className="text-gray-600">{p.paymentDate || '-'}</TableCell>
                                        <TableCell className="text-gray-800 font-semibold">
                                            R$ {(p.amountPaid || p.amountDue)?.toFixed(2)}
                                        </TableCell>
                                        <TableCell className="text-center">
                                            {getPaymentStatusBadge(p.status)}
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <div className="flex justify-end items-center gap-2">
                                                {p.status !== 'PAID' && p.status !== 'CANCELED' ? (
                                                    <ConfirmPaymentModal
                                                        paymentId={p.id}
                                                        memberName={p.memberName}
                                                        paymentValue={p.amountDue}
                                                    />
                                                ) : (
                                                    <span className="text-xs text-gray-400 italic mr-2">Liquidado</span>
                                                )}
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </div>
                )}

                {/* RODAPÉ E PAGINAÇÃO IDÊNTICOS */}
                {totalPages > 0 && !isLoading && (
                    <div className="flex items-center justify-between px-6 py-4 border-t border-gray-100 bg-gray-50/30">
                        <span className="text-sm text-gray-500 font-medium">
                            Página {currentPage + 1} de {totalPages} <span className="text-gray-400 font-normal">({totalElements} registros)</span>
                        </span>

                        <div className="flex gap-2">
                            <Button
                                variant="outline"
                                size="sm"
                                className="bg-white"
                                onClick={() => handlePageChange(Math.max(0, currentPage - 1))}
                                disabled={currentPage === 0 || isLoading}
                            >
                                <ChevronLeft className="h-4 w-4 mr-1" /> Anterior
                            </Button>

                            <Button
                                variant="outline"
                                size="sm"
                                className="bg-white"
                                onClick={() => handlePageChange(Math.min(totalPages - 1, currentPage + 1))}
                                disabled={currentPage >= totalPages - 1 || isLoading}
                            >
                                Próxima <ChevronRight className="h-4 w-4 ml-1" />
                            </Button>
                        </div>
                    </div>
                )}
            </div>
        </div>
    )
}