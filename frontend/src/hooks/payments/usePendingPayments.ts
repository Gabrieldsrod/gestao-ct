import { useState, useEffect } from 'react';

export function usePendingPayments(page = 0, size = 10) {
    const [payments, setPayments] = useState<any[]>([]);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const controller = new AbortController();

        async function fetchPayments() {
            try {
                setIsLoading(true);
                const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/payments/pending?page=${page}&size=${size}`, {
                    signal: controller.signal
                });

                if (!response.ok) throw new Error("Erro ao buscar pagamentos");

                const data = await response.json();

                if (data && Array.isArray(data.content)) {
                    setPayments(data.content);
                    setTotalPages(data.page?.totalPages || 0);
                    setTotalElements(data.page?.totalElements || 0);
                } else {
                    setPayments([]);
                }
            } catch (err: any) {
                if (err.name !== 'AbortError') setError(err.message);
            } finally {
                setIsLoading(false);
            }
        }

        fetchPayments();
        return () => controller.abort();
    }, [page, size]);

    return { payments, totalPages, totalElements, isLoading, error };
}