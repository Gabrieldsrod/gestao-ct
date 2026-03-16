import { ArrowDownCircle, ArrowUpCircle, Wallet } from 'lucide-react'
import type { CashFlow } from '@/types/finances/CashFlow';

interface TransactionSummaryProps {
    periodMode: 'MONTHLY' | 'ALL_TIME';
    setPeriodMode: (mode: 'MONTHLY' | 'ALL_TIME') => void;
    cashflow: CashFlow | null;
    currentMonth: number;
    currentYear: number;
    onMonthChange: (month: number) => void;
    onYearChange: (year: number) => void;
}

const MESES = [
    "Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho",
    "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"
];

export function TransactionSummary({
    periodMode, setPeriodMode, cashflow, currentMonth, currentYear, onMonthChange, onYearChange
}: TransactionSummaryProps) {

    const formatCurrency = (value: number = 0) => {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value)
    }

    const isPositive = (cashflow?.netBalance || 0) >= 0;

    return (
        <div className="space-y-6">

            <div className="flex items-center justify-between bg-white p-4 rounded-xl border border-gray-100 shadow-sm">
                <div className="flex items-center gap-4">
                    <label className="text-sm font-medium text-gray-600">Visualização:</label>

                    <select
                        value={periodMode}
                        onChange={(e) => setPeriodMode(e.target.value as 'MONTHLY' | 'ALL_TIME')}
                        className="px-3 py-1.5 bg-gray-50 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-blue-500"
                    >
                        <option value="MONTHLY">Mensal</option>
                        <option value="ALL_TIME">Todo o Histórico</option>
                    </select>

                    {periodMode === 'MONTHLY' && (
                        <div className="flex gap-2 animate-in fade-in slide-in-from-left-2">
                            <select
                                value={currentMonth}
                                onChange={(e) => onMonthChange(Number(e.target.value))}
                                className="px-3 py-1.5 bg-gray-50 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-blue-500"
                            >
                                {MESES.map((mes, index) => (
                                    <option key={index} value={index + 1}>{mes}</option>
                                ))}
                            </select>
                            <select
                                value={currentYear}
                                onChange={(e) => onYearChange(Number(e.target.value))}
                                className="px-3 py-1.5 bg-gray-50 border border-gray-200 rounded-lg text-sm outline-none focus:ring-2 focus:ring-blue-500"
                            >
                                <option value={2025}>2025</option>
                                <option value={2026}>2026</option>
                                <option value={2027}>2027</option>
                            </select>
                        </div>
                    )}
                </div>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">

                <div className="bg-white p-6 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between hover:shadow-md transition-shadow">
                    <div>
                        <p className="text-sm font-medium text-gray-500 mb-1">Total de Receitas</p>
                        <h3 className="text-2xl font-bold text-gray-800">
                            {formatCurrency(cashflow?.totalIncome)}
                        </h3>
                    </div>
                    <div className="h-12 w-12 bg-green-50 rounded-full flex items-center justify-center border border-green-100">
                        <ArrowUpCircle className="h-6 w-6 text-green-600" />
                    </div>
                </div>

                <div className="bg-white p-6 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between hover:shadow-md transition-shadow">
                    <div>
                        <p className="text-sm font-medium text-gray-500 mb-1">Total de Despesas</p>
                        <h3 className="text-2xl font-bold text-gray-800">
                            {formatCurrency(cashflow?.totalExpenses)}
                        </h3>
                    </div>
                    <div className="h-12 w-12 bg-red-50 rounded-full flex items-center justify-center border border-red-100">
                        <ArrowDownCircle className="h-6 w-6 text-red-600" />
                    </div>
                </div>

                <div className="bg-white p-6 rounded-xl border border-gray-100 shadow-sm flex items-center justify-between hover:shadow-md transition-shadow">
                    <div>
                        <p className="text-sm font-medium text-gray-500 mb-1">Saldo Líquido</p>
                        <h3 className={`text-2xl font-bold ${isPositive ? 'text-blue-600' : 'text-red-600'}`}>
                            {formatCurrency(cashflow?.netBalance)}
                        </h3>
                    </div>
                    <div className={`h-12 w-12 rounded-full flex items-center justify-center border ${isPositive ? 'bg-blue-50 border-blue-100' : 'bg-red-50 border-red-100'}`}>
                        <Wallet className={`h-6 w-6 ${isPositive ? 'text-blue-600' : 'text-red-600'}`} />
                    </div>
                </div>

            </div>
        </div>
    )
}