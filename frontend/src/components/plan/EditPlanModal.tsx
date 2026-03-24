import { useState } from "react"
import { usePatchPlan } from "@/hooks/plan/usePatchPlan"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogTrigger } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Edit } from "lucide-react"
import { type Plan } from "../../types/plan/Plan"

interface EditPlanModalProps {
    plan: Plan;
    onSaveSuccess: () => void;
}

export function EditPlanModal({ plan, onSaveSuccess }: EditPlanModalProps) {
    const [open, setOpen] = useState(false)
    const [name, setName] = useState(plan.name)
    const [price, setPrice] = useState<string | number>(Number(plan.price).toFixed(2))

    const { isSaving, error, updatePlan } = usePatchPlan(plan.id);

    const handleSave = async (e: React.ChangeEvent<HTMLFormElement>) => {
        e.preventDefault();
        const numericPrice = typeof price === 'string' ? parseFloat(price) : price;
        const success = await updatePlan(name, numericPrice);
        if (success) {
            setOpen(false);
            onSaveSuccess();
        }
    }

    return (
        <Dialog open={open} onOpenChange={setOpen}>
            <DialogTrigger asChild>
                {/* Botão Gatilho idêntico ao dos Alunos */}
                <Button variant="ghost" size="icon" className="h-8 w-8 text-blue-600 hover:text-blue-800 hover:bg-blue-50">
                    <Edit className="h-4 w-4" />
                </Button>
            </DialogTrigger>

            <DialogContent aria-describedby="edit-plan-dialog" className="sm:max-w-md max-h-[90vh] overflow-y-auto bg-white">
                <DialogHeader>
                    {/* Título com a mesma tipografia */}
                    <DialogTitle className="text-xl font-bold text-gray-800">Editar Plano</DialogTitle>
                    <DialogDescription id="edit-plan-dialog" className="sr-only">
                        Altere os dados do plano do CT e clique em salvar.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSave} className="space-y-4 mt-2">

                    {error && (
                        <div className="text-red-500 text-xs bg-red-50 border border-red-100 p-2 rounded-md">
                            {error}
                        </div>
                    )}

                    {/* SESSÃO DO PLANO - Box cinza com a mesma estrutura */}
                    <div className="bg-gray-50 p-4 rounded-lg border border-gray-100">
                        <h3 className="font-semibold text-gray-700 mb-3 text-sm">Dados do Plano</h3>

                        <div className="space-y-3">
                            <div>
                                <label className="block text-xs font-medium text-gray-700 mb-1">Nome do Plano *</label>
                                <input
                                    type="text"
                                    required
                                    value={name}
                                    onChange={(e) => setName(e.target.value)}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm outline-none focus:ring-2 focus:ring-blue-500"
                                />
                            </div>

                            <div>
                                <label className="block text-xs font-medium text-gray-700 mb-1">Valor Mensal (R$) *</label>
                                <input
                                    type="number"
                                    step="0.01"
                                    required
                                    value={price}
                                    onChange={(e) => setPrice(e.target.value)}
                                    className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm outline-none focus:ring-2 focus:ring-blue-500"
                                />
                            </div>
                        </div>
                    </div>

                    {/* SESSÃO DO BOTÃO - Idêntica ao dos alunos */}
                    <div className="pt-2 flex justify-end">
                        <button
                            disabled={isSaving}
                            type="submit"
                            className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-6 py-2 rounded-lg font-medium transition-colors"
                        >
                            {isSaving ? 'A Salvar...' : 'Salvar Alterações'}
                        </button>
                    </div>
                </form>
            </DialogContent>
        </Dialog>
    )
}