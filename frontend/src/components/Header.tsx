import { CreateMemberModal } from "./member/CreateMemberModal";

export function Header() {
  return (
    <header className="px-8 py-6 flex justify-between items-center bg-white border-b border-gray-200">
      <div>
        <h2 className="text-2xl font-semibold text-gray-800">Painel de Controle</h2>
        <p className="text-sm text-gray-500">Acompanhe as métricas do CT</p>
      </div>
      
      <CreateMemberModal />
      
    </header>
  )
}