import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"

// Tipagem do que vem do seu backend
interface Plan {
  id: number;
  name: string;
  price: number;
}

export function CreateMemberModal() {
  const [open, setOpen] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [plans, setPlans] = useState<Plan[]>([])

  // Busca os planos assim que o componente é montado
  useEffect(() => {
    async function fetchPlans() {
      try {
        const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/plans`)
        if (response.ok) {
          const data = await response.json()
          setPlans(data)
          // Se houver planos, define o primeiro como padrão no formulário
          if (data.length > 0) {
            setFormData(prev => ({ ...prev, planId: data[0].id }))
          }
        }
      } catch (error) {
        console.error("Erro ao buscar planos:", error)
      }
    }
    fetchPlans()
  }, [])

  const [formData, setFormData] = useState({
    name: '', email: '', whatsapp: '', birthDate: '', planId: 0
  })

  const [dependentData, setDependentData] = useState({
    name: '', email: '', whatsapp: '', birthDate: ''
  })

  const handleHolderChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value })
  }

  const handleDependentChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setDependentData({ ...dependentData, [e.target.name]: e.target.value })
  }

  // Verifica se o plano selecionado contém a palavra "Casal" (Independente do ID)
  const selectedPlan = plans.find(p => p.id === Number(formData.planId))
  const isCouplePlan = selectedPlan?.name.toLowerCase().includes('casal')

  const handleSubmit = async (e: React.SubmitEvent<HTMLFormElement>) => {
    e.preventDefault()
    setIsLoading(true)

    try {
      // 1. Salvar o Titular
      const holderResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          name: formData.name, email: formData.email, whatsapp: formData.whatsapp,
          birthDate: formData.birthDate, planId: Number(formData.planId), holderId: null
        }),
      })

      if (!holderResponse.ok) throw new Error('Erro ao cadastrar titular')
      const holder = await holderResponse.json()

      // 2. Se for Plano Casal, salva o Dependente apontando para o Titular
      if (isCouplePlan) {
        const dependentResponse = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify({
            name: dependentData.name, email: dependentData.email, whatsapp: dependentData.whatsapp,
            birthDate: dependentData.birthDate, planId: Number(formData.planId), holderId: holder.id
          }),
        })

        if (!dependentResponse.ok) throw new Error('Erro ao cadastrar dependente')
      }

      setOpen(false)
      window.location.reload() 
      
    } catch (error) {
      alert("Falha ao salvar. Verifique o console.")
      console.error(error)
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors">
          + Novo Aluno
        </button>
      </DialogTrigger>

      <DialogContent className="sm:max-w-137.5 max-h-[90vh] overflow-y-auto bg-white">
        <DialogHeader>
          <DialogTitle className="text-xl font-bold text-gray-800">Nova Matrícula</DialogTitle>
          <p className="text-sm text-gray-500">
            Atenção: Use apenas para cadastrar pessoas que ainda não estão no sistema.
          </p>
        </DialogHeader>
        
        <form onSubmit={handleSubmit} className="space-y-4 mt-2">
          {/* SESSÃO DO TITULAR */}
          <div className="bg-gray-50 p-4 rounded-lg border border-gray-100">
            <h3 className="font-semibold text-gray-700 mb-3 text-sm">Dados do Titular</h3>
            <div className="space-y-3">
              <div>
                <label className="block text-xs font-medium text-gray-700 mb-1">Nome Completo *</label>
                <input required type="text" name="name" value={formData.name} onChange={handleHolderChange} 
                       className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
              </div>
              
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">WhatsApp</label>
                  <input type="text" name="whatsapp" value={formData.whatsapp} onChange={handleHolderChange} placeholder="(00) 00000-0000"
                         className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">Data Nasc. *</label>
                  <input required type="date" name="birthDate" value={formData.birthDate} onChange={handleHolderChange} 
                         className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">E-mail</label>
                  <input type="email" name="email" value={formData.email} onChange={handleHolderChange} 
                         className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
                </div>
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">Plano Escolhido *</label>
                  <select name="planId" value={formData.planId} onChange={handleHolderChange} 
                          className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm">
                    {/* Renderiza dinamicamente os planos que vieram do Spring Boot */}
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

          {/* SESSÃO DO DEPENDENTE (APARECE SE O PLANO CONTIVER "CASAL") */}
          {isCouplePlan && (
            <div className="bg-blue-50 p-4 rounded-lg border border-blue-100 mt-4 animate-in fade-in slide-in-from-top-4">
              <h3 className="font-semibold text-blue-800 mb-3 text-sm">Dados do Dependente</h3>
              <div className="space-y-3">
                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">Nome do Dependente *</label>
                  <input required type="text" name="name" value={dependentData.name} onChange={handleDependentChange} 
                         className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
                </div>
                
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">WhatsApp</label>
                    <input type="text" name="whatsapp" value={dependentData.whatsapp} onChange={handleDependentChange} 
                           className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
                  </div>
                  <div>
                    <label className="block text-xs font-medium text-gray-700 mb-1">Data Nasc. *</label>
                    <input required type="date" name="birthDate" value={dependentData.birthDate} onChange={handleDependentChange} 
                           className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
                  </div>
                </div>

                <div>
                  <label className="block text-xs font-medium text-gray-700 mb-1">E-mail</label>
                  <input type="email" name="email" value={dependentData.email} onChange={handleDependentChange} 
                         className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm" />
                </div>
              </div>
            </div>
          )}

          <div className="pt-2 flex justify-end">
            <button disabled={isLoading} type="submit" 
                    className="bg-green-600 hover:bg-green-700 disabled:bg-green-400 text-white px-6 py-2 rounded-lg font-medium transition-colors">
              {isLoading ? 'Salvando...' : 'Salvar Matrícula'}
            </button>
          </div>
        </form>
      </DialogContent>
    </Dialog>
  )
}