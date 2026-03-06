import { useState, useEffect } from 'react'
import { createFileRoute, useNavigate } from '@tanstack/react-router'
import { MembersTable } from '../../components/member/MembersTable'
import { z } from 'zod'

const membersSearchSchema = z.object({
  page: z.number().catch(0),
  q: z.string().catch(''),
})

export const Route = createFileRoute('/_members/members')({
  validateSearch: membersSearchSchema,
  component: MembersPage,
  head: () => ({
    meta: [{ title: 'Gestão de Alunos - Academia' }],
  }),
})

function MembersPage() {
  const { q, page } = Route.useSearch()
  const navigate = useNavigate({ from: Route.fullPath })
  const [inputText, setInputText] = useState(q)

  useEffect(() => {
    const delayDebounceFn = setTimeout(() => {
      if (inputText !== q) {
        navigate({
          search: { q: inputText, page: 0 },
        })
      }
    }, 500)

    return () => clearTimeout(delayDebounceFn)
  }, [inputText, q, navigate])

  return (
    <div className="p-8 space-y-6">
      <div className="flex justify-between items-center mb-6">
        <div>
          <h2 className="text-2xl font-bold text-gray-800">Gestão de Alunos</h2>
          <p className="text-sm text-gray-500">Consulte e gerencie todos os alunos matriculados no CT.</p>
        </div>

        <input
          type="text"
          placeholder="Pesquisar aluno..."
          value={inputText}
          onChange={(e) => setInputText(e.target.value)}
          className="px-4 py-2 border border-gray-300 rounded-lg text-sm focus:outline-none focus:ring-2 focus:ring-blue-500 w-64"
        />
      </div>


      <MembersTable searchTerm={q} currentPage={page} />

    </div>
  )
}