import * as z from "zod"

export const memberSchema = z.object({
    name: z.string().min(3, "O nome deve ter pelo menos 3 letras"),
    whatsapp: z.string().min(10, "O número de WhatsApp deve ter pelo menos 10 dígitos"),
    email: z.email({ message: "E-mail inválido" }).or(z.literal('')),
    birthDate: z.string().min(1, "A data de nascimento é obrigatória"),
    planId: z.number().min(1, "Selecione um plano válido"),

    dependentName: z.string().optional(),
    dependentWhatsapp: z.string().optional(),
    dependentEmail: z.email({ message: "E-mail inválido" }).or(z.literal('')).optional(),
    dependentBirthDate: z.string().optional()
})

export type MemberFormValues = z.infer<typeof memberSchema>