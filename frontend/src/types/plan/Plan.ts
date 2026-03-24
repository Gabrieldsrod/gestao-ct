export interface Plan {
  id: number;
  name: string;
  price: number;
  lastUpdated: string;
  activeMembers: number;
}

export type PlanCreate = {
  name: string;
  price: number;
};
