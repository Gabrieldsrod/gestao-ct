import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import { usePageTitle } from '../../hooks/usePageTitle'
import { useGetTransactions } from '../../hooks/transaction/useGetTransactions'
import { useCreateTransaction } from '@/hooks/transaction/useCreateTransaction' 
import { useCashflow } from '@/hooks/transaction/useCashFlow'
import { useCategories } from '@/hooks/category/useCategories'
import { PageHeader } from '@/components/dashboard/PageHeader'
import { TransactionSummary } from '@/components/transaction/TransactionSummary'
import { TransactionsTable } from '@/components/transaction/TransactionsTable';
import { CategoriesModal } from '@/components/category/CategoriesModal'
import { NewTransactionModal } from '@/components/transaction/NewTransactionModal'

export const Route = createFileRoute('/_transactions/transactions')({
    component: TransactionsPage,
})

function TransactionsPage() {
    usePageTitle("Fluxo de Caixa")

    const hoje = new Date();
    const [currentMonth, setCurrentMonth] = useState(hoje.getMonth() + 1);
    const [currentYear, setCurrentYear] = useState(hoje.getFullYear());
    const [currentPage, setCurrentPage] = useState(0);

    const { categories, isLoading : isCategoriesLoading, createCategory } = useCategories();

    const { transactions, totalPages, totalElements, isLoading : isTransactionsLoading, error, refetchTransactions } = useGetTransactions(currentPage, currentMonth, currentYear);
    const { cashflow } = useCashflow();
    const { createTransaction } = useCreateTransaction();

    return (
        <div className="p-8 space-y-6">

            <div className="flex justify-between items-center mb-0">
                <PageHeader
                    title="Fluxo de Caixa"
                    subtitle="Acompanhe suas receitas e despesas mensais."
                />

                <div className="flex gap-3">
                    <CategoriesModal
                        categories={categories}
                        isLoading={isCategoriesLoading}
                        createCategory={createCategory} />
                    <NewTransactionModal
                        categories={categories}
                        createTransaction={createTransaction}
                        refetchTransactions={refetchTransactions}
                    />
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
                isLoading={isTransactionsLoading}
                error={error}
                currentPage={currentPage}
                totalPages={totalPages}
                totalElements={totalElements}
                onPageChange={setCurrentPage}
            />

        </div>
    )
}