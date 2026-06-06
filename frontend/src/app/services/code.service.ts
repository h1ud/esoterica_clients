import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

import { AuthService } from './auth.service';
import { ClientSummary } from '../models/user';

export interface Code {
  id: number;
  title: string;
  description: string;
  valueCode: number;
  discountCode: number;
  visibility: string;
  isActive: boolean;
  isUsed: boolean;
  expiration: string;
  createdAt: string;
}

export interface CodeActivation {
  id: number;
  codeId: number;
  codeTitle: string;
  productQuantity: number;
  activatedAt: string;
}

export interface OfferActivationResponse {
  activation: CodeActivation;
  client: ClientSummary;
  vipUnlocked: boolean;
  message: string;
}

@Injectable({
  providedIn: 'root',
})
export class CodeService {
  private readonly http = inject(HttpClient);
  private readonly authService = inject(AuthService);
  private readonly apiUrl = 'http://localhost:8080/api/code';

  getCodes(): Observable<Code[]> {
    return this.http.get<Code[]>(this.apiUrl, this.authOptions());
  }

  createCode(code: Partial<Code>): Observable<Code> {
    return this.http.post<Code>(this.apiUrl, code, this.authOptions());
  }

  updateCode(id: number, code: Partial<Code>): Observable<Code> {
    return this.http.put<Code>(`${this.apiUrl}/${id}`, code, this.authOptions());
  }

  deleteCode(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`, this.authOptions());
  }

  activateCode(id: number, productQuantity: number): Observable<OfferActivationResponse> {
    return this.http.post<OfferActivationResponse>(
      `${this.apiUrl}/${id}/activate`,
      { productQuantity },
      this.authOptions()
    );
  }

  getMyActivations(): Observable<CodeActivation[]> {
    return this.http.get<CodeActivation[]>(`${this.apiUrl}/activations/me`, this.authOptions());
  }

  private authOptions(): { headers?: HttpHeaders } {
    const token = this.authService.session()?.token;
    return token
      ? { headers: new HttpHeaders({ Authorization: `Bearer ${token}` }) }
      : {};
  }
}
