import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Settings, ChevronLeft, Edit2 } from "lucide-react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"

import { updateFeeSchema, type UpdateFeeFormValues } from "@/schemas/paymentFeeSchema"
import { useGetPaymentFees } from "@/hooks/fee/useGetPaymentFee" 
import { useUpdatePaymentFee } from "@/hooks/fee/useUpdatePaymentFee"
import type { PaymentFee } from "@/types/fee/Fee"

export function PaymentFeesConfigModal() {
    const [isOpen, setIsOpen] = useState(false);
    const [editingFee, setEditingFee] = useState<PaymentFee | null>(null);
    const [apiError, setApiError] = useState<string | null>(null);

    const { fees, isLoading, refetchFees } = useGetPaymentFees();
    const { updateFee, isUpdating } = useUpdatePaymentFee();

    const { register, handleSubmit, reset, formState: { errors } } = useForm<UpdateFeeFormValues>({
        resolver: zodResolver(updateFeeSchema)
    });

    const handleEditClick = (fee: PaymentFee) => {
        reset({
            percentageFee: fee.percentageFee,
            fixedFee: fee.fixedFee,
            daysToReceive: fee.daysToReceive
        });
        setApiError(null);
        setEditingFee(fee);
    }

    const onSubmit = async (data: UpdateFeeFormValues) => {
        if (!editingFee) return;
        
        setApiError(null);
        const result = await updateFee(editingFee.id, data);

        if (result.success) {
            await refetchFees();
            setEditingFee(null);
        } else {
            setApiError(result.error || "Erro ao atualizar a taxa.");
        }
    }

    const formatPaymentMethod = (method: string) => {
        const map: Record<string, string> = {
            'CASH': 'Dinheiro',
            'CREDIT_CARD': 'Cartão de Crédito',
            'DEBIT_CARD': 'Cartão de Débito',
            'PIX': 'Pix',
            'SLIP': 'Boleto'
        };
        return map[method] || method;
    }

    return (
        <Dialog open={isOpen} onOpenChange={(open) => {
            setIsOpen(open);
            if (!open) setEditingFee(null); 
        }}>
            <DialogTrigger asChild>
                <button className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-200 rounded-lg hover:bg-gray-50 transition-colors shadow-sm">
                    <Settings className="w-4 h-4" /> Configurar Taxas
                </button>
            </DialogTrigger>

            <DialogContent className="sm:max-w-md bg-white">
                <DialogHeader>
                    <DialogTitle className="text-xl font-bold text-gray-800 flex items-center gap-2">
                        {editingFee && (
                            <button onClick={() => setEditingFee(null)} className="p-1 hover:bg-gray-100 rounded-md transition-colors mr-1">
                                <ChevronLeft className="w-5 h-5 text-gray-600" />
                            </button>
                        )}
                        {editingFee ? `Editar ${formatPaymentMethod(editingFee.paymentMethod)}` : 'Taxas de Transação'}
                    </DialogTitle>
                </DialogHeader>

                <div className="mt-2">
                    {!editingFee && (
                        <div className="space-y-3 max-h-[60vh] overflow-y-auto pr-1">
                            {isLoading ? (
                                <p className="text-center text-gray-500 py-4 text-sm">A carregar taxas...</p>
                            ) : fees.map(fee => (
                                <div key={fee.id} className="flex items-center justify-between p-4 border border-gray-100 rounded-xl bg-gray-50/50 hover:bg-blue-50/30 transition-colors group">
                                    <div>
                                        <h4 className="font-semibold text-gray-800">{formatPaymentMethod(fee.paymentMethod)}</h4>
                                        <div className="flex flex-wrap gap-2 mt-2 text-xs font-medium text-gray-500">
                                            <span className="bg-white px-2 py-1 rounded-md border border-gray-200 shadow-sm">
                                                {fee.percentageFee}%
                                            </span>
                                            <span className="bg-white px-2 py-1 rounded-md border border-gray-200 shadow-sm">
                                                R$ {fee.fixedFee}
                                            </span>
                                            <span className="bg-white px-2 py-1 rounded-md border border-gray-200 shadow-sm">
                                                {fee.daysToReceive === 0 ? 'Cai na hora' : `${fee.daysToReceive} dias`}
                                            </span>
                                        </div>
                                    </div>
                                    <button 
                                        onClick={() => handleEditClick(fee)} 
                                        className="p-2 text-gray-400 group-hover:text-blue-600 bg-white rounded-lg border border-gray-200 shadow-sm hover:border-blue-200 transition-colors"
                                        title="Editar Taxa"
                                    >
                                        <Edit2 className="w-4 h-4" />
                                    </button>
                                </div>
                            ))}
                            <p className="text-xs text-gray-400 text-center mt-4">
                                Estes valores são descontados automaticamente ao registar um novo pagamento.
                            </p>
                        </div>
                    )}

                    {editingFee && (
                        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 animate-in fade-in slide-in-from-right-4 duration-300">
                            {apiError && (
                                <div className="text-red-500 text-xs bg-red-50 border border-red-100 p-2 rounded-md">
                                    {apiError}
                                </div>
                            )}

                            <div className="grid grid-cols-2 gap-4">
                                <div className="space-y-1">
                                    <label className="text-xs font-medium text-gray-700">Taxa Percentual (%) *</label>
                                    <input
                                        {...register("percentageFee", { valueAsNumber: true })}
                                        type="number"
                                        step="0.01"
                                        className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.percentageFee ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                                    />
                                    {errors.percentageFee && <p className="text-red-500 text-xs">{errors.percentageFee.message}</p>}
                                </div>

                                <div className="space-y-1">
                                    <label className="text-xs font-medium text-gray-700">Taxa Fixa (R$) *</label>
                                    <input
                                        {...register("fixedFee", { valueAsNumber: true })}
                                        type="number"
                                        step="0.01"
                                        className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.fixedFee ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                                    />
                                    {errors.fixedFee && <p className="text-red-500 text-xs">{errors.fixedFee.message}</p>}
                                </div>
                            </div>

                            <div className="space-y-1">
                                <label className="text-xs font-medium text-gray-700">Dias para Recebimento *</label>
                                <input
                                    {...register("daysToReceive", { valueAsNumber: true })}
                                    type="number"
                                    className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.daysToReceive ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                                />
                                <p className="text-xs text-gray-400">Ex: 0 para Pix, 30 para Crédito à vista.</p>
                                {errors.daysToReceive && <p className="text-red-500 text-xs">{errors.daysToReceive.message}</p>}
                            </div>

                            <div className="pt-4 flex justify-end">
                                <button 
                                    disabled={isUpdating} 
                                    type="submit"
                                    className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-6 py-2 rounded-lg font-medium transition-colors w-full sm:w-auto"
                                >
                                    {isUpdating ? 'A Guardar...' : 'Salvar Nova Taxa'}
                                </button>
                            </div>
                        </form>
                    )}
                </div>
            </DialogContent>
        </Dialog>
    )
}