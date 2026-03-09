import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import { usePageTitle } from '../../hooks/usePageTitle'
import { useTransactions } from '@/hooks/transaction/useTransaction'
import { Plus } from 'lucide-react'
import { TransactionSummary } from '@/components/transaction/TransactionSummary'
import { TransactionsTable } from '@/components/transaction/TransactionsTable';
import { CategoriesModal } from '@/components/category/CategoriesModal'
import { PageHeader } from '@/components/dashboard/PageHeader'

export const Route = createFileRoute('/_transactions/transactions')({
    component: TransactionsPage,
})

function TransactionsPage() {
    usePageTitle("Fluxo de Caixa")

    const hoje = new Date();
    const [currentMonth, setCurrentMonth] = useState(hoje.getMonth() + 1);
    const [currentYear, setCurrentYear] = useState(hoje.getFullYear());
    const [currentPage, setCurrentPage] = useState(0);

    const { transactions, cashflow, totalPages, totalElements, isLoading, error, refetch } = useTransactions(currentPage, currentMonth, currentYear);

    return (
        <div className="p-8 space-y-6">

            <div className="flex justify-between items-center mb-6">
                <PageHeader
                    title="Fluxo de Caixa"
                    subtitle="Acompanhe suas receitas e despesas mensais."
                />

                <div className="flex gap-3">
                    <CategoriesModal />
                    <button className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors shadow-sm">
                        <Plus className="w-4 h-4" /> Nova Transação
                    </button>
                </div>
            </div>


            <TransactionSummary
                cashflow={cashflow}
                currentMonth={currentMonth}
                currentYear={currentYear}
                onMonthChange={(month) => {
                    setCurrentMonth(month);
                    setCurrentPage(0);
                }}
                onYearChange={(year) => {
                    setCurrentYear(year);
                    setCurrentPage(0);
                }}
            />

            <TransactionsTable
                transactions={transactions}
                isLoading={isLoading}
                error={error}
                currentPage={currentPage}
                totalPages={totalPages}
                totalElements={totalElements}
                onPageChange={setCurrentPage}
            />

        </div>
    )
}