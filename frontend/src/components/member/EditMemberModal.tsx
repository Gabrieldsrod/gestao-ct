import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogTrigger } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Edit } from "lucide-react"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { memberSchema, type MemberFormValues } from "../../schemas/memberSchema"

// Helper para formatar a data que vem do Java para o HTML
function parseDateForInput(backendDate: any): string {
  if (!backendDate) return '';
  if (Array.isArray(backendDate)) {
    return `${backendDate[0]}-${String(backendDate[1]).padStart(2, '0')}-${String(backendDate[2]).padStart(2, '0')}`;
  }
  if (typeof backendDate === 'string') {
    if (backendDate.includes('T')) return backendDate.split('T')[0];
    if (backendDate.includes('/')) {
      const [dia, mes, ano] = backendDate.split('/');
      return `${ano}-${mes}-${dia}`;
    }
    return backendDate;
  }
  return '';
}

interface Plan { id: number; name: string; price: number; }
interface EditMemberModalProps { memberId: number; }

export function EditMemberModal({ memberId }: EditMemberModalProps) {
  const [open, setOpen] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [isLoadingData, setIsLoadingData] = useState(false)
  const [plans, setPlans] = useState<Plan[]>([])
  const [originalPlanId, setOriginalPlanId] = useState(0)

  const { register, handleSubmit, watch, formState: { errors }, setError, reset } = useForm<MemberFormValues>({
    resolver: zodResolver(memberSchema),
    defaultValues: {
      name: '', whatsapp: '', email: '', birthDate: '', planId: 0,
      dependentName: '', dependentWhatsapp: '', dependentEmail: '', dependentBirthDate: ''
    }
  })

  // Regra de Negócio: Fica de olho no select para saber se fez Upgrade para Casal
  const watchedPlanId = watch("planId")
  const selectedPlan = plans.find(p => p.id === Number(watchedPlanId))
  const isCouplePlan = selectedPlan?.name.toLowerCase().includes('casal')
  const isUpgradingToCouple = isCouplePlan && originalPlanId !== 0 && originalPlanId !== Number(watchedPlanId)

  useEffect(() => {
    if (open) {
      fetchPlans()
      fetchMemberData()
    }
  }, [open])

  async function fetchPlans() {
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/plans`)
      if (response.ok) setPlans(await response.json())
    } catch (error) {
      console.error("Erro ao buscar planos:", error)
    }
  }

  async function fetchMemberData() {
    setIsLoadingData(true)
    try {
      const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members/${memberId}`)
      if (response.ok) {
        const data = await response.json()
        setOriginalPlanId(data.planId || 1)
        
        // Preenche o formulário automaticamente com os dados do banco!
        reset({
          name: data.name || '',
          email: data.email || '',
          whatsapp: data.whatsapp || '',
          birthDate: parseDateForInput(data.birthDate),
          planId: data.planId || 1,
          dependentName: '', dependentWhatsapp: '', dependentEmail: '', dependentBirthDate: ''
        })
      }
    } catch (error) {
      console.error("Erro ao buscar dados do aluno:", error)
    } finally {
      setIsLoadingData(false)
    }
  }

  const onSubmit = async (data: MemberFormValues) => {
    setIsLoading(true)

    // Impede o envio se escolheu Upgrade mas não preencheu o dependente
    if (isUpgradingToCouple) {
      if (!data.dependentName || data.dependentName.length < 3) {
        setError("dependentName", { message: "Nome do dependente é obrigatório" })
        setIsLoading(false)
        return
      }
      if (!data.dependentBirthDate) {
        setError("dependentBirthDate", { message: "Data de nascimento é obrigatória" })
        setIsLoading(false)
        return
      }
    }

    try {
      // 1. Atualiza o Titular (PUT)
      const updateResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members/${memberId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: data.name, email: data.email, whatsapp: data.whatsapp,
          birthDate: data.birthDate, planId: data.planId
        }),
      })

      if (!updateResponse.ok) throw new Error('Erro ao atualizar titular')

      // 2. Cria o novo Dependente caso seja um Upgrade (POST)
      if (isUpgradingToCouple) {
        const dependentResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            name: data.dependentName, email: data.dependentEmail, whatsapp: data.dependentWhatsapp,
            birthDate: data.dependentBirthDate, planId: data.planId, holderId: memberId
          }),
        })
        if (!dependentResponse.ok) throw new Error('Erro ao cadastrar o novo dependente')
      }

      setOpen(false)
      window.location.reload() 
      
    } catch (error) {
      alert("Falha ao salvar as alterações. Verifique o console.")
      console.error(error)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="ghost" size="icon" className="h-8 w-8 text-blue-600 hover:text-blue-800 hover:bg-blue-50">
          <Edit className="h-4 w-4" />
        </Button>
      </DialogTrigger>

      <DialogContent aria-describedby="edit-dialog" className="sm:max-w-137.5 max-h-[90vh] overflow-y-auto bg-white">
        <DialogHeader>
          <DialogTitle className="text-xl font-bold text-gray-800">Editar Perfil do Aluno</DialogTitle>
          <DialogDescription id="edit-dialog" className="sr-only">
            Altere os dados de perfil e plano do aluno e clique em salvar.
          </DialogDescription>
        </DialogHeader>
        
        {isLoadingData ? (
          <div className="py-8 text-center text-gray-500">A carregar dados...</div>
        ) : (
          <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 mt-2">
            
            {/* SESSÃO DO TITULAR */}
            <div className="bg-gray-50 p-4 rounded-lg border border-gray-100">
              <h3 className="font-semibold text-gray-700 mb-3 text-sm">Dados do Aluno</h3>
              <div className="space-y-3">
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">Nome Completo *</label>
                  <input {...register("name")} 
                         className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.name ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`} />
                  {errors.name && <p className="text-red-500 text-xs mt-1">{errors.name.message}</p>}
                </div>
                
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">WhatsApp</label>
                    <input {...register("whatsapp")} 
                           className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">Data Nasc. *</label>
                    <input type="date" {...register("birthDate")} 
                           className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.birthDate ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`} />
                    {errors.birthDate && <p className="text-red-500 text-xs mt-1">{errors.birthDate.message}</p>}
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">E-mail</label>
                    <input type="email" {...register("email")} 
                           className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.email ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`} />
                    {errors.email && <p className="text-red-500 text-xs mt-1">{errors.email.message}</p>}
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">Plano Atual</label>
                    <select {...register("planId", { valueAsNumber: true })} 
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm">
                      {plans.map(plan => (
                        <option key={plan.id} value={plan.id}>
                          {plan.name} - R$ {plan.price.toFixed(2)}
                        </option>
                      ))}
                    </select>
                  </div>
                </div>
              </div>
            </div>

            {/* SESSÃO DO DEPENDENTE (Aparece no Upgrade) */}
            {isUpgradingToCouple && (
              <div className="bg-blue-50 p-4 rounded-lg border border-blue-100 mt-4 animate-in fade-in slide-in-from-top-4">
                <h3 className="font-semibold text-blue-800 mb-3 text-sm">Upgrade: Incluir Dependente</h3>
                <div className="space-y-3">
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">Nome do Dependente *</label>
                    <input {...register("dependentName")} 
                           className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.dependentName ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`} />
                    {errors.dependentName && <p className="text-red-500 text-xs mt-1">{errors.dependentName.message}</p>}
                  </div>
                  
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-xs font-medium text-gray-700 mb-1">WhatsApp</label>
                      <input {...register("dependentWhatsapp")} 
                             className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
                    </div>
                    <div>
                      <label className="block text-xs font-medium text-gray-700 mb-1">Data Nasc. *</label>
                      <input type="date" {...register("dependentBirthDate")} 
                             className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.dependentBirthDate ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`} />
                      {errors.dependentBirthDate && <p className="text-red-500 text-xs mt-1">{errors.dependentBirthDate.message}</p>}
                    </div>
                  </div>

                  {/* O CAMPO DE E-MAIL QUE ESTAVA FALTANDO! */}
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">E-mail</label>
                    <input type="email" {...register("dependentEmail")} 
                           className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.dependentEmail ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`} />
                    {errors.dependentEmail && <p className="text-red-500 text-xs mt-1">{errors.dependentEmail.message}</p>}
                  </div>
                </div>
              </div>
            )}

            <div className="pt-2 flex justify-end">
              <button disabled={isLoading} type="submit" 
                      className="bg-blue-600 hover:bg-blue-700 disabled:bg-blue-400 text-white px-6 py-2 rounded-lg font-medium transition-colors">
                {isLoading ? 'A Salvar...' : 'Salvar Alterações'}
              </button>
            </div>
          </form>
        )}
      </DialogContent>
    </Dialog>
  )
}