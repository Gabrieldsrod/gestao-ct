import { useState, useEffect } from 'react';
import { type DashboardSummary } from '../../types/dashboard/DashboardSummary';

export function useDashboard() {
    const [summary, setSummary] = useState<DashboardSummary | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const controller = new AbortController();

        async function fetchSummary() {
            try {
                setIsLoading(true);
                setError(null);

                const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/dashboard/summary`, {
                    signal: controller.signal,
                    headers: { 'Content-Type': 'application/json' },
                });

                if (!response.ok) {
                    throw new Error(`Falha ao carregar o dashboard: ${response.status}`);
                }

                const data = await response.json();
                setSummary(data);

            } catch (err: any) {
                if (err.name !== 'AbortError') setError(err.message || 'Erro de conexão');
            } finally {
                setIsLoading(false);
            }
        }

        fetchSummary();

        return () => controller.abort();
    }, []);

    return { summary, isLoading, error };
}