export interface PaymentFee {
    id: number;
    paymentMethod: string;
    percentageFee: number;
    fixedFee: number;
    daysToReceive: number;
}

export interface UpdatePaymentFee {
    percentageFee?: number;
    fixedFee?: number;
    daysToReceive?: number;
}