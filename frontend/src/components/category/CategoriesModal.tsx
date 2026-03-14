import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogTrigger } from "@/components/ui/dialog"
import { Settings2, Plus, ArrowUpCircle, ArrowDownCircle } from "lucide-react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { categorySchema, type CategoryFormValues } from "@/schemas/transactionSchemas"

interface CategoriesModalProps {
    categories: any[];
    isLoading: boolean;
    createCategory: (name: string, type: "INCOME" | "EXPENSE") => Promise<{ success: boolean; message?: string }>;
}

export function CategoriesModal({ categories, isLoading, createCategory }: CategoriesModalProps) {
  const [open, setOpen] = useState(false)
  const [isSaving, setIsSaving] = useState(false)
  const [apiError, setApiError] = useState<string | null>(null)

  const { register, handleSubmit, reset, formState: { errors } } = useForm<CategoryFormValues>({
    resolver: zodResolver(categorySchema),
    defaultValues: {
      name: '',
      type: 'EXPENSE'
    }
  })

  const onSubmit = async (data: CategoryFormValues) => {
    setIsSaving(true);
    setApiError(null);

    const result = await createCategory(data.name, data.type);

    if (result.success) {
      reset();
    } else {
      setApiError(result.message || "Erro desconhecido");
    }
    setIsSaving(false);
  }

  const incomeCategories = categories.filter(c => c.type === 'INCOME');
  const expenseCategories = categories.filter(c => c.type === 'EXPENSE');

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <button className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-gray-700 bg-white border border-gray-300 rounded-lg hover:bg-gray-50 transition-colors shadow-sm">
          <Settings2 className="w-4 h-4" /> Categorias
        </button>
      </DialogTrigger>

      <DialogContent className="sm:max-w-md bg-white">
        <DialogHeader>
          <DialogTitle className="text-xl font-bold text-gray-800">Gerir Categorias</DialogTitle>
          <DialogDescription>Adicione novas categorias para classificar suas transações.</DialogDescription>
        </DialogHeader>

        <div className="space-y-6 mt-4">

          <form onSubmit={handleSubmit(onSubmit)} className="bg-gray-50 p-4 rounded-lg border border-gray-200 flex flex-col gap-2">
            <div className="flex gap-2 items-start">
              <div className="flex-1 space-y-1">
                <label className="text-xs font-medium text-gray-700">Nova Categoria</label>
                <input
                  {...register("name")}
                  placeholder="Ex: Conta de Luz"
                  className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.name ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                />
              </div>
              <div className="w-28 space-y-1">
                <label className="text-xs font-medium text-gray-700">Tipo</label>
                <select
                  {...register("type")}
                  className="w-full px-2 py-2 border border-gray-300 rounded-md text-sm outline-none focus:ring-2 focus:ring-blue-500"
                >
                  <option value="EXPENSE">Saída</option>
                  <option value="INCOME">Entrada</option>
                </select>
              </div>
              <div className="pt-5">
                <button
                  type="submit"
                  disabled={isSaving}
                  className="bg-gray-800 text-white p-2 rounded-md hover:bg-gray-700 transition-colors disabled:opacity-50"
                >
                  <Plus className="w-5 h-5" />
                </button>
              </div>
            </div>

            {/* Mensagens de erro do Zod ou da API */}
            {errors.name && <p className="text-red-500 text-xs">{errors.name.message}</p>}
            {apiError && <p className="text-red-500 text-xs">{apiError}</p>}
          </form>

          {/* LISTA DAS CATEGORIAS (Mantida exatamente igual) */}
          <div className="max-h-60 overflow-y-auto pr-2 space-y-4">
            {isLoading ? (
              <p className="text-center text-gray-500 text-sm py-4">A carregar...</p>
            ) : (
              <>
                <div>
                  <h4 className="text-xs font-semibold text-gray-500 uppercase flex items-center gap-1 mb-2">
                    <ArrowUpCircle className="w-3 h-3 text-green-500" /> Entradas
                  </h4>
                  <div className="flex flex-wrap gap-2">
                    {incomeCategories.length === 0 && <span className="text-xs text-gray-400">Nenhuma categoria</span>}
                    {incomeCategories.map(cat => (
                      <span key={cat.id} className="text-xs font-medium px-2 py-1 bg-green-50 text-green-700 border border-green-200 rounded-md">{cat.name}</span>
                    ))}
                  </div>
                </div>

                <div>
                  <h4 className="text-xs font-semibold text-gray-500 uppercase flex items-center gap-1 mb-2">
                    <ArrowDownCircle className="w-3 h-3 text-red-500" /> Saídas
                  </h4>
                  <div className="flex flex-wrap gap-2">
                    {expenseCategories.length === 0 && <span className="text-xs text-gray-400">Nenhuma categoria</span>}
                    {expenseCategories.map(cat => (
                      <span key={cat.id} className="text-xs font-medium px-2 py-1 bg-red-50 text-red-700 border border-red-200 rounded-md">{cat.name}</span>
                    ))}
                  </div>
                </div>
              </>
            )}
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}