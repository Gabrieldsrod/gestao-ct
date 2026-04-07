import { useState, useEffect, useCallback } from 'react';
import { type PaymentFee } from '@/types/fee/Fee'; 

export function useGetPaymentFees() {
    const [fees, setFees] = useState<PaymentFee[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const API_URL = import.meta.env.VITE_API_URL;

    const fetchFees = useCallback(async () => {
        setIsLoading(true);
        setError(null);
        try {
            const res = await fetch(`${API_URL}/v1/api/fees`);
            
            if (!res.ok) throw new Error('Erro ao buscar as taxas de pagamento.');

            const data = await res.json();
            
            setFees(data.content || data);
        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message);
            } else {
                setError('Erro de conexão ao buscar taxas.');
            }
        } finally {
            setIsLoading(false);
        }
    }, [API_URL]);

    useEffect(() => {
        fetchFees();
    }, [fetchFees]);

    return { fees, isLoading, error, refetchFees: fetchFees };
}