import { createFileRoute } from '@tanstack/react-router'
import { RecentStudentsTable } from '../../components/RecentStudentsTable'

// A magia acontece aqui: isto define automaticamente o caminho /alunos
export const Route = createFileRoute('/_members/members')({
  component: MembersPage,
  head: () => ({
    meta: [
      { 
        title: 'Gestão de Alunos - Academia' 
      },
    ],
  }),
})

function MembersPage() {
  return (
    <div className="p-8 space-y-6">
      
      {/* Cabeçalho específico da página */}
      <div className="flex justify-between items-center mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Gestão de Alunos</h2>
          <p className="text-sm text-gray-500">Consulte e gira todos os alunos matriculados no CT.</p>
        </div>
        {/* Aqui no futuro podemos colocar um campo de pesquisa ou filtros */}
        <input 
          type="text" 
          placeholder="Pesquisar aluno..." 
          className="px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
        />
      </div>

      {/* A nossa tabela ganha agora o ecrã inteiro! */}
      <div className="bg-white rounded-xl border border-gray-200 shadow-sm p-6">
        <RecentStudentsTable />
      </div>

    </div>
  )
}