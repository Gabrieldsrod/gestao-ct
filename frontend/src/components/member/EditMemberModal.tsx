import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription, DialogTrigger } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Edit } from "lucide-react"

interface Plan {
  id: number;
  name: string;
  price: number;
}

interface EditMemberModalProps {
  memberId: number;
}

// Helper para garantir que o HTML entenda a data vinda do Java
function parseDateForInput(backendDate: any): string {
  if (!backendDate) return '';
  
  // Se o Java devolver como Array (ex: [2026, 3, 3])
  if (Array.isArray(backendDate)) {
    return `${backendDate[0]}-${String(backendDate[1]).padStart(2, '0')}-${String(backendDate[2]).padStart(2, '0')}`;
  }
  
  // Se for String
  if (typeof backendDate === 'string') {
    // Se vier com o T do timestamp (ex: 2026-03-03T10:00)
    if (backendDate.includes('T')) return backendDate.split('T')[0];
    
    // Se vier com barras (ex: 03/03/2026)
    if (backendDate.includes('/')) {
      const [dia, mes, ano] = backendDate.split('/');
      return `${ano}-${mes}-${dia}`;
    }
    
    // Se já estiver certo (2026-03-03)
    return backendDate;
  }
  return '';
}

export function EditMemberModal({ memberId }: EditMemberModalProps) {
  const [open, setOpen] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [isLoadingData, setIsLoadingData] = useState(false)
  const [plans, setPlans] = useState<Plan[]>([])

  // Estado do aluno atual (Titular)
  const [formData, setFormData] = useState({
    name: '', email: '', whatsapp: '', birthDate: '', planId: 0
  })

  // Estado para o NOVO dependente (caso faça upgrade)
  const [dependentData, setDependentData] = useState({
    name: '', email: '', whatsapp: '', birthDate: ''
  })

  // Controla se o plano original era individual e está mudando para casal
  const [originalPlanId, setOriginalPlanId] = useState(0)

  // Quando o modal abre, busca os planos e os dados do aluno
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
        setFormData({
          name: data.name || '',
          email: data.email || '',
          whatsapp: data.whatsapp || '',
          birthDate: parseDateForInput(data.birthDate), // Converte a data para o formato HTML
          planId: data.planId || 1 // Idealmente o backend deve devolver o planId
        })
        setOriginalPlanId(data.planId || 1)
      }
    } catch (error) {
      console.error("Erro ao buscar dados do aluno:", error)
    } finally {
      setIsLoadingData(false)
    }
  }

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleDependentChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDependentData({ ...dependentData, [e.target.name]: e.target.value })
  }

  // Verifica se o plano selecionado é um plano "Casal"
  const selectedPlan = plans.find(p => p.id === Number(formData.planId))
  const isCouplePlan = selectedPlan?.name.toLowerCase().includes('casal')
  
  // Regra de negócio: Só mostra a caixa do dependente se ele mudou para um plano casal AGORA
  const isUpgradingToCouple = isCouplePlan && originalPlanId !== Number(formData.planId)

  const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      // 1. Atualiza o Titular (PUT)
      const updateResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members/${memberId}`, {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: formData.name, 
          email: formData.email, 
          whatsapp: formData.whatsapp,
          birthDate: formData.birthDate, 
          planId: Number(formData.planId)
        }),
      })

      if (!updateResponse.ok) throw new Error('Erro ao atualizar titular')

      // 2. Se fez upgrade para casal, cria o Dependente (POST)
      if (isUpgradingToCouple) {
        const dependentResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            name: dependentData.name, 
            email: dependentData.email, 
            whatsapp: dependentData.whatsapp,
            birthDate: dependentData.birthDate, 
            planId: Number(formData.planId), 
            holderId: memberId 
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

      <DialogContent className="sm:max-w-137.5 max-h-[90vh] overflow-y-auto bg-white">
        <DialogHeader>
          <DialogTitle className="text-xl font-bold text-gray-800">Editar Perfil do Aluno</DialogTitle>
          <DialogDescription className="sr-only">
            Altere os dados de perfil e plano do aluno e clique em salvar.
          </DialogDescription>
        </DialogHeader>
        
        {isLoadingData ? (
          <div className="py-8 text-center text-gray-500">A carregar dados do aluno...</div>
        ) : (
          <form onSubmit={handleSubmit} className="space-y-4 mt-2">
            <div className="bg-gray-50 p-4 rounded-lg border border-gray-100">
              <h3 className="font-semibold text-gray-700 mb-3 text-sm">Dados do Aluno</h3>
              <div className="space-y-3">
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">Nome Completo *</label>
                  <input required type="text" name="name" value={formData.name} onChange={handleChange} 
                         className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm" />
                </div>
                
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">WhatsApp</label>
                    <input type="text" name="whatsapp" value={formData.whatsapp} onChange={handleChange} 
                           className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm" />
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">Data Nasc.</label>
                    <input required type="date" name="birthDate" value={formData.birthDate} onChange={handleChange} 
                           className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm" />
                  </div>
                </div>

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">E-mail</label>
                    <input type="email" name="email" value={formData.email} onChange={handleChange} 
                           className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm" />
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">Plano Atual</label>
                    <select name="planId" value={formData.planId} onChange={handleChange} 
                            className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm">
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

            {/* SE FIZER UPGRADE PARA CASAL, PEDE O DEPENDENTE AQUI */}
            {isUpgradingToCouple && (
              <div className="bg-blue-50 p-4 rounded-lg border border-blue-100 mt-4">
                <h3 className="font-semibold text-blue-800 mb-3 text-sm">Upgrade: Incluir Dependente</h3>
                <div className="space-y-3">
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">Nome do Dependente *</label>
                    <input required type="text" name="name" value={dependentData.name} onChange={handleDependentChange} 
                           className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm" />
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div>
                      <label className="block text-xs font-medium text-gray-700 mb-1">WhatsApp</label>
                      <input type="text" name="whatsapp" value={dependentData.whatsapp} onChange={handleDependentChange} 
                             className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm" />
                    </div>
                    <div>
                      <label className="block text-xs font-medium text-gray-700 mb-1">Data Nasc. *</label>
                      <input required type="date" name="birthDate" value={dependentData.birthDate} onChange={handleDependentChange} 
                             className="w-full px-3 py-2 border border-gray-300 rounded-md text-sm" />
                    </div>
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