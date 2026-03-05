import { type Plan } from "./Plan";

export interface Member {
  id: number;
  name: string;
  whatsapp: string;
  email: string;
  plan: Plan;
  status: string;
  registrationDate: string;
  holderName?: string | null;
}