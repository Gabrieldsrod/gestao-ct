import { useState, useEffect } from "react"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { useForm } from "react-hook-form"
import { zodResolver } from "@hookform/resolvers/zod"
import { DependentSection } from "./DependentSection"
import { memberSchema, type MemberFormValues } from "../../schemas/memberSchema"
import { useCreateMember } from "../../hooks/member/useCreateMember"

interface Plan {
  id: number;
  name: string;
  price: number;
}

export function CreateMemberModal() {
  const [open, setOpen] = useState(false)
  const { createMember, isLoading } = useCreateMember()
  const [plans, setPlans] = useState<Plan[]>([])

  const [dependentMode, setDependentMode] = useState<'new' | 'existing'>('new')

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors },
    setError,
    reset
  } = useForm<MemberFormValues>({
    resolver: zodResolver(memberSchema),
    defaultValues: {
      name: '', whatsapp: '', email: '', birthDate: '', planId: 0,
      dependentName: '', dependentWhatsapp: '', dependentEmail: '', dependentBirthDate: ''
    }
  })

  const watchedPlanId = watch("planId")
  const selectedPlan = plans.find(p => p.id === Number(watchedPlanId))
  const isCouplePlan = selectedPlan?.name.toLowerCase().includes('casal')

  useEffect(() => {
    async function fetchPlans() {
      try {
        const response = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/plans`)
        if (response.ok) {
          const data = await response.json()
          setPlans(data)
          if (data.length > 0) {
            reset(formValues => ({ ...formValues, planId: data[0].id }))
          }
        }
      } catch (error) {
        console.error("Erro ao buscar planos:", error)
      }
    }
    fetchPlans()
  }, [reset])

  const onSubmit = async (data: MemberFormValues) => {
    if (isCouplePlan) {
      if (dependentMode === 'existing') {
        if (!data.existingDependentId || data.existingDependentId === 0) {
          setError("existingDependentId" as any, { message: "Selecione um aluno para vincular" })
          return
        }
      } else {
        if (!data.dependentName || data.dependentName.length < 3) {
          setError("dependentName", { message: "Nome do dependente é obrigatório" })
          return
        }
        if (!data.dependentBirthDate) {
          setError("dependentBirthDate", { message: "Data de nascimento é obrigatória" })
          return
        }
      }
    }

    const success = await createMember(data, isCouplePlan);

    if (success) {
      setOpen(false)
      reset()
      window.location.reload()
    } else {
      alert("Falha ao salvar. Verifique o console.")
    }
  }

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <button className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg font-medium transition-colors">
          + Novo Aluno
        </button>
      </DialogTrigger>

      <DialogContent aria-describedby="create-dialog" className="sm:max-w-137.5 max-h-[90vh] overflow-y-auto bg-white">
        <DialogHeader>
          <DialogTitle className="text-xl font-bold text-gray-800">Nova Matrícula</DialogTitle>
          <p id="create-dialog" className="text-sm text-gray-500">
            Atenção: Use apenas para cadastrar pessoas que ainda não estão no sistema.
          </p>
        </DialogHeader>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-4 mt-2">

          <div className="bg-gray-50 p-4 rounded-lg border border-gray-100">
            <h3 className="font-semibold text-gray-700 mb-3 text-sm">Dados do Titular</h3>
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
                  <input {...register("whatsapp")} placeholder="(00) 00000-0000"
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
                  <label className="block text-xs font-medium text-gray-700 mb-1">Plano Escolhido *</label>
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

          {isCouplePlan && (
            <DependentSection 
              register={register} 
              errors={errors} 
              dependentMode={dependentMode} 
              setDependentMode={setDependentMode} 
            />
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