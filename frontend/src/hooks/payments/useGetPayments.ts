import { useState, useEffect } from 'react';
import { type Payment } from '../../types/Payment';

export function useGetPayments(page = 0, size = 10, status = 'PENDING') {
    const [payments, setPayments] = useState<Payment[]>([]);
    const [totalPages, setTotalPages] = useState(0);
    const [totalElements, setTotalElements] = useState(0);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const controller = new AbortController();

        async function fetchPayments() {
            try {
                setIsLoading(true);

                let url = `${import.meta.env.VITE_API_URL}/v1/api/payments?page=${page}&size=${size}`;

                if (status && status !== 'ALL') {
                    url += `&status=${status}`;
                }

                const response = await fetch(url, {
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
    }, [page, size, status]);

    return { payments, totalPages, totalElements, isLoading, error };
}