import { computed, Injectable, inject, signal } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { catchError, map, Observable, of, tap, throwError } from 'rxjs';
import { ClientSummary, SessionRole, User, UserSession } from '../models/user';

const SESSION_KEY = 'esoterica.session';

interface BackendAuthResponse {
  token: string;
  id: number;
  name: string;
  dni: string;
  birthdayDate: string;
  role?: SessionRole;
  weeklyProductCount?: number;
  vip?: boolean;
  vipSince?: string | null;
  vipRequiredProducts?: number;
  vipRemainingProducts?: number;
  usedOffersCount?: number;
}

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = 'http://localhost:8080';

  readonly session = signal<UserSession | null>(this.readSession());
  readonly isGuest = computed(() => this.session()?.role === 'guest');
  readonly isClient = computed(() => this.session()?.role === 'client');
  readonly isAdmin = computed(() => this.session()?.role === 'admin');
  readonly currentUser = computed<User | null>(() => {
    const session = this.session();
    if (!session || session.role === 'guest') return null;

    const nameParts = (session.name ?? '').split(' ');
    const nombre = nameParts[0] || '';
    const apellido = nameParts.slice(1).join(' ') || '';

    return {
      nombre,
      apellido,
      dni: session.dni ?? '',
      fechaNacimiento: session.birthdayDate ?? '',
      password: '',
    };
  });

  register(user: User): Observable<boolean> {
    const backendClient = {
      name: `${user.nombre} ${user.apellido}`.trim(),
      dni: user.dni,
      passwordHash: user.password,
      birthdayDate: user.fechaNacimiento,
    };

    return this.http.post<BackendAuthResponse>(`${this.apiUrl}/register`, backendClient).pipe(
      tap((res) => {
        if (res && res.token) {
          this.setSession({
            role: this.normalizeRole(res.role),
            token: res.token,
            id: res.id,
            name: res.name,
            dni: res.dni,
            birthdayDate: res.birthdayDate,
            weeklyProductCount: res.weeklyProductCount,
            vip: res.vip,
            vipSince: res.vipSince,
            vipRequiredProducts: res.vipRequiredProducts,
            vipRemainingProducts: res.vipRemainingProducts,
            usedOffersCount: res.usedOffersCount,
          });
        }
      }),
      map((res) => !!res && !!res.token),
      catchError((error: HttpErrorResponse) => this.handleLoginError(error))
    );
  }

  login(dni: string, password: string): Observable<boolean> {
    const backendClient = {
      dni,
      passwordHash: password,
    };

    return this.http.post<BackendAuthResponse>(`${this.apiUrl}/login`, backendClient).pipe(
      tap((res) => {
        if (res && res.token) {
          this.setSession({
            role: this.normalizeRole(res.role),
            token: res.token,
            id: res.id,
            name: res.name,
            dni: res.dni,
            birthdayDate: res.birthdayDate,
            weeklyProductCount: res.weeklyProductCount,
            vip: res.vip,
            vipSince: res.vipSince,
            vipRequiredProducts: res.vipRequiredProducts,
            vipRemainingProducts: res.vipRemainingProducts,
            usedOffersCount: res.usedOffersCount,
          });
        }
      }),
      map((res) => !!res && !!res.token),
      catchError(() => of(false))
    );
  }

  enterAsGuest(): void {
    this.setSession({ role: 'guest' });
  }

  logout(): void {
    this.storage?.removeItem(SESSION_KEY);
    this.session.set(null);
  }

  hasSession(): boolean {
    return this.session() !== null;
  }

  syncClientSession(client: ClientSummary): void {
    const current = this.session();
    if (!current || current.role === 'guest') return;

    this.setSession({
      ...current,
      role: this.normalizeRole(client.role),
      id: client.id,
      name: client.name,
      dni: client.dni,
      birthdayDate: client.birthdayDate,
      weeklyProductCount: client.weeklyProductCount,
      vip: client.vip,
      vipSince: client.vipSince,
      vipRequiredProducts: client.vipRequiredProducts,
      vipRemainingProducts: client.vipRemainingProducts,
      usedOffersCount: client.usedOffersCount,
    });
  }

  private setSession(session: UserSession): void {
    this.storage?.setItem(SESSION_KEY, JSON.stringify(session));
    this.session.set(session);
  }

  private readSession(): UserSession | null {
    const rawSession = this.storage?.getItem(SESSION_KEY);
    if (!rawSession) return null;
    try {
      return JSON.parse(rawSession) as UserSession;
    } catch {
      this.storage?.removeItem(SESSION_KEY);
      return null;
    }
  }

  private get storage(): Storage | null {
    return typeof localStorage === 'undefined' ? null : localStorage;
  }

  private normalizeRole(role: string | undefined): SessionRole {
    return role?.toLowerCase() === 'admin' ? 'admin' : 'client';
  }

  private handleLoginError(error: HttpErrorResponse): Observable<boolean> {
    if (error.status >= 400 && error.status < 500) return of(false);

    return throwError(() => error);
  }
}
