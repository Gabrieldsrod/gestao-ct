import { useState } from 'react';

export function useInactivateMember() {
  const [isLoading, setIsLoading] = useState(false);

  const inactivateMember = async (memberId: number) => {
    setIsLoading(true);
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members/${memberId}/inactivate`, {
        method: 'PATCH',
        headers: { 'Content-Type': 'application/json' },
      });

      if (!response.ok) {
        const errorData = await response.json().catch(() => null);
        throw new Error(errorData?.message || "Erro ao inativar aluno. Verifique as regras de negócio.");
      }
      
      return { success: true };
    } catch (error: any) {
      console.error(error);
      return { success: false, message: error.message };
    } finally {
      setIsLoading(false);
    }
  };

  return { inactivateMember, isLoading };
}