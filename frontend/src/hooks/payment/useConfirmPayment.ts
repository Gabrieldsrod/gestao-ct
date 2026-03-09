import { useState } from 'react';

export function useConfirmPayment() {
  const [isConfirming, setIsConfirming] = useState(false);

  const confirmPayment = async (paymentId: number, method: string) => {
    setIsConfirming(true);
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/payments/${paymentId}/register`, {
        method: 'POST', 
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ paymentMethod: method }) 
      });

      if (!response.ok) throw new Error("Erro ao confirmar pagamento");
      
      return true; // Sucesso
    } catch (error) {
      console.error(error);
      return false; // Falha
    } finally {
      setIsConfirming(false);
    }
  };

  return { confirmPayment, isConfirming };
}