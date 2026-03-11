import { useState } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogTrigger } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { UserMinus, UserCheck, AlertTriangle, Info } from "lucide-react"
import { useInactivateMember } from "../../hooks/member/useInactivateMember"
import { useActivateMember } from "../../hooks/member/useActivateMember"

interface MemberStatusModalProps {
    memberId: number;
    memberName: string;
    status: string;
}

export function MemberStatusModal({ memberId, memberName, status }: MemberStatusModalProps) {
    const [open, setOpen] = useState(false)
    const { inactivateMember, isLoading: isInactivating } = useInactivateMember()
    const { activateMember, isLoading: isActivating } = useActivateMember()
    const [apiError, setApiError] = useState<string | null>(null)

    const isInactive = status === 'INACTIVE'
    const isLoading = isInactivating || isActivating

    const handleConfirm = async () => {
        setApiError(null)
        
        const result = isInactive 
            ? await activateMember(memberId)
            : await inactivateMember(memberId)
            
        if (result.success) {
            setOpen(false)
            window.location.reload() // Recarrega a tabela para atualizar os dados
        } else {
            setApiError(result.message || "Ocorreu um erro na operação.")
        }
    }

    return (
        <Dialog open={open} onOpenChange={(val) => { setOpen(val); if (!val) setApiError(null); }}>
            <DialogTrigger asChild>
                {isInactive ? (
                    <Button 
                        variant="outline" 
                        size="icon" 
                        className="h-8 w-8 text-green-500 hover:text-green-700 hover:bg-green-50"
                        title="Reativar Aluno"
                    >
                        <UserCheck className="h-4 w-4" />
                    </Button>
                ) : (
                    <Button 
                        variant="outline" 
                        size="icon" 
                        className="h-8 w-8 text-red-500 hover:text-red-700 hover:bg-red-50"
                        title="Inativar Aluno"
                    >
                        <UserMinus className="h-4 w-4" />
                    </Button>
                )}
            </DialogTrigger>

            <DialogContent className="sm:max-w-md bg-white">
                <DialogHeader>
                    <DialogTitle className="flex items-center gap-2 text-xl font-bold text-gray-800">
                        {isInactive ? (
                            <><Info className="h-5 w-5 text-blue-500" /> Confirmar Reativação</>
                        ) : (
                            <><AlertTriangle className="h-5 w-5 text-red-500" /> Confirmar Inativação</>
                        )}
                    </DialogTitle>
                    <DialogDescription className="text-gray-600 pt-2">
                        {isInactive 
                            ? <>Tem certeza que deseja reativar o aluno <strong className="text-gray-800">{memberName}</strong>? Ele voltará a ter acesso ao CT e as cobranças mensais serão retomadas.</>
                            : <>Tem certeza que deseja inativar o aluno <strong className="text-gray-800">{memberName}</strong>? Ele perderá o acesso ao CT e futuras cobranças serão suspensas.</>
                        }
                    </DialogDescription>
                </DialogHeader>

                {apiError && (
                    <div className="bg-red-50 border border-red-200 text-red-700 p-3 rounded-md text-sm mt-2">
                        <strong>Atenção:</strong> {apiError}
                    </div>
                )}

                <div className="flex justify-end gap-3 mt-4">
                    <Button variant="outline" onClick={() => setOpen(false)} disabled={isLoading} className="bg-white">
                        Cancelar
                    </Button>
                    <Button 
                        className={`text-white transition-colors ${isInactive ? "bg-green-600 hover:bg-green-700" : "bg-red-600 hover:bg-red-700"}`} 
                        onClick={handleConfirm} 
                        disabled={isLoading}
                    >
                        {isLoading ? "A processar..." : (isInactive ? "Sim, Reativar" : "Sim, Inativar")}
                    </Button>
                </div>
            </DialogContent>
        </Dialog>
    )
}