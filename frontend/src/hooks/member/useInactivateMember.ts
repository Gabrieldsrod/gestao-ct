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

      const data = await response.json().catch(() => null);

      if (!response.ok) {
        return { success: false, message: data?.message || data?.error || 'Erro ao inativar o aluno.' };
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