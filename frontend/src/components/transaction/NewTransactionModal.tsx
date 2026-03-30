import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogTrigger } from "@/components/ui/dialog"
import { Plus } from "lucide-react"
import { useForm, useWatch } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { transactionSchema, type TransactionFormValues } from "@/schemas/transactionSchemas"

interface NewTransactionModalProps {
    createTransaction: (data: any) => Promise<{ success: boolean; message?: string }>;
    categories: any[];
    refetchTransactions: () => Promise<void>;
    refetchCashflow: () => Promise<void>;
}

export function NewTransactionModal({ createTransaction, categories, refetchTransactions, refetchCashflow }: NewTransactionModalProps) {
    const [open, setOpen] = useState(false)
    const [isSaving, setIsSaving] = useState(false)
    const [apiError, setApiError] = useState<string | null>(null)

    const hoje = new Date().toISOString().split('T')[0];

    const { register, handleSubmit, control, reset, setValue, formState: { errors } } = useForm<TransactionFormValues>({
        resolver: zodResolver(transactionSchema),
        defaultValues: {
            transactionType: 'EXPENSE',
            amount: '',
            description: '',
            categoryId: 0,
            transactionDate: hoje,
            paymentMethod: 'PIX'
        }
    })

    const watchedType = useWatch({
        control,
        name: "transactionType"
    });

    const filteredCategories = categories.filter(c => c.type === watchedType);

    useEffect(() => {
        setValue("categoryId", 0);
    }, [watchedType, setValue]);

    const onSubmit = async (data: TransactionFormValues) => {
        setIsSaving(true);
        setApiError(null);

        const numericAmount = parseFloat(data.amount.replace(',', '.'));

        const payload = {
            description: data.description,
            amount: numericAmount,
            transactionType: data.transactionType,
            paymentMethod: data.paymentMethod,
            transactionDate: data.transactionDate,
            categoryId: data.categoryId
        };

        const result = await createTransaction(payload);

        if (result.success) {
            await Promise.all([
                refetchTransactions(),
                refetchCashflow()
            ]);
            reset();
            setOpen(false);
        } else {
            setApiError(result.message || "Erro ao salvar transação");
        }
        setIsSaving(false);
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                <button className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors shadow-sm">
                    <Plus className="w-4 h-4" /> Nova Transação
                </button>
            </DialogTrigger>

            <DialogContent className="sm:max-w-md bg-white">
                <DialogHeader>
                    <DialogTitle className="text-xl font-bold text-gray-800">Nova Transação</DialogTitle>
                    <DialogDescription>Lance uma nova entrada ou saída no fluxo de caixa.</DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 mt-2">
                    {apiError && <div className="text-red-500 text-xs bg-red-50 border border-red-100 p-2 rounded-md">{apiError}</div>}

                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-1">
                            <label className="text-xs font-medium text-gray-700">Tipo *</label>
                            <select
                                {...register("transactionType")}
                                className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.transactionType ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                            >
                                <option value="EXPENSE">Saída</option>
                                <option value="INCOME">Entrada</option>
                            </select>
                            {errors.transactionType && <p className="text-red-500 text-xs">{errors.transactionType.message}</p>}
                        </div>

                        <div className="space-y-1">
                            <label className="text-xs font-medium text-gray-700">Valor (R$) *</label>
                            <input
                                {...register("amount")}
                                type="text"
                                inputMode="decimal"
                                className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.amount ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                                placeholder="Ex: 150,00"
                            />
                            {errors.amount && <p className="text-red-500 text-xs">{errors.amount.message}</p>}
                        </div>
                    </div>

                    <div className="space-y-1">
                        <label className="text-xs font-medium text-gray-700">Descrição *</label>
                        <input
                            {...register("description")}
                            type="text"
                            className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.description ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                            placeholder="Ex: Compra de Tatame, Conta de Luz..."
                        />
                        {errors.description && <p className="text-red-500 text-xs">{errors.description.message}</p>}
                    </div>

                    <div className="space-y-1">
                        <label className="text-xs font-medium text-gray-700">Categoria *</label>
                        {/* AQUI ESTÁ A CORREÇÃO DA CATEGORIA: */}
                        <select
                            {...register("categoryId", { valueAsNumber: true })}
                            className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.categoryId ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                        >
                            <option value={0} disabled>Selecione uma categoria...</option>
                            {filteredCategories.map(cat => (
                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                            ))}
                        </select>
                        {errors.categoryId && <p className="text-red-500 text-xs">{errors.categoryId.message}</p>}
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-1">
                            <label className="text-xs font-medium text-gray-700">Data *</label>
                            <input
                                {...register("transactionDate")}
                                type="date"
                                className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.transactionDate ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                            />
                            {errors.transactionDate && <p className="text-red-500 text-xs">{errors.transactionDate.message}</p>}
                        </div>

                        <div className="space-y-1">
                            <label className="text-xs font-medium text-gray-700">Forma de Pagamento *</label>
                            <select
                                {...register("paymentMethod")}
                                className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.paymentMethod ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                            >
                                <option value="PIX">Pix</option>
                                <option value="CREDIT_CARD">Cartão de Crédito</option>
                                <option value="DEBIT_CARD">Cartão de Débito</option>
                                <option value="CASH">Dinheiro</option>
                                <option value="SLIP">Boleto</option>
                            </select>
                            {errors.paymentMethod && <p className="text-red-500 text-xs">{errors.paymentMethod.message}</p>}
                        </div>
                    </div>

                    <div className="pt-4 flex justify-end">
                        <button disabled={isSaving} type="submit"
                            className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-6 py-2 rounded-lg font-medium transition-colors"
                        >
                            {isSaving ? 'A Salvar...' : 'Registrar Transação'}
                        </button>
                    </div>
                </form>
            </DialogContent>
        </Dialog>
    )
}