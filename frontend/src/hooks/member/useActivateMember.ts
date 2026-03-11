import { useState } from 'react';

export function useActivateMember() {
  const [isLoading, setIsLoading] = useState(false);

  const activateMember = async (id: number) => {
    setIsLoading(true);
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members/${id}/activate`, {
        method: 'PATCH', 
        headers: { 'Content-Type': 'application/json' },
      });

      const data = await response.json().catch(() => null);

      if (!response.ok) {
        
        return { success: false, message: data?.message || data?.error || 'Erro ao ativar o aluno.' };
      }

      return { success: true, message: data?.message || 'Aluno ativado com sucesso.' };
    } catch (err: any) {
      return { success: false, message: err.message || 'Erro de conexão.' };
    } finally {
      setIsLoading(false);
    }
  };

  return { activateMember, isLoading };
}