import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogDescription } from "@/components/ui/dialog"
import { Badge } from "@/components/ui/badge"
import { Calendar, CreditCard, Mail, Phone, User, Clock, ShieldAlert } from "lucide-react"
import type { Member } from "@/types/member/Member"

function formatDateTime(isoString?: string | null) {
  if (!isoString) return '-';
  try {
    const date = new Date(isoString);
    return new Intl.DateTimeFormat('pt-BR', { dateStyle: 'short', timeStyle: 'short' }).format(date);
  } catch {
    return isoString;
  }
}

function formatDateOnly(dateString?: string | null) {
  if (!dateString) return '-';
  if (dateString.includes('-')) {
    const [ano, mes, dia] = dateString.split('-');
    return `${dia}/${mes}/${ano}`;
  }
  return dateString;
}

interface MemberDetailsModalProps {
  member: Member | null;
  open: boolean;
  onOpenChange: (open: boolean) => void;
}

export function MemberDetailsModal({ member, open, onOpenChange }: MemberDetailsModalProps) {
  if (!member) return null;

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-2xl bg-white">
        <DialogHeader>
          <DialogTitle className="text-2xl font-bold text-gray-800 flex items-center gap-2">
            <User className="w-6 h-6 text-blue-600" />
            Perfil do Aluno
          </DialogTitle>
          <DialogDescription>
            Visão detalhada do cadastro, plano e histórico do aluno no CT.
          </DialogDescription>
        </DialogHeader>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-6 mt-4">
          <div className="space-y-4">
            <div className="bg-gray-50 p-4 rounded-lg border border-gray-100 space-y-3">
              <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-2">Dados Pessoais</h3>
              
              <div>
                <p className="text-xs text-gray-500">Nome Completo</p>
                <p className="font-medium text-gray-900">{member.name}</p>
              </div>

              <div className="flex items-center gap-2">
                <Phone className="w-4 h-4 text-gray-400" />
                <div>
                  <p className="text-xs text-gray-500">WhatsApp</p>
                  <p className="text-sm font-medium text-gray-900">{member.whatsapp || 'Não informado'}</p>
                </div>
              </div>

              <div className="flex items-center gap-2">
                <Mail className="w-4 h-4 text-gray-400" />
                <div>
                  <p className="text-xs text-gray-500">E-mail</p>
                  <p className="text-sm font-medium text-gray-900">{member.email || 'Não informado'}</p>
                </div>
              </div>
            </div>

            {member.holderName && (
              <div className="bg-blue-50 p-4 rounded-lg border border-blue-100">
                <div className="flex items-center gap-2">
                  <User className="w-4 h-4 text-blue-500" />
                  <div>
                    <p className="text-xs text-blue-600 font-semibold uppercase">Dependente de</p>
                    <p className="text-sm font-medium text-blue-900">{member.holderName}</p>
                  </div>
                </div>
              </div>
            )}
          </div>

          <div className="space-y-4">
            <div className="bg-gray-50 p-4 rounded-lg border border-gray-100 space-y-3">
              <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-2">Assinatura</h3>
              
              <div className="flex items-center gap-2">
                <CreditCard className="w-4 h-4 text-gray-400" />
                <div>
                  <p className="text-xs text-gray-500">Plano Atual</p>
                  <p className="text-sm font-medium text-gray-900">{member.plan?.name || "Sem plano"}</p>
                </div>
              </div>

              <div className="flex items-center gap-2">
                <ShieldAlert className="w-4 h-4 text-gray-400" />
                <div>
                  <p className="text-xs text-gray-500">Status Atual</p>
                  <Badge variant="outline" className="mt-0.5">{member.status}</Badge>
                </div>
              </div>
            </div>

            <div className="bg-gray-50 p-4 rounded-lg border border-gray-100 space-y-3">
              <h3 className="text-sm font-semibold text-gray-500 uppercase tracking-wider mb-2">Datas de Sistema</h3>
              
              <div className="grid grid-cols-2 gap-3">
                <div className="flex items-start gap-2">
                  <Calendar className="w-4 h-4 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-xs text-gray-500">Matrícula</p>
                    <p className="text-sm font-medium text-gray-900">{formatDateOnly(member.registrationDate)}</p>
                  </div>
                </div>

                {member.inactivationDate && (
                  <div className="flex items-start gap-2">
                    <Calendar className="w-4 h-4 text-red-400 mt-0.5" />
                    <div>
                      <p className="text-xs text-red-500">Inativado em</p>
                      <p className="text-sm font-medium text-red-900">{formatDateOnly(member.inactivationDate)}</p>
                    </div>
                  </div>
                )}

                <div className="flex items-start gap-2">
                  <Clock className="w-4 h-4 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-xs text-gray-500">Criado em</p>
                    <p className="text-sm font-medium text-gray-900">{formatDateTime(member.createdAt)}</p>
                  </div>
                </div>

                <div className="flex items-start gap-2">
                  <Clock className="w-4 h-4 text-gray-400 mt-0.5" />
                  <div>
                    <p className="text-xs text-gray-500">Atualizado em</p>
                    <p className="text-sm font-medium text-gray-900">{formatDateTime(member.updatedAt)}</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}