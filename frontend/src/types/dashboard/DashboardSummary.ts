
import { type MonthlyChartData } from '../../types/dashboard/MonthlyChartData';
import { type CashFlow } from '../../types/finances/CashFlow';

export interface DashboardSummary {
    finance: CashFlow;
    activeMembers: number;
    delinquentMembers: number;
    pendingMembers: number;
    chartData: MonthlyChartData[];
}