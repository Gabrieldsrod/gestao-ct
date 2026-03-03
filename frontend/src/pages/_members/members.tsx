import { useState, useEffect } from 'react'
import { createFileRoute } from '@tanstack/react-router'
import { MembersTable } from '../../components/member/MembersTable'

export const Route = createFileRoute('/_members/members')({
  component: MembersPage,
  head: () => ({
    meta: [{ title: 'Gestão de Alunos - Academia' }],
  }),
})

function MembersPage() {
  // O que o usuário está digitando AGORA
  const [inputText, setInputText] = useState('')
  // O termo final que será enviado para o backend após o usuário parar de digitar
  const [searchTerm, setSearchTerm] = useState('')

  // Efeito de "Debounce" (espera 500ms após a última tecla digitada para atualizar o termo de busca)
  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      setSearchTerm(inputText)
    }, 500)

    return () => clearTimeout(delayDebounceFn)
  }, [inputText])

  return (
    <div className="p-8 space-y-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Gestão de Alunos</h2>
          <p className="text-sm text-gray-500">Consulte e gerencie todos os alunos matriculados no CT.</p>
        </div>
        
        {/* Input agora é controlado pelo React */}
        <input 
          type="text" 
          placeholder="Pesquisar aluno..." 
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          className="px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 w-64"
        />
      </div>

      <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-6">
        {/* Passamos o termo de busca final para a tabela */}
        <MembersTable searchTerm={searchTerm} />
      </div>
    </div>
  )
}