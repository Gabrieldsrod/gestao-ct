import { useState, useEffect } from "react"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { ChevronLeft, ChevronRight, Calendar, User, Search, X } from "lucide-react"
import { useNavigate } from "@tanstack/react-router"
import { useGetPayments } from "../../hooks/payment/useGetPayments"
import { useSearchMembers } from "@/hooks/member/useGetMembers";
import { useDebounce } from "@/hooks/useDebounce"
import { ConfirmPaymentModal } from "./ConfirmPaymentModal"

function getPaymentStatusBadge(status: string) {
    switch (status) {
        case 'PAID':
            return <Badge className="bg-green-100 text-green-800 hover:bg-green-100 border-green-200">Pago</Badge>
        case 'PENDING':
            return <Badge className="bg-yellow-100 text-yellow-800 hover:bg-yellow-100 border-yellow-200">Pendente</Badge>
        case 'OVERDUE':
            return <Badge className="bg-red-100 text-red-800 hover:bg-red-100 border-red-200">Atrasado</Badge>
        case 'CANCELED':
            return <Badge className="bg-gray-100 text-gray-800 hover:bg-gray-100 border-gray-200">Cancelado</Badge>
        default:
            return <Badge>{status}</Badge>
    }
}

interface PaymentsTableProps {
    currentPage: number;
}

