import { useState, useEffect, useCallback } from 'react';
import type { Transaction } from '@/types/finances/Transaction';
import type { CashFlow } from '@/types/finances/CashFlow';
import type { CreateTransactionPayload } from '@/types/finances/CreateTransactionPayload';


export function useTransactions(page: number, month: number, year: number) {
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [cashflow, setCashflow] = useState<CashFlow | null>(null);

    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);

    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const API_URL = import.meta.env.VITE_API_URL;

    const fetchData = useCallback(async () => {
        setIsLoading(true);
        setError(null);
        try {
            const transRes = await fetch(`${API_URL}/v1/api/transactions?month=${month}&year=${year}&page=${page}&size=10`);
            if (!transRes.ok) throw new Error('Erro ao buscar transações.');
            const transData = await transRes.json();

            const cashRes = await fetch(`${API_URL}/v1/api/transactions/cashflow`);
            if (!cashRes.ok) throw new Error('Erro ao buscar fluxo de caixa.');
            const cashData = await cashRes.json();

            setTransactions(transData.content);
            setTotalPages(transData.page.totalPages);
            setTotalElements(transData.page.totalElements);
            setCashflow(cashData);
        } catch (err: any) {
            setError(err.message || 'Erro de conexão');
        } finally {
            setIsLoading(false);
        }
    }, [page, month, year, API_URL]);

    useEffect(() => {
        fetchData();
    }, [fetchData]);

    const createTransaction = async (data: CreateTransactionPayload) => {
        try {
            const response = await fetch(`${API_URL}/v1/api/transactions`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data),
            });

            if (!response.ok) throw new Error('Falha ao criar transação.');

            await fetchData();
            return { success: true };
        } catch (err: any) {
            return { success: false, message: err.message };
        }
    };

    return { transactions, cashflow, totalPages, totalElements, isLoading, error, refetch: fetchData, createTransaction };
}