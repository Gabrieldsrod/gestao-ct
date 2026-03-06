export interface Payment {
    id: number;
    memberId: number;
    memberName: string;
    memberEmail: string;
    memberPhone: string;
    planName: string;
    dueDate: string;
    paymentDate: string | null;
    amountDue: number;
    amountPaid: number | null;
    status: 'PENDING' | 'PAID' | 'OVERDUE' | 'CANCELED';
}