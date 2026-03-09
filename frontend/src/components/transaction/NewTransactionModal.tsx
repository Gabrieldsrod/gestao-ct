import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogTrigger } from "@/components/ui/dialog"
import { Plus } from "lucide-react"
import { useCategories } from "@/hooks/category/useCategories"

interface NewTransactionModalProps {
    createTransaction: (data: any) => Promise<{ success: boolean; message?: string }>;
}

export function NewTransactionModal({ createTransaction }: NewTransactionModalProps) {
    const [open, setOpen] = useState(false)
    const { categories, refetch } = useCategories()

    useEffect(() => {
        if (open) {
            refetch();
        }
    }, [open, refetch]);

    const hoje = new Date().toISOString().split('T')[0];

    const [description, setDescription] = useState("")
    const [amount, setAmount] = useState<string | number>("")
    const [type, setType] = useState<'INCOME' | 'EXPENSE'>('EXPENSE')
    const [date, setDate] = useState(hoje)
    const [paymentMethod, setPaymentMethod] = useState("PIX")
    const [categoryId, setCategoryId] = useState("")

    const [isSaving, setIsSaving] = useState(false)
    const [error, setError] = useState<string | null>(null)

    const filteredCategories = categories.filter(c => c.type === type);

    const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
        e.preventDefault();
        if (!categoryId) {
            setError("Por favor, selecione uma categoria.");
            return;
        }

        setIsSaving(true);
        setError(null);

        const stringAmount = String(amount).replace(',', '.');
        const numericAmount = parseFloat(stringAmount);

        const result = await createTransaction({
            description,
            amount: numericAmount,
            transactionType: type,
            paymentMethod,
            transactionDate: date,
            categoryId: Number(categoryId)
        });

        if (result.success) {
            setDescription("");
            setAmount("");
            setCategoryId("");
            setOpen(false);
        } else {
            setError(result.message || "Erro ao salvar transação");
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
                    <DialogDescription>
                        Lance uma nova entrada ou saída no fluxo de caixa.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit} className="space-y-4 mt-2">
                    {error && <div className="text-red-500 text-xs bg-red-50 border border-red-100 p-2 rounded-md">{error}</div>}

                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-1">
                            <label className="text-xs font-medium text-gray-700">Tipo *</label>
                            <select
                                value={type}
                                onChange={(e) => {
                                    setType(e.target.value as 'INCOME' | 'EXPENSE');
                                    setCategoryId("");
                                }}
                                className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm outline-none focus:ring-2 focus:ring-blue-500"
                            >
                                <option value="EXPENSE">Saída</option>
                                <option value="INCOME">Entrada</option>
                            </select>
                        </div>

                        <div className="space-y-1">
                            <label className="text-xs font-medium text-gray-700">Valor (R$) *</label>
                            <input
                                type="text"
                                inputMode="decimal"
                                required
                                value={amount}
                                onChange={(e) => setAmount(e.target.value)}
                                className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm outline-none focus:ring-2 focus:ring-blue-500"
                                placeholder="Ex: 150,00 ou 150.00"
                            />
                        </div>
                    </div>

                    <div className="space-y-1">
                        <label className="text-xs font-medium text-gray-700">Descrição *</label>
                        <input type="text" required value={description} onChange={(e) => setDescription(e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm outline-none focus:ring-2 focus:ring-blue-500"
                            placeholder="Ex: Compra de Equipamento, Conta de Luz..."
                        />
                    </div>

                    <div className="space-y-1">
                        <label className="text-xs font-medium text-gray-700">Categoria *</label>
                        <select required value={categoryId} onChange={(e) => setCategoryId(e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm outline-none focus:ring-2 focus:ring-blue-500"
                        >
                            <option value="" disabled>Selecione uma categoria...</option>
                            {filteredCategories.map(cat => (
                                <option key={cat.id} value={cat.id}>{cat.name}</option>
                            ))}
                        </select>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div className="space-y-1">
                            <label className="text-xs font-medium text-gray-700">Data *</label>
                            <input type="date" required value={date} onChange={(e) => setDate(e.target.value)}
                                className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm outline-none focus:ring-2 focus:ring-blue-500"
                            />
                        </div>

                        <div className="space-y-1">
                            <label className="text-xs font-medium text-gray-700">Forma de Pagamento *</label>
                            <select required value={paymentMethod} onChange={(e) => setPaymentMethod(e.target.value)}
                                className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm outline-none focus:ring-2 focus:ring-blue-500"
                            >
                                <option value="PIX">Pix</option>
                                <option value="CREDIT_CARD">Cartão de Crédito</option>
                                <option value="DEBIT_CARD">Cartão de Débito</option>
                                <option value="CASH">Dinheiro</option>
                                <option value="SLIP">Boleto</option>
                            </select>
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