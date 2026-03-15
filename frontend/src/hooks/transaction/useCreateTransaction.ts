import { useState } from 'react';
import type { CreateTransactionPayload } from '@/types/finances/CreateTransactionPayload';

export function useCreateTransaction() {
    const [isSaving, setIsSaving] = useState(false);

    const API_URL = import.meta.env.VITE_API_URL;

    const createTransaction = async (data: CreateTransactionPayload) => {
        setIsSaving(true);
        try {
            const response = await fetch(`${API_URL}/v1/api/transactions`, {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => null);
                throw new Error(errorData?.message || 'Falha ao criar transação.');
            }

            return { success: true };
        } catch (err: any) {
            return { success: false, message: err.message };
        } finally {
            setIsSaving(false);
        }
    };

    return { createTransaction, isSaving };
}