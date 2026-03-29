import { useState } from 'react';
import { type Plan } from '../../types/plan/Plan';

export function usePatchPlan(planId: number) {
  const [plan] = useState<Plan | null>(null);
  const [isLoading] = useState(true);
  const [isSaving, setIsSaving] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const API_URL = import.meta.env.VITE_API_URL;

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
    } catch (err: unknown) {
      setError((err as Error).message || 'Erro ao salvar');
      return false;
    } finally {
      setIsSaving(false);
    }
  };

  return { plan, isLoading, isSaving, error, updatePlan };
}