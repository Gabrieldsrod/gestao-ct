import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { useGetPlans } from "../../hooks/plan/useGetPlans"
import { EditPlanModal } from "./EditPlanModal"

export function PlansTable() {
    const { plans, isLoading, error, refetch } = useGetPlans();

    const formatCurrency = (value: number) => {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value);
    }

    return (
        <div className="flex flex-col gap-4">
            <div className="rounded-md border border-gray-100 bg-white flex  shadow-sm">
                
                {isLoading && plans.length === 0 ? (
                    <div className="p-8 text-center text-gray-500 flex-1 flex items-center justify-center">A carregar planos...</div>
                ) : error ? (
                    <div className="p-8 text-center text-red-500 flex-1 flex items-center justify-center">Erro: {error}</div>
                ) : plans.length === 0 ? (
                    <div className="p-8 text-center text-gray-500 flex-1 flex items-center justify-center">
                        Nenhum plano cadastrado.
                    </div>
                ) : (
                    <div className="flex-1">
                        <Table>
                            <TableHeader className="bg-gray-50/50">
                                <TableRow>
                                    <TableHead className="font-semibold text-gray-600">ID</TableHead>
                                    <TableHead className="font-semibold text-gray-600">Nome do Plano</TableHead>
                                    <TableHead className="font-semibold text-gray-600 text-right">Valor Mensal</TableHead>
                                    <TableHead className="text-right font-semibold text-gray-600">Ações</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {plans.map((plan) => (
                                    <TableRow key={plan.id} className="border-gray-100 hover:bg-gray-50/50 transition-colors">
                                        <TableCell className="text-gray-500 w-24">#{plan.id}</TableCell>
                                        <TableCell>
                                            <span className="font-medium text-gray-800">{plan.name}</span>
                                        </TableCell>
                                        <TableCell className="text-right text-gray-800 font-semibold">
                                            {formatCurrency(plan.price)}
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <div className="flex justify-end items-center gap-2">
                                                <EditPlanModal plan={plan} onSaveSuccess={refetch} />
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </div>
                )}
            </div>
        </div>
    )
}