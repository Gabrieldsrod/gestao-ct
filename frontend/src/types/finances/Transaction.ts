export interface Transaction {
    id: number;
    description: string;
    category: string;
    transactionType: 'EXPENSE' | 'INCOME';
    paymentMethod: 'CASH' | 'CREDIT_CARD' | 'DEBIT_CARD' | 'PIX' | 'SLIP';
    transactionDate: string;
    amount: number;
}