import { z } from "zod";

export const categorySchema = z.object({
  name: z.string().min(2, "O nome deve ter pelo menos 2 caracteres."),
  type: z.enum(["INCOME", "EXPENSE"]).describe("O tipo de categoria é obrigatório."),
});

export type CategoryFormValues = z.infer<typeof categorySchema>;

export const transactionSchema = z.object({
  transactionType: z.enum(["INCOME", "EXPENSE"]),
  amount: z.string()
    .min(1, "O valor é obrigatório.")
    .refine((val) => {
      const parsed = parseFloat(val.replace(',', '.'));
      return !isNaN(parsed) && parsed > 0;
    }, "Informe um valor maior que zero."),

  description: z.string().min(3, "A descrição deve ter pelo menos 3 caracteres."),

  categoryId: z.number({ message: "Selecione uma categoria." }).min(1, "Selecione uma categoria."),

  transactionDate: z.string().min(10, "Informe uma data válida."),

  paymentMethod: z.enum(["CASH", "CREDIT_CARD", "DEBIT_CARD", "PIX", "SLIP"]).describe("Selecione uma forma de pagamento."),
});

export type TransactionFormValues = z.infer<typeof transactionSchema>;