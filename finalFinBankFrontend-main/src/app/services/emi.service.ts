import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import {
  EmiFullHistoryResponse,
  EmiNextDueResponse,
  EmiOutstandingResponse,
  EmiPaymentHistory,
  EmiPaymentRequest
} from '../models/emi.model';

@Injectable({
  providedIn: 'root'
})
export class EmiService {
  private readonly apiBaseUrl = 'http://localhost:8080/emi';

  constructor(private readonly http: HttpClient) { }

  calculateEmi(loanId: number): Observable<unknown> {
    return this.http.post(`${this.apiBaseUrl}/calculate/${loanId}`, {});
  }

  getDetails(loanId: number): Observable<EmiFullHistoryResponse> {
    return this.http.get<EmiFullHistoryResponse>(`${this.apiBaseUrl}/details/${loanId}`);
  }

  getOutstanding(loanId: number): Observable<EmiOutstandingResponse> {
    return this.http.get<EmiOutstandingResponse>(`${this.apiBaseUrl}/outstanding/${loanId}`);
  }

  getNextDue(loanId: number): Observable<EmiNextDueResponse> {
    return this.http.get<EmiNextDueResponse>(`${this.apiBaseUrl}/next-due/${loanId}`);
  }

  getPaymentHistory(loanId: number): Observable<EmiPaymentHistory[]> {
    return this.http.get<EmiPaymentHistory[]>(`${this.apiBaseUrl}/history/${loanId}`).pipe(
      // Backend model has `PaymentId`, normalize it for template usage.
      map((items) => items.map((item) => ({
        ...item,
        paymentId: item.paymentId ?? (item as { PaymentId?: number }).PaymentId ?? 0
      })))
    );
  }

  recordPayment(request: EmiPaymentRequest): Observable<EmiPaymentHistory> {
    return this.http.post<EmiPaymentHistory>(`${this.apiBaseUrl}/record`, request).pipe(
      map((item) => ({
        ...item,
        paymentId: item.paymentId ?? (item as { PaymentId?: number }).PaymentId ?? 0
      }))
    );
  }
}
