import { createFileRoute } from '@tanstack/react-router'
import { useState } from 'react'
import { Filter } from 'lucide-react'
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

    const today = new Date();
    const [currentMonth, setCurrentMonth] = useState(today.getMonth() + 1);
    const [currentYear, setCurrentYear] = useState(today.getFullYear());
    const [currentPage, setCurrentPage] = useState(0);
    const [typeFilter, setTypeFilter] = useState('ALL');
    const [selectedCategory, setSelectedCategory] = useState<number>(0);

    const { categories, isLoading : isCategoriesLoading, createCategory } = useCategories();

   const { transactions, totalPages, totalElements, isLoading : isTransactionsLoading, error, refetchTransactions } = useGetTransactions(currentPage, currentMonth, currentYear, typeFilter, selectedCategory);
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

            <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm flex flex-col md:flex-row gap-4 items-center justify-start">
                
                <div className="flex items-center gap-2">
                    <Filter className="w-4 h-4 text-gray-400" />
                    <label className="text-sm font-medium text-gray-600">Tipo:</label>
                    <select 
                        value={typeFilter}
                        onChange={(e) => { setTypeFilter(e.target.value); setCurrentPage(0); }}
                        className="px-3 py-2 border border-gray-200 rounded-lg text-sm bg-gray-50 outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        <option value="ALL">Todas</option>
                        <option value="INCOME">Apenas Entradas</option>
                        <option value="EXPENSE">Apenas Saídas</option>
                    </select>
                </div>

                <div className="flex items-center gap-2">
                    <label className="text-sm font-medium text-gray-600">Categoria:</label>
                    <select 
                        value={selectedCategory}
                        onChange={(e) => { setSelectedCategory(Number(e.target.value)); setCurrentPage(0); }}
                        className="px-3 py-2 border border-gray-200 rounded-lg text-sm bg-gray-50 outline-none focus:ring-2 focus:ring-blue-500 min-w-[200px]"
                    >
                        <option value={0}>Todas as Categorias</option>
                        {categories.map(cat => (
                            <option key={cat.id} value={cat.id}>{cat.name}</option>
                        ))}
                    </select>
                </div>
            </div>

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