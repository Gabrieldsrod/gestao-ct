import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { ChevronLeft, ChevronRight, UserMinus } from "lucide-react"
import { useNavigate } from "@tanstack/react-router"
import { useGetMembers } from "../../hooks/member/useGetMembers"
import { useInactivateMember } from "../../hooks/member/useInactivateMember"
import { EditMemberModal } from "./EditMemberModal"

function getStatusBadge(status: string) {
  switch (status) {
    case 'ACTIVE':
      return <Badge className="bg-green-100 text-green-800 hover:bg-green-100 border-green-200">Ativo</Badge>
    case 'DELINQUENT':
      return <Badge className="bg-red-100 text-red-800 hover:bg-red-100 border-red-200">Inadimplente</Badge>
    case 'PENDING':
      return <Badge className="bg-yellow-100 text-yellow-800 hover:bg-yellow-100 border-yellow-200">Pendente</Badge>
    case 'INACTIVE':
      return <Badge className="bg-gray-100 text-gray-800 hover:bg-gray-100 border-gray-200">Inativo</Badge>
    default:
      return <Badge>{status}</Badge>
  }
}

function formatarData(dataIso: string) {
  if (!dataIso) return '-';
  const partes = dataIso.split('-');
  if (partes.length === 3) {
    return `${partes[2]}/${partes[1]}/${partes[0]}`;
  }
  return dataIso;
}

interface MembersTableProps {
  searchTerm: string;
  currentPage: number;
}

export function MembersTable({ searchTerm, currentPage }: MembersTableProps) {
  const navigate = useNavigate({ from: '/members' })

  const { members, totalPages, totalElements, isLoading, error } = useGetMembers(currentPage, 10, searchTerm);
  const { inactivateMember, isLoading: isActivating } = useInactivateMember();

  const handleInactivateClick = async (id: number, name: string) => {
    if (window.confirm(`Deseja realmente inativar o aluno ${name}?`)) {
      const result = await inactivateMember(id);
      if (result.success) {
        alert("Aluno inativado com sucesso!");
        window.location.reload();
      } else {
        // Aqui a mágica acontece: mostra o erro do seu Java na cara do usuário!
        alert(`Falha: ${result.message}`);
      }
    }
  }

  const handlePageChange = (newPage: number) => {
    navigate({
      search: (prev) => ({ ...prev, page: newPage })
    })
  }
  if (isLoading && members.length === 0) {
    return <div className="p-8 text-center text-gray-500">A carregar alunos...</div>;
  }

  if (error) {
    return <div className="p-8 text-center text-red-500">Erro: {error}</div>;
  }

  if (members.length === 0) {
    return (
      <div className="p-8 text-center text-gray-500">
        {searchTerm ? `Nenhum aluno encontrado para "${searchTerm}".` : "Nenhum aluno matriculado ainda."}
      </div>
    );
  }

  return (
    <div className="rounded-md border border-gray-100 bg-white flex flex-col min-h-[calc(100vh-240px)] shadow-sm">
      <div className="flex-1">
        <Table>
          <TableHeader className="bg-gray-50/50">
            <TableRow>
              <TableHead className="font-semibold text-gray-600">Aluno</TableHead>
              <TableHead className="font-semibold text-gray-600">WhatsApp</TableHead>
              <TableHead className="font-semibold text-gray-600">E-mail</TableHead>
              <TableHead className="font-semibold text-gray-600">Plano</TableHead>
              <TableHead className="font-semibold text-gray-600">Data de Matrícula</TableHead>
              <TableHead className="font-semibold text-gray-600 text-center">Status</TableHead>
              <TableHead className="text-right font-semibold text-gray-600">Ações</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {members.map((student) => (
              <TableRow key={student.id} className="hover:bg-gray-50/50 transition-colors">
                <TableCell className="font-medium text-gray-800">{student.name}</TableCell>
                <TableCell className="text-gray-600">{student.whatsapp || '-'}</TableCell>
                <TableCell className="text-gray-600">{student.email || '-'}</TableCell>
                <TableCell className="text-gray-600">{student.plan?.name || "Sem plano"}</TableCell>
                <TableCell className="text-gray-600">{formatarData(student.registrationDate)}</TableCell>
                <TableCell className="text-center">
                  {getStatusBadge(student.status)}
                </TableCell>
                <TableCell className="text-right">
                  <EditMemberModal memberId={student.id} />
                  <Button
                    variant="outline"
                    size="sm"
                    className="ml-2 text-red-500 hover:text-red-700"
                    onClick={() => handleInactivateClick(student.id, student.name)}
                    disabled={isActivating}
                  >
                    <UserMinus className="h-4 w-4" />
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </div>

      {/* 3. A BARRA DE PAGINAÇÃO: Agora vive dentro do card branco, no rodapé, com uma bordinha sutil em cima */}
      {totalPages > 0 && (
        <div className="flex items-center justify-between px-6 py-4 border-t border-gray-100 bg-gray-50/30">
          <span className="text-sm text-gray-500 font-medium">
            Página {currentPage + 1} de {totalPages} <span className="text-gray-400 font-normal">({totalElements} alunos no total)</span>
          </span>

          <div className="flex gap-2">
            <Button
              variant="outline"
              size="sm"
              className="bg-white"
              onClick={() => handlePageChange(Math.max(0, currentPage - 1))}
              disabled={currentPage === 0 || isLoading}
            >
              <ChevronLeft className="h-4 w-4 mr-1" /> Anterior
            </Button>

            <Button
              variant="outline"
              size="sm"
              className="bg-white"
              onClick={() => handlePageChange(Math.min(totalPages - 1, currentPage + 1))}
              disabled={currentPage >= totalPages - 1 || isLoading}
            >
              Próxima <ChevronRight className="h-4 w-4 ml-1" />
            </Button>
          </div>
        </div>
      )}
    </div>
  )
}