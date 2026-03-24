import { useState } from "react";
import { useCreatePlan } from "@/hooks/plan/useCreatePlan";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogTrigger } from "@/components/ui/dialog";
import { Plus } from "lucide-react";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import * as z from "zod";

interface CreatePlanModalProps {
    onCreateSuccess: () => void;
}

const createPlanSchema = z.object({
    name: z.string().min(3, "O nome do plano deve ter pelo menos 3 caracteres."),
    price: z.number({ message: "Insira um valor numérico válido." })
        .positive("O valor deve ser maior que zero.")
});

type CreatePlanFormData = z.infer<typeof createPlanSchema>;

export function CreatePlanModal({ onCreateSuccess }: CreatePlanModalProps) {
    const [open, setOpen] = useState(false);

    const { register, handleSubmit, formState: { errors }, reset } = useForm<CreatePlanFormData>({
        resolver: zodResolver(createPlanSchema),
        defaultValues: {
            name: "",
        }
    });

    const { createPlan, isLoading: isSaving, error: apiError } = useCreatePlan(() => {
        setOpen(false);
        reset();
        onCreateSuccess();
    });

    const onSubmit = async (data: CreatePlanFormData) => {
        await createPlan(data);
    };

    const handleOpenChange = (isOpen: boolean) => {
        setOpen(isOpen);
        if (!isOpen) reset();
    };

    return (
        <Dialog open={open} onOpenChange={handleOpenChange}>
            <DialogTrigger asChild>
                <button className="flex items-center gap-2 px-4 py-2 text-sm font-medium text-white bg-blue-600 rounded-lg hover:bg-blue-700 transition-colors shadow-sm">
                    <Plus className="w-4 h-4" /> Novo Plano
                </button>
            </DialogTrigger>

            <DialogContent aria-describedby="create-plan-dialog" className="sm:max-w-md max-h-[90vh] overflow-y-auto bg-white">
                <DialogHeader>
                    <DialogTitle className="text-xl font-bold text-gray-800">Criar Novo Plano</DialogTitle>
                    <DialogDescription id="create-plan-dialog" className="sr-only">
                        Preencha os dados do novo plano do CT e clique em salvar.
                    </DialogDescription>
                </DialogHeader>

                <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 mt-2">
                    {apiError && (
                        <div className="text-red-500 text-xs bg-red-50 border border-red-100 p-2 rounded-md">
                            {apiError}
                        </div>
                    )}

                    <div className="bg-gray-50 p-4 rounded-lg border border-gray-100">
                        <h3 className="font-semibold text-gray-700 mb-3 text-sm">Dados do Plano</h3>
                        <div className="space-y-3">
                            <div>
                                <label className="block text-xs font-medium text-gray-700 mb-1">Nome do Plano *</label>
                                <input
                                    type="text"
                                    {...register("name")}
                                    className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.name ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                                />
                                {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name.message}</p>}
                            </div>
                            <div>
                                <label className="block text-xs font-medium text-gray-700 mb-1">Valor Mensal (R$) *</label>
                                <input
                                    type="number"
                                    step="0.01"
                                    {...register("price", { valueAsNumber: true })}
                                    className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.price ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                                />
                                {errors.price && <p className="text-red-500 text-xs mt-1">{errors.price.message}</p>}
                            </div>
                        </div>
                    </div>

                    <div className="pt-2 flex justify-end">
                        <button
                            disabled={isSaving}
                            type="submit"
                            className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-6 py-2 rounded-lg font-medium transition-colors"
                        >
                            {isSaving ? 'A guardar...' : 'Criar Plano'}
                        </button>
                    </div>
                </form>
            </DialogContent>
        </Dialog>
    );
}