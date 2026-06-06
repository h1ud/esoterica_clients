export interface User {
  nombre: string;
  apellido: string;
  dni: string;
  fechaNacimiento: string;
  password: string;
}

export type SessionRole = 'guest' | 'client' | 'admin';

export interface UserSession {
  role: SessionRole;
  token?: string;
  id?: number;
  name?: string;
  dni?: string;
  birthdayDate?: string;
  weeklyProductCount?: number;
  vip?: boolean;
  vipSince?: string | null;
  vipRequiredProducts?: number;
  vipRemainingProducts?: number;
  usedOffersCount?: number;
}

export interface ClientSummary {
  id: number;
  name: string;
  dni: string;
  birthdayDate: string;
  role: Exclude<SessionRole, 'guest'>;
  weeklyProductCount: number;
  vip: boolean;
  vipSince: string | null;
  vipRequiredProducts: number;
  vipRemainingProducts: number;
  usedOffersCount: number;
}
