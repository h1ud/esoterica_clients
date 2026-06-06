import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

import { AuthService } from './auth.service';
import { ClientSummary, SessionRole } from '../models/user';

export interface AdminClientUpdate {
  name?: string;
  dni?: string;
  birthdayDate?: string;
  role?: Exclude<SessionRole, 'guest'>;
  weeklyProductCount?: number;
  vip?: boolean;
}

@Injectable({
  providedIn: 'root',
})
export class ClientService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly apiUrl = 'http://localhost:8080/api/clients';

  getMe(): Observable<ClientSummary> {
    return this.http.get<ClientSummary>(`${this.apiUrl}/me`, this.authOptions()).pipe(
      tap((client) => this.authService.syncClientSession(client))
    );
  }

  getClients(): Observable<ClientSummary[]> {
    return this.http.get<ClientSummary[]>(this.apiUrl, this.authOptions());
  }

  updateClient(id: number, payload: AdminClientUpdate): Observable<ClientSummary> {
    return this.http.put<ClientSummary>(`${this.apiUrl}/${id}`, payload, this.authOptions());
  }

  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, this.authOptions());
  }

  private authOptions(): { headers?: HttpHeaders } {
    const token = this.authService.session()?.token;
    return token
      ? { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) }
      : {};
  }
}
