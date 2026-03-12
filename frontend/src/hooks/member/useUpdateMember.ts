import { useState } from "react";
import { type MemberFormValues } from "../../schemas/memberSchema";

export function useUpdateMember() {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const updateMember = async (memberId: number, data: MemberFormValues, isUpgradingToCouple: boolean | undefined) => {
        setIsLoading(true);
        setError(null);

        try {
            const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members/${memberId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ ...data, isUpgradingToCouple }),
            });

            if (!response.ok) {
                const errorData = await response.json().catch(() => null);
                return { success: false, message: errorData?.message || "Erro ao atualizar." };
            }

            return { success: true };
        } catch (err: any) {
            console.error(err);
            setError(err.message || 'Erro inesperado ao atualizar.');
            return { success: false, message: err.message || 'Erro inesperado ao atualizar.' };
        } finally {
            setIsLoading(false);
        }
    };

    return { updateMember, isLoading, error };
}