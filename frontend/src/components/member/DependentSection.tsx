import { useState } from "react";
import { type UseFormRegister } from "react-hook-form";
import { useGetEligibleDependents } from "@/hooks/member/useGetElegibleDependents";
import { useDebounce } from "@/hooks/useDebounce";

interface DependentSectionProps {
    register: UseFormRegister<any>; 
    errors: any; 
    dependentMode: 'new' | 'existing';
    setDependentMode: (mode: 'new' | 'existing') => void;
    currentMemberId?: number;
}

export function DependentSection({
    register,
    errors,
    dependentMode,
    setDependentMode,
    currentMemberId
}: DependentSectionProps) {

    const [searchTerm, setSearchTerm] = useState('');
    const debouncedSearchTerm = useDebounce(searchTerm, 300);

    const { eligibleDependents, isLoading, error } = useGetEligibleDependents(currentMemberId, debouncedSearchTerm);

    return (
        <div className="bg-blue-50 p-4 rounded-lg border border-blue-100 mt-4 animate-in fade-in slide-in-from-top-4">
            <div className="flex justify-between items-center mb-3">
                <h3 className="font-semibold text-blue-800 text-sm">Dados do Dependente</h3>

                <div className="flex bg-blue-100 rounded-md p-0.5">
                    <button type="button" onClick={() => setDependentMode('new')}
                        className={`text-xs px-3 py-1 rounded-sm transition-colors ${dependentMode === 'new' ? 'bg-white text-blue-800 shadow-sm font-medium' : 'text-blue-600 hover:text-blue-800'}`}>
                        Novo Cadastro
                    </button>
                    <button type="button" onClick={() => setDependentMode('existing')}
                        className={`text-xs px-3 py-1 rounded-sm transition-colors ${dependentMode === 'existing' ? 'bg-white text-blue-800 shadow-sm font-medium' : 'text-blue-600 hover:text-blue-800'}`}>
                        Vincular Existente
                    </button>
                </div>
            </div>

            {dependentMode === 'existing' ? (
                <div className="space-y-3">
                    <div>
                        <label className="block text-xs font-medium text-gray-700 mb-1">Pesquisar Aluno</label>
                        <input
                            type="text"
                            placeholder="Digite o nome para filtrar..."
                            value={searchTerm}
                            onChange={(e) => setSearchTerm(e.target.value)}
                            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500 text-sm bg-white mb-3"
                        />
                    </div>

                    <div>
                        <label className="block text-xs font-medium text-gray-700 mb-1">Selecione o Aluno *</label>
                        <select
                            {...register("existingDependentId", { valueAsNumber: true })}
                            disabled={isLoading}
                            className={`w-full px-3 py-2 border rounded-md focus:outline-none focus:ring-2 text-sm bg-white ${errors.existingDependentId ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`}
                        >
                            <option value={0}>
                                {isLoading ? "Buscando..." : "-- Selecione um aluno na lista --"}
                            </option>

                            {eligibleDependents.map(em => (
                                <option key={em.id} value={em.id}>
                                    {em.name} {em.whatsapp ? `(Whats: ${em.whatsapp})` : ''}
                                </option>
                            ))}
                        </select>

                        {error && <p className="text-red-500 text-xs mt-1">{error}</p>}
                        {errors.existingDependentId && <p className="text-red-500 text-xs mt-1">{errors.existingDependentId.message}</p>}

                        {!isLoading && searchTerm && eligibleDependents.length === 0 && !error && (
                            <p className="text-orange-500 text-xs mt-1">Nenhum aluno elegível encontrado com esse nome.</p>
                        )}
                    </div>

                    <p className="text-xs text-blue-600 mt-2">
                        As cobranças avulsas deste aluno serão canceladas e ele passará a integrar o plano deste titular.
                    </p>
                </div>
            ) : (
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

                    <div>
                        <label className="block text-xs font-medium text-gray-700 mb-1">E-mail</label>
                        <input type="email" {...register("dependentEmail")}
                            className={`w-full px-3 py-2 border rounded-md text-sm outline-none focus:ring-2 ${errors.dependentEmail ? 'border-red-500 focus:ring-red-200' : 'border-gray-300 focus:ring-blue-500'}`} />
                        {errors.dependentEmail && <p className="text-red-500 text-xs mt-1">{errors.dependentEmail.message}</p>}
                    </div>
                </div>
            )}
        </div>
    )
}