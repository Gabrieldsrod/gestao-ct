import { Users, UserX, TrendingUp, Wallet } from 'lucide-react'
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"

interface StatCardProps {
    title: string;
    value: string | number;
    icon: React.ReactNode;
    description?: string;
    color: string;
}

function StatCard({ title, value, icon, description, color }: StatCardProps) {
    return (
        <Card className="bg-white border-gray-100 shadow-sm">
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                <CardTitle className="text-sm font-medium text-muted-foreground">{title}</CardTitle>
                <div className={`p-2 bg-muted/50 rounded-lg border border-border flex items-center justify-center ${color}`}>
                    {icon}
                </div>
            </CardHeader>
            <CardContent>
                <div className="text-3xl font-bold tracking-tight text-gray-900">{value}</div>
                {description && (
                    <p className="text-xs text-muted-foreground mt-1">{description}</p>
                )}
            </CardContent>
        </Card>
    )
}

interface DashboardStatCardsProps {
    activeMembers: number;
    delinquentMembers: number;
    totalIncome: number;
    netBalance: number;
}

export function DashboardStatCards({ activeMembers, delinquentMembers, totalIncome, netBalance }: DashboardStatCardsProps) {

    const formatBRL = (value: number) => {
        return new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' }).format(value)
    }

    return (
        <div className="grid gap-6 grid-cols-1 md:grid-cols-2 lg:grid-cols-4">
            <StatCard
                title="Alunos Ativos"
                value={activeMembers}
                icon={<Users className="h-5 w-5" />}
                color="text-blue-600"
            />
            <StatCard
                title="Inadimplentes"
                value={delinquentMembers}
                icon={<UserX className="h-5 w-5" />}
                color="text-red-600"
            />
            <StatCard
                title="Receita Mensal"
                value={formatBRL(totalIncome)}
                icon={<TrendingUp className="h-5 w-5" />}
                color="text-green-600"
            />
            <StatCard
                title="Saldo Líquido"
                value={formatBRL(netBalance)}
                icon={<Wallet className="h-5 w-5" />}
                color="text-emerald-600"
            />
        </div>
    )
}