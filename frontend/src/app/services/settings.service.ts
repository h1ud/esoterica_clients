import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AuthService } from './auth.service';

export interface VipSettings {
  vipRequiredProducts: number;
  updatedAt: string;
}

@Injectable({
  providedIn: 'root',
})
export class SettingsService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly apiUrl = 'http://localhost:8080/api/settings';

  getVipSettings(): Observable<VipSettings> {
    return this.http.get<VipSettings>(`${this.apiUrl}/vip`);
  }

  updateVipSettings(vipRequiredProducts: number): Observable<VipSettings> {
    return this.http.put<VipSettings>(
      `${this.apiUrl}/vip`,
      { vipRequiredProducts },
      this.authOptions()
    );
  }

  private authOptions(): { headers?: HttpHeaders } {
    const token = this.authService.session()?.token;
    return token
      ? { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) }
      : {};
  }
}
