import { useState, useEffect, useCallback } from 'react';
import { type Plan } from '../../types/plan/Plan';

export function useGetPlans() {
  const [plans, setPlans] = useState<Plan[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const fetchPlans = useCallback(async () => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/plans`);
      if (!response.ok) throw new Error('Erro ao carregar os planos.');
      
      const data = await response.json();
      setPlans(data);
    } catch (err: any) {
      setError(err.message || 'Erro de conexão');
    } finally {
      setIsLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchPlans();
  }, [fetchPlans]);

  return { plans, isLoading, error, refetch: fetchPlans };
}