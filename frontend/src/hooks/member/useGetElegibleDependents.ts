import { useEffect, useState } from "react";
import type { Dependent } from "@/types/member/Dependent";

export function useGetEligibleDependents(currentMemberId?: number, searchTerm = '') {
    const [eligibleDependents, setEligibleDependents] = useState<Dependent[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const controller = new AbortController();

        async function fetchEligibleDependents() {
            setIsLoading(true);
            setError(null);

            try {
                const baseUrl = `${import.meta.env.VITE_API_URL}/v1/api/members/eligible-dependents`;
                const url = new URL(baseUrl);
                
                if (searchTerm) {
                    url.searchParams.append('name', searchTerm);
                }
                
                if (currentMemberId) {
                    url.searchParams.append('excludeId', currentMemberId.toString());
                }

                const response = await fetch(url.toString(), {
                    signal: controller.signal,
                    headers: { 'Content-Type': 'application/json' },
                });

                if (!response.ok) {
                    throw new Error(`Erro ao buscar dependentes: ${response.status}`);
                }

                const data = await response.json();
                setEligibleDependents(data);

            } catch (err) {
                if (err instanceof Error) {
                    if (err.name === 'AbortError') return;
                    setError(err.message);
                } else {
                    setError('Erro de conexão ao buscar dependentes.');
                }
                setEligibleDependents([]); 
            } finally {
                setIsLoading(false);
            }
        }

        fetchEligibleDependents();

        return () => controller.abort();
        
    }, [currentMemberId, searchTerm]);

    return { eligibleDependents, isLoading, error };
}