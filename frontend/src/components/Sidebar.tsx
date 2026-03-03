import { Link } from '@tanstack/react-router'

export function Sidebar() {
  return (
    <aside className="w-64 bg-white border-r border-gray-200 flex flex-col">
      <div className="p-6">
        <h1 className="text-xl font-bold text-blue-600">Gestão CT</h1>
      </div>
      <nav className="flex-1 px-4 space-y-2">
        {/* O 'to="/"' aponta para o seu pages/index.tsx */}
        <Link to="/" className="block px-4 py-2 rounded-lg bg-blue-50 text-blue-700 font-medium [&.active]:font-bold">
          Visão Geral
        </Link>
        <Link to="/alunos" className="block px-4 py-2 rounded-lg text-gray-600 hover:bg-gray-100 font-medium opacity-50 cursor-not-allowed">
          Alunos (Em breve)
        </Link>
      </nav>
    </aside>
  )
}