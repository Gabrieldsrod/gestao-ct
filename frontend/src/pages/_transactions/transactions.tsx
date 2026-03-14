import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import { usePageTitle } from '../../hooks/usePageTitle'
import { useTransactions } from '@/hooks/transaction/useTransaction'
import { PageHeader } from '@/components/dashboard/PageHeader'
import { TransactionSummary } from '@/components/transaction/TransactionSummary'
import { TransactionsTable } from '@/components/transaction/TransactionsTable';
import { CategoriesModal } from '@/components/category/CategoriesModal'
import { NewTransactionModal } from '@/components/transaction/NewTransactionModal'
import { useCategories } from '@/hooks/category/useCategories'

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

    const { transactions, cashflow, totalPages, totalElements, isLoading : isTransactionsLoading, error, createTransaction } = useTransactions(currentPage, currentMonth, currentYear);

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