export function PaymentsTable({ currentPage }: PaymentsTableProps) {
    const navigate = useNavigate({ from: '/payments' })
    const [statusFilter, setStatusFilter] = useState<string>("ALL")
    const [startDate, setStartDate] = useState("");
    const [endDate, setEndDate] = useState("");
    const [searchMember, setSearchMember] = useState("");
    const [selectedMemberId, setSelectedMemberId] = useState<number | null>(null);

    const debouncedSearch = useDebounce(searchMember, 300);

    const { payments, totalPages, totalElements, isLoading, error } = useGetPayments(
        currentPage, 10, statusFilter, startDate, endDate, selectedMemberId
    );

    const { suggestions: memberSuggestions, setSuggestions: setMemberSuggestions } = useSearchMembers(
        selectedMemberId ? "" : debouncedSearch // Se já selecionou um ID, não pesquisa mais
    );

    useEffect(() => {
        if (!debouncedSearch || selectedMemberId) {
            return;
        }

        async function fetchMembers() {
            try {
                const res = await fetch(`${import.meta.env.VITE_API_URL}/v1/api/members/search?name=${debouncedSearch}`);

                if (res.ok) {
                    const data = await res.json();

                    setMemberSuggestions(data.content || data);
                }
            } catch (err) {
                console.error("Erro ao buscar sugestões de alunos", err);
            }
        }
        fetchMembers();
    }, [debouncedSearch, selectedMemberId, setMemberSuggestions]);

    const handlePageChange = (newPage: number) => {
        navigate({ search: (prev: any) => ({ ...prev, page: newPage }) })
    }

    const handleTabChange = (newStatus: string) => {
        setStatusFilter(newStatus);
        handlePageChange(0);
    }

    const handleSearchChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setSearchMember(value);

        if (!value) {
            setMemberSuggestions([]);
        }

        if (selectedMemberId) setSelectedMemberId(null);
        handlePageChange(0);
    }

    const selectMember = (id: number, name: string) => {
        setSelectedMemberId(id);
        setSearchMember(name);
        setMemberSuggestions([]);
        handlePageChange(0);
    }

    const clearMemberFilter = () => {
        setSelectedMemberId(null);
        setSearchMember("");
        setMemberSuggestions([]);
        handlePageChange(0);
    }

    return (
        <div className="flex flex-col gap-4">

            {/* ABAS DE STATUS */}
            <div className="flex space-x-2 overflow-x-auto pb-1">
                {[
                    { id: "ALL", label: "Todos" },
                    { id: "PENDING", label: "Pendentes" },
                    { id: "PAID", label: "Pagos" },
                    { id: "OVERDUE", label: "Atrasados" }
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

            {/* BARRA DE FILTROS (Novo) */}
            <div className="bg-white p-4 rounded-xl border border-gray-100 shadow-sm flex flex-col md:flex-row gap-4 items-end md:items-center justify-start relative z-10">

                {/* Filtro de Datas */}
                <div className="flex items-center gap-2">
                    <Calendar className="w-4 h-4 text-gray-400" />
                    <label className="text-sm font-medium text-gray-600">Período:</label>
                    <div className="flex items-center gap-2">
                        <input
                            type="date"
                            value={startDate}
                            onChange={(e) => { setStartDate(e.target.value); handlePageChange(0); }}
                            className="px-3 py-2 border border-gray-200 rounded-lg text-sm bg-gray-50 outline-none focus:ring-2 focus:ring-blue-500"
                        />
                        <span className="text-gray-400 text-sm">até</span>
                        <input
                            type="date"
                            value={endDate}
                            onChange={(e) => { setEndDate(e.target.value); handlePageChange(0); }}
                            className="px-3 py-2 border border-gray-200 rounded-lg text-sm bg-gray-50 outline-none focus:ring-2 focus:ring-blue-500"
                        />
                    </div>
                </div>

                {/* Divisor Visual */}
                <div className="hidden md:block w-px h-8 bg-gray-200 mx-2"></div>

                {/* Filtro de Aluno (Combobox Híbrido) */}
                <div className="flex items-center gap-2 relative w-full md:w-auto">
                    <User className="w-4 h-4 text-gray-400" />
                    <label className="text-sm font-medium text-gray-600">Aluno:</label>
                    <div className="relative flex-1 md:w-64">
                        <input
                            type="text"
                            placeholder="Buscar por nome..."
                            value={searchMember}
                            onChange={handleSearchChange}
                            className={`w-full px-3 py-2 border rounded-lg text-sm bg-gray-50 outline-none focus:ring-2 focus:ring-blue-500 pr-8 ${selectedMemberId ? 'border-blue-300 ring-1 ring-blue-100' : 'border-gray-200'}`}
                        />
                        {searchMember ? (
                            <button onClick={clearMemberFilter} className="absolute right-2 top-2.5 text-gray-400 hover:text-gray-600">
                                <X className="w-4 h-4" />
                            </button>
                        ) : (
                            <Search className="absolute right-2 top-2.5 w-4 h-4 text-gray-400" />
                        )}

                        {/* Menu de Sugestões Flutuante */}
                        {memberSuggestions.length > 0 && (
                            <div className="absolute top-full left-0 w-full mt-1 bg-white border border-gray-200 rounded-lg shadow-lg overflow-hidden z-50 max-h-48 overflow-y-auto">
                                {memberSuggestions.map(member => (
                                    <div
                                        key={member.id}
                                        onClick={() => selectMember(member.id, member.name)}
                                        className="px-3 py-2 text-sm text-gray-700 hover:bg-blue-50 cursor-pointer border-b border-gray-50 last:border-0"
                                    >
                                        {member.name}
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* TABELA PRINCIPAL (Mantida igual, apenas atualizei o z-index para não sobrepor o combobox) */}
            <div className="rounded-xl border border-gray-100 bg-white flex flex-col min-h-[calc(100vh-400px)] shadow-sm relative z-0">

                {isLoading && payments.length === 0 ? (
                    <div className="p-8 text-center text-gray-500 flex-1 flex items-center justify-center">A carregar mensalidades...</div>
                ) : error ? (
                    <div className="p-8 text-center text-red-500 flex-1 flex items-center justify-center">Erro: {error}</div>
                ) : payments.length === 0 ? (
                    <div className="p-8 text-center text-gray-500 flex-1 flex items-center justify-center flex-col gap-2">
                        <p>Nenhum pagamento encontrado.</p>
                        <p className="text-xs text-gray-400">Tente ajustar os filtros de data, aluno ou status acima.</p>
                    </div>
                ) : (
                    <div className="flex-1">
                        <Table>
                            <TableHeader className="bg-gray-50/50">
                                <TableRow>
                                    <TableHead className="font-semibold text-gray-600">Aluno</TableHead>
                                    <TableHead className="font-semibold text-gray-600">Plano</TableHead>
                                    <TableHead className="font-semibold text-gray-600">Vencimento</TableHead>
                                    <TableHead className="font-semibold text-gray-600">Pagamento</TableHead>
                                    <TableHead className="font-semibold text-gray-600">Valor</TableHead>
                                    <TableHead className="font-semibold text-gray-600 text-center">Status</TableHead>
                                    <TableHead className="text-right font-semibold text-gray-600">Ação</TableHead>
                                </TableRow>
                            </TableHeader>
                            <TableBody>
                                {payments.map((p) => (
                                    <TableRow key={p.id} className="border-gray-100 hover:bg-gray-50/50 transition-colors">
                                        <TableCell className="font-medium text-gray-800">{p.memberName}</TableCell>
                                        <TableCell className="text-gray-600">{p.planName}</TableCell>
                                        <TableCell className="text-gray-600">{p.dueDate}</TableCell>
                                        <TableCell className="text-gray-600">{p.paymentDate || '-'}</TableCell>
                                        <TableCell className="text-gray-800 font-semibold">
                                            R$ {(p.amountPaid || p.amountDue)?.toFixed(2)}
                                        </TableCell>
                                        <TableCell className="text-center">
                                            {getPaymentStatusBadge(p.status)}
                                        </TableCell>
                                        <TableCell className="text-right">
                                            <div className="flex justify-end items-center gap-2">
                                                {p.status !== 'PAID' && p.status !== 'CANCELED' ? (
                                                    <ConfirmPaymentModal
                                                        paymentId={p.id}
                                                        memberName={p.memberName}
                                                        paymentValue={p.amountDue}
                                                    />
                                                ) : (
                                                    <span className="text-xs text-gray-400 italic mr-2">Liquidado</span>
                                                )}
                                            </div>
                                        </TableCell>
                                    </TableRow>
                                ))}
                            </TableBody>
                        </Table>
                    </div>
                )}

                {/* RODAPÉ E PAGINAÇÃO */}
                {totalPages > 0 && !isLoading && (
                    <div className="flex items-center justify-between px-6 py-4 border-t border-gray-100 bg-gray-50/30">
                        <span className="text-sm text-gray-500 font-medium">
                            Página {currentPage + 1} de {totalPages} <span className="text-gray-400 font-normal">({totalElements} registros)</span>
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