import { Link } from '@tanstack/react-router'
import { LayoutDashboard, Users, CircleDollarSign } from "lucide-react"

export function Sidebar() {
  return (
    <aside className="w-64 bg-white border-r border-gray-200 flex flex-col">
      <div className="p-6">
        <h1 className="text-xl font-bold text-blue-600">Gestão CT</h1>
      </div>
      <nav className="flex-1 px-4 space-y-2">

        <nav className="space-y-2 mt-6">
          {/* Link Visão Geral */}
          <Link
            to="/"
            className="flex items-center gap-3 px-4 py-2 rounded-lg text-gray-600 hover:bg-gray-100 [&.active]:bg-blue-50 [&.active]:text-blue-700 [&.active]:font-semibold transition-colors"
          >
            <LayoutDashboard className="h-5 w-5" />
            Visão Geral
          </Link>

          {/* Link Alunos */}
          <Link
            to="/members"
            search={{ page: 0, q: '' }} /* <-- AQUI: Avisamos que começa na pág 0 e sem pesquisa */
            className="flex items-center gap-3 px-4 py-2 rounded-lg text-gray-600 hover:bg-gray-100 [&.active]:bg-blue-50 [&.active]:text-blue-700 [&.active]:font-semibold transition-colors"
          >
            <Users className="h-5 w-5" />
            Alunos
          </Link>

          {/* Link Pagamentos */}
          <Link
            to="/tsx/payments" /* <-- AQUI: Usamos a rota exata que o seu projeto gerou */
            search={{ page: 0 }} /* <-- AQUI: Avisamos que os pagamentos também começam na pág 0 */
            className="flex items-center gap-3 px-4 py-2 rounded-lg text-gray-600 hover:bg-gray-100 [&.active]:bg-blue-50 [&.active]:text-blue-700 [&.active]:font-semibold transition-colors"
          >
            <CircleDollarSign className="h-5 w-5" />
            Pagamentos
          </Link>
        </nav>

      </nav>
    </aside>
  )
}