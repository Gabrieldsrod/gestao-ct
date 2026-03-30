import { useState, useEffect, useCallback } from 'react';
import type { Transaction } from '@/types/finances/Transaction';

export function useGetTransactions(page: number, month?: number, year?: number, type?: string, categoryId?: number) {
    const [transactions, setTransactions] = useState<Transaction[]>([]);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const API_URL = import.meta.env.VITE_API_URL;

    const fetchTransactions = useCallback(async () => {
        setIsLoading(true);
        setError(null);
        try {
            let url = `${API_URL}/v1/api/transactions?page=${page}&size=10`;

            if (month !== undefined && year !== undefined) {
                url += `&month=${month}&year=${year}`;
            }

            if (type && type !== 'ALL')
                url += `&type=${type}`;

            if (categoryId && categoryId > 0)
                url += `&categoryId=${categoryId}`;

            const res = await fetch(url);
            if (!res.ok)
                throw new Error('Erro ao buscar transações.');

            const data = await res.json();
            setTransactions(data.content);
            setTotalPages(data.page.totalPages);
            setTotalElements(data.page.totalElements);
        } catch (err: any) {
            setError(err.message || 'Erro de conexão');
        } finally {
            setIsLoading(false);
        }
    }, [page, month, year, type, categoryId, API_URL]);

    useEffect(() => {
        fetchTransactions();
    }, [fetchTransactions]);

    return { transactions, totalPages, totalElements, isLoading, error, refetchTransactions: fetchTransactions };
}