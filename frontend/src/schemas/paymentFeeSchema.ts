import { z } from 'zod';

export const updateFeeSchema = z.object({
    percentageFee: z.number().min(0, "A taxa não pode ser negativa"),
    
    fixedFee: z.number().min(0, "A taxa não pode ser negativa"),
    
    daysToReceive: z.number().int("Deve ser um número inteiro").min(0, "O prazo não pode ser negativo")
});

export type UpdateFeeFormValues = z.infer<typeof updateFeeSchema>;