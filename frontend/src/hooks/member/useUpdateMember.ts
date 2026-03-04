import { useState } from "react";
import { type MemberFormValues } from "../../schemas/memberSchema";

export function useUpdateMember() {
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const updateMember = async (memberId: number, data: MemberFormValues, isUpgradingToCouple: boolean | undefined) => {
        setIsLoading(true);
        setError(null);

        try {
            // 1. Atualiza o Titular (PUT)
            const updateResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members/${memberId}`, {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({
                    name: data.name, email: data.email, whatsapp: data.whatsapp,
                    birthDate: data.birthDate, planId: data.planId
                }),
            });

            if (!updateResponse.ok) throw new Error('Erro ao atualizar titular');

            // 2. Cria o novo Dependente caso seja um Upgrade (POST)
            if (isUpgradingToCouple) {
                const dependentResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({
                        name: data.dependentName, email: data.dependentEmail, whatsapp: data.dependentWhatsapp,
                        birthDate: data.dependentBirthDate, planId: data.planId, holderId: memberId
                    }),
                });
                if (!dependentResponse.ok) throw new Error('Erro ao cadastrar o novo dependente');
            }

            return true; // Sucesso
        } catch (err: any) {
            console.error(err);
            setError(err.message || 'Erro inesperado ao atualizar.');
            return false; // Falha
        } finally {
            setIsLoading(false);
        }
    };

    return { updateMember, isLoading, error };
}