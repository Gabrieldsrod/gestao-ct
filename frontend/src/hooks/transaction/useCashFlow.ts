import { useState, useEffect, useCallback } from 'react';
import type { CashFlow } from '@/types/finances/CashFlow';

export function useCashflow() {
    const [cashflow, setCashflow] = useState<CashFlow | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const API_URL = import.meta.env.VITE_API_URL;

    const fetchCashflow = useCallback(async () => {
        setIsLoading(true);
        setError(null);
        try {
            const res = await fetch(`${API_URL}/v1/api/transactions/cashflow`);
            
            if (!res.ok) throw new Error('Erro ao buscar fluxo de caixa.');
            
            const data = await res.json();
            setCashflow(data);
        } catch (err: any) {
            setError(err.message || 'Erro de conexão');
            console.error("Erro ao buscar fluxo de caixa", err);
        } finally {
            setIsLoading(false);
        }
    }, []);

    useEffect(() => {
        fetchCashflow();
    }, [fetchCashflow]);

    return { 
        cashflow, 
        isLoading, 
        error, 
        refetchCashflow: fetchCashflow 
    };
}