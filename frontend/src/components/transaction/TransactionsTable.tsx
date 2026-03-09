import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { ChevronLeft, ChevronRight } from "lucide-react"
import type { Transaction } from "@/types/finances/Transaction"

interface TransactionsTableProps {
    transactions: Transaction[];
    isLoading: boolean;
    error: string | null;
    currentPage: number;
    totalPages: number;
    totalElements: number;
    onPageChange: (newPage: number) => void;
}

export function TransactionsTable({
    transactions,
    isLoading,
    error,
    currentPage,
    totalPages,
    totalElements,
    onPageChange
}: TransactionsTableProps) {

    const formatCurrency = (value: number) => {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
    }


    const formatDate = (dateString: string) => {
        if (!dateString) return '-';
        const datePart = dateString.includes('T') ? dateString.split('T')[0] : dateString;
        const parts = datePart.split('-');
        if (parts.length === 3) {
            return `${parts[2]}/${parts[1]}/${parts[0]}`;
        }
        return dateString;
    }

    const formatPaymentMethod = (method: string) => {
        const map: Record<string, string> = {
            'CASH': 'Dinheiro',
            'CREDIT_CARD': 'Cartão de Crédito',
            'DEBIT_CARD': 'Cartão de Débito',
            'PIX': 'Pix',
            'SLIP': 'Boleto'
        };
        return map[method] || method; // Se vier algo diferente, mostra como veio
    }

    const getTypeBadge = (type: string) => {
        if (type === 'INCOME') {
            return <Badge className="bg-green-100 text-green-800 hover:bg-green-100 border-green-200">Entrada</Badge>;
        }
        if (type === 'EXPENSE') {
            return <Badge className="bg-red-100 text-red-800 hover:bg-red-100 border-red-200">Saída</Badge>;
        }
        return <Badge>{type}</Badge>;
    }

    return (
        <div className="rounded-xl border border-gray-100 bg-white shadow-sm flex flex-col min-h-100">

            {isLoading && transactions.length === 0 ? (
                <div className="flex-1 flex items-center justify-center text-gray-500">A carregar transações...</div>
            ) : error ? (
                <div className="flex-1 flex items-center justify-center text-red-500">Erro: {error}</div>
            ) : transactions.length === 0 ? (
                <div className="flex-1 flex flex-col items-center justify-center text-gray-500 py-12">
                    <p>Nenhuma transação encontrada neste período.</p>
                </div>
            ) : (
                <div className="flex-1">
                    <Table>
                        <TableHeader className="bg-gray-50/50">
                            <TableRow>
                                <TableHead className="font-semibold text-gray-600">Data</TableHead>
                                <TableHead className="font-semibold text-gray-600">Descrição</TableHead>
                                <TableHead className="font-semibold text-gray-600">Categoria</TableHead>
                                <TableHead className="font-semibold text-gray-600">Método</TableHead>
                                <TableHead className="font-semibold text-gray-600 text-center">Tipo</TableHead>
                                <TableHead className="font-semibold text-gray-600 text-right">Valor</TableHead>
                            </TableRow>
                        </TableHeader>
                        <TableBody>
                            {transactions.map((transaction) => (
                                <TableRow key={transaction.id} className="border-gray-100 hover:bg-gray-50/50 transition-colors">
                                    <TableCell className="text-gray-600">
                                        {formatDate(transaction.transactionDate)}
                                    </TableCell>
                                    <TableCell className="font-medium text-gray-800">
                                        {transaction.description}
                                    </TableCell>
                                    <TableCell className="text-gray-600">
                                        {transaction.category}
                                    </TableCell>
                                    <TableCell className="text-gray-600 font-medium">
                                        {formatPaymentMethod(transaction.paymentMethod)}
                                    </TableCell>
                                    <TableCell className="text-center">
                                        {getTypeBadge(transaction.transactionType)}
                                    </TableCell>
                                    <TableCell className={`text-right font-semibold ${transaction.transactionType === 'INCOME' ? 'text-green-600' : 'text-red-600'}`}>
                                        {transaction.transactionType === 'EXPENSE' ? '- ' : '+ '}
                                        {formatCurrency(transaction.amount)}
                                    </TableCell>
                                </TableRow>
                            ))}
                        </TableBody>
                    </Table>
                </div>
            )}

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
                            onClick={() => onPageChange(Math.max(0, currentPage - 1))}
                            disabled={currentPage === 0 || isLoading}
                        >
                            <ChevronLeft className="h-4 w-4 mr-1" /> Anterior
                        </Button>

                        <Button
                            variant="outline"
                            size="sm"
                            className="bg-white"
                            onClick={() => onPageChange(Math.min(totalPages - 1, currentPage + 1))}
                            disabled={currentPage >= totalPages - 1 || isLoading}
                        >
                            Próxima <ChevronRight className="h-4 w-4 ml-1" />
                        </Button>
                    </div>
                </div>
            )}

        </div>
    )
}