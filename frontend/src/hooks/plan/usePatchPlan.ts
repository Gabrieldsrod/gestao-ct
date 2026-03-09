import { useState, useEffect, useCallback } from 'react';
import { type Plan } from '../../types/plan/Plan';

export function usePlan(planId: number) {
  const [plan, setPlan] = useState<Plan | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const API_URL = import.meta.env.VITE_API_URL;

  const fetchPlan = useCallback(async () => {
    try {
      setIsLoading(true);
      setError(null);
      const response = await fetch(`${API_URL}/v1/api/plans/${planId}`);
      
      if (!response.ok) throw new Error('Falha ao buscar os dados do plano.');
      
      const data = await response.json();
      setPlan(data);
    } catch (err: any) {
      setError(err.message || 'Erro de conexão');
    } finally {
      setIsLoading(false);
    }
  }, [planId, API_URL]);

  useEffect(() => {
    if (planId) fetchPlan();
  }, [fetchPlan, planId]);

  // Envia a edição (PATCH)
  const updatePlan = async (name: string, price: number) => {
    try {
      setIsSaving(true);
      setError(null);
      
      const response = await fetch(`${API_URL}/v1/api/plans/${planId}`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, price }),
      });

      if (!response.ok) throw new Error('Erro ao salvar as alterações do plano.');

      return true; 
    } catch (err: any) {
      setError(err.message || 'Erro ao salvar');
      return false;
    } finally {
      setIsSaving(false);
    }
  };

  return { plan, isLoading, isSaving, error, updatePlan };
}