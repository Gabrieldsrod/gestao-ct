import { useState } from "react";
import { type MemberFormValues } from "../../schemas/memberSchema";

export function useCreateMember() {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const createMember = async (data: MemberFormValues, isCouplePlan: boolean | undefined) => {
    setIsLoading(true);
    setError(null);

    try {
      // 1. Salvar o Titular
      const holderResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: data.name, email: data.email, whatsapp: data.whatsapp,
          birthDate: data.birthDate, planId: data.planId, holderId: null
        }),
      });

      if (!holderResponse.ok) throw new Error('Erro ao cadastrar titular');
      const holder = await holderResponse.json();

      // 2. Salvar o Dependente (Se for casal)
      if (isCouplePlan) {
        const dependentResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            name: data.dependentName, email: data.dependentEmail, whatsapp: data.dependentWhatsapp,
            birthDate: data.dependentBirthDate, planId: data.planId, holderId: holder.id
          }),
        });

        if (!dependentResponse.ok) throw new Error('Erro ao cadastrar dependente');
      }

      return true; // Retorna sucesso!
    } catch (err: any) {
      console.error(err);
      setError(err.message || 'Erro inesperado ao salvar.');
      return false; // Retorna falha
    } finally {
      setIsLoading(false);
    }
  };

  return { createMember, isLoading, error };
}