import { useState, useCallback } from "react";
import { type PlanCreate } from "../../types/plan/Plan";

export function useCreatePlan(onSuccess?: () => void) {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const createPlan = useCallback(async (planData: PlanCreate) => {
        setIsLoading(true);
        setError(null);
        try {
            const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/plans`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(planData),
            });
            if (!response.ok) throw new Error('Erro ao criar o plano.');
            if (onSuccess) onSuccess();
        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message);
            } else {
                setError('Erro de conexão');
            }
        } finally {
            setIsLoading(false);
        }
    }, [onSuccess]);

    return { createPlan, isLoading, error };
}

