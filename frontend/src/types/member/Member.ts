import type { Plan } from "../plan/Plan";

export interface Member {
  id: number;
  name: string;
  whatsapp: string;
  email: string;
  plan: Plan;
  status: string;
  registrationDate: string;
  inactivationDate?: string | null; // Novo
  createdAt?: string;               // Novo
  updatedAt?: string;               // Novo
  holderName?: string | null;
}