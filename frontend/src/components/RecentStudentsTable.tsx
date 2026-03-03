// src/components/RecentStudentsTable.tsx
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table"
import { Badge } from "@/components/ui/badge"

// Dados simulados (No futuro, virão do seu Spring Boot)
const recentStudents = [
  { id: "1", name: "João Silva", plan: "Plano Casal", status: "ACTIVE", date: "02 Mar 2026" },
  { id: "2", name: "Maria Oliveira", plan: "Plano Casal", status: "ACTIVE", date: "02 Mar 2026" },
  { id: "3", name: "Carlos Santos", plan: "Mensal Padrão", status: "DELINQUENT", date: "28 Fev 2026" },
  { id: "4", name: "Ana Costa", plan: "Trimestral", status: "PENDING", date: "02 Mar 2026" },
  { id: "5", name: "Pedro Mendes", plan: "Mensal Padrão", status: "INACTIVE", date: "15 Jan 2026" },
]

// Função para dar cores diferentes aos status
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

export function RecentStudentsTable() {
  return (
    <div className="rounded-md border border-gray-100">
      <Table>
        <TableHeader className="bg-gray-50/50">
          <TableRow>
            <TableHead className="font-semibold text-gray-600">Aluno</TableHead>
            <TableHead className="font-semibold text-gray-600">Plano</TableHead>
            <TableHead className="font-semibold text-gray-600">Data de Matrícula</TableHead>
            <TableHead className="text-right font-semibold text-gray-600">Status</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {recentStudents.map((student) => (
            <TableRow key={student.id} className="hover:bg-gray-50/50 transition-colors">
              <TableCell className="font-medium text-gray-800">{student.name}</TableCell>
              <TableCell className="text-gray-600">{student.plan}</TableCell>
              <TableCell className="text-gray-600">{student.date}</TableCell>
              <TableCell className="text-right">
                {getStatusBadge(student.status)}
              </TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  )
}