import { useState } from "react" // <-- Adicionado o useState aqui
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
    const [statusFilter, setStatusFilter] = useState<string>("ACTIVE")

    const { members, totalPages, totalElements, isLoading, error } = useGetMembers(currentPage, 10, searchTerm, statusFilter);
    const { inactivateMember, isLoading: isActivating } = useInactivateMember();

    const handleInactivateClick = async (id: number, name: string) => {
        if (window.confirm(`Deseja realmente inativar o aluno ${name}?`)) {
            const result = await inactivateMember(id);
            if (result.success) {
                alert("Aluno inativado com sucesso!");
                window.location.reload();
            } else {
                alert(`Falha: ${result.message}`);
            }
        }
    }

    const handlePageChange = (newPage: number) => {
        navigate({
            search: (prev) => ({ ...prev, page: newPage })
        })
    }

    const handleTabChange = (newStatus: string) => {
        setStatusFilter(newStatus);
        navigate({
            search: (prev: any) => ({ ...prev, page: 0 })
        });
    }

    return (
        <div className="flex flex-col gap-4">
            <div className="flex space-x-2 overflow-x-auto pb-1">
                {[
                    { id: "ACTIVE", label: "Ativos" },
                    { id: "PENDING", label: "Pendentes" },
                    { id: "DELINQUENT", label: "Inadimplentes" },
                    { id: "INACTIVE", label: "Inativos" },
                    { id: "ALL", label: "Todos" }
                ].map((tab) => (
                    <button
                        key={tab.id}
                        onClick={() => handleTabChange(tab.id)}
                        className={`px-4 py-2 text-sm font-medium rounded-full transition-colors whitespace-nowrap
              ${statusFilter === tab.id
                                ? 'bg-blue-600 text-white shadow-sm'
                                : 'bg-white border border-gray-200 text-gray-600 hover:bg-gray-50'
                            }`}
                    >
                        {tab.label}
                    </button>
                ))}
            </div>

            <div className="rounded-md border border-gray-100 bg-white flex flex-col min-h-[calc(100vh-300px)] shadow-sm">

                {isLoading && members.length === 0 ? (
                    <div className="p-8 text-center text-gray-500 flex-1 flex items-center justify-center">A carregar alunos...</div>
                ) : error ? (
                    <div className="p-8 text-center text-red-500 flex-1 flex items-center justify-center">Erro: {error}</div>
                ) : members.length === 0 ? (
                    <div className="p-8 text-center text-gray-500 flex-1 flex items-center justify-center">
                        {searchTerm ? `Nenhum aluno encontrado para "${searchTerm}".` : "Nenhum aluno encontrado nesta categoria."}
                    </div>
                ) : (
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
                                        <TableCell>
                                            <div className="flex flex-col">
                                                <span className="font-medium text-gray-800">{student.name}</span>
                                                {student.holderName && (
                                                    <span className="text-xs text-gray-500 mt-0.5">
                                                        ↳ Dependente de: <strong className="text-gray-600">{student.holderName}</strong>
                                                    </span>
                                                )}
                                            </div>
                                        </TableCell>
                                        <TableCell className="text-gray-600">{student.whatsapp || '-'}</TableCell>
                                        <TableCell className="text-gray-600">{student.email || '-'}</TableCell>
                                        <TableCell className="text-gray-600">{student.plan?.name || "Sem plano"}</TableCell>
                                        <TableCell className="text-gray-600">{formatarData(student.registrationDate)}</TableCell>
                                        <TableCell className="text-center">
                                            {getStatusBadge(student.status)}
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <div className="flex justify-end items-center gap-2">
                                                <EditMemberModal memberId={student.id} />
                                                <Button
                                                    variant="outline"
                                                    size="icon"
                                                    className="h-8 w-8 text-red-500 hover:text-red-700 hover:bg-red-50"
                                                    onClick={() => handleInactivateClick(student.id, student.name)}
                                                    disabled={isActivating}
                                                >
                                                    <UserMinus className="h-4 w-4" />
                                                </Button>
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </div>
                )}

                {totalPages > 0 && !isLoading && (
                    <div className="flex items-center justify-between px-6 py-4 border-t border-gray-100 bg-gray-50/30">
                        <span className="text-sm text-gray-500 font-medium">
                            Página {currentPage + 1} de {totalPages} <span className="text-gray-400 font-normal">({totalElements} alunos nesta aba)</span>
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
        </div>
    )
}