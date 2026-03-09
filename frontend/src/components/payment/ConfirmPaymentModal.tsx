import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { useConfirmPayment } from "../../hooks/payment/useConfirmPayment" 

interface ConfirmPaymentModalProps {
  paymentId: number;
  memberName: string;
  paymentValue: number;
}

export function ConfirmPaymentModal({ paymentId, memberName, paymentValue }: ConfirmPaymentModalProps) {
  const [open, setOpen] = useState(false)
  const [method, setMethod] = useState("PIX")
  const { confirmPayment, isConfirming } = useConfirmPayment()

  const formattedValue = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(paymentValue)

  const handleConfirm = async () => {
    const success = await confirmPayment(paymentId, method);
    
    if (success) {
      setOpen(false)
      window.location.reload() 
    } else {
      alert("Erro ao confirmar pagamento. Verifique o console.")
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button size="sm" className="bg-orange-500 hover:bg-orange-600 text-white">
          Baixar Mensalidade
        </Button>
      </DialogTrigger>

      <DialogContent className="sm:max-w-100 bg-white">
        <DialogHeader>
          <DialogTitle className="text-lg font-bold text-gray-800">Confirmar Pagamento</DialogTitle>
        </DialogHeader>
        
        <div className="space-y-4 py-4">
          <div className="bg-gray-50 p-3 rounded-md border border-gray-100">
            <p className="text-sm text-gray-600">Aluno: <strong className="text-gray-800">{memberName}</strong></p>
            <p className="text-sm text-gray-600">Valor: <strong className="text-gray-800">{formattedValue}</strong></p>
          </div>

          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">Método de Pagamento</label>
            <select 
              value={method} 
              onChange={(e) => setMethod(e.target.value)}
              className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm"
            >
              <option value="PIX">Pix</option>
              <option value="CREDIT_CARD">Cartão de Crédito</option>
              <option value="DEBIT_CARD">Cartão de Débito</option>
              <option value="CASH">Dinheiro</option>
            </select>
          </div>

          <Button 
            className="w-full bg-green-600 hover:bg-green-700 text-white mt-2" 
            onClick={handleConfirm}
            disabled={isConfirming}
          >
            {isConfirming ? "Processando..." : "Confirmar Recebimento"}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  )
}