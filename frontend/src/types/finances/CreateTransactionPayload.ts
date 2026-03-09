export interface CreateTransactionPayload {
    description: string;
    amount: number;
    transactionType: 'INCOME' | 'EXPENSE';
    paymentMethod: string;
    transactionDate: string;
    categoryId: number;
}