import { useState } from "react";
import type { UpdatePaymentFee } from "@/types/fee/Fee";

export function useUpdatePaymentFee() {
    const [isUpdating, setIsUpdating] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const API_URL = import.meta.env.VITE_API_URL;

    const updateFee =  async (id: number, data: UpdatePaymentFee) => {
        setIsUpdating(true);
        setError(null);
    
        try {
            const res = await fetch(`${API_URL}/v1/api/fees/${id}`, {
                method: 'PATCH',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!res.ok) {
                const errorData = await res.json().catch(() => null);
                throw new Error(errorData?.message || 'Erro ao atualizar a taxa de pagamento.');   
            }

            return { success: true };

        } catch (err: unknown) {
            if (err instanceof Error) {
                setError(err.message);
            } else {
                setError('Erro de conexão ao atualizar taxa.');
            }

            return { success: false, error: error || 'Erro desconhecido.' };
        } finally {
            setIsUpdating(false);
        }
    };

    return { updateFee, isUpdating, error };
}