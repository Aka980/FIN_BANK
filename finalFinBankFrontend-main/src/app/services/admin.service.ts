import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AdminCustomerProfile, AdminLoan } from '../models/admin.model';

@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private readonly apiBaseUrl = 'http://localhost:8080';

  constructor(private readonly http: HttpClient) { }

  register(payload: { username: string; password: string; fullName: string }): Observable<string> {
    const registerPayload = {
      username: payload.username,
      password: payload.password,
      fullName: payload.fullName,
      full_name: payload.fullName
    };
    return this.http.post(`${this.apiBaseUrl}/auth/register`, registerPayload, { responseType: 'text' });
  }

  login(payload: { username: string; password: string }): Observable<string> {
    return this.http.post(`${this.apiBaseUrl}/auth/login`, payload, { responseType: 'text' });
  }

  deleteLoanEmi(applicationNo: number): Observable<string> {
    return this.http.delete(`${this.apiBaseUrl}/admin/delete-emi/${applicationNo}`, { responseType: 'text' });
  }

  ping(): Observable<string> {
    return this.http.get(`${this.apiBaseUrl}/admin/ping`, { responseType: 'text' });
  }

  getAllLoans(): Observable<AdminLoan[]> {
    return this.http.get<AdminLoan[]>(`${this.apiBaseUrl}/admin/loans`);
  }

  getCustomerProfile(accountNo: number): Observable<AdminCustomerProfile> {
    return this.http.get<AdminCustomerProfile>(`${this.apiBaseUrl}/admin/customer-profile/${accountNo}`);
  }

  approveLoan(applicationId: number): Observable<string> {
    const params = new HttpParams().set('applicationId', applicationId.toString());
    return this.http.put(`${this.apiBaseUrl}/admin/approve`, null, { params, responseType: 'text' });
  }

  denyLoan(applicationId: number, reason: string): Observable<string> {
    const params = new HttpParams()
      .set('applicationId', applicationId.toString())
      .set('reason', reason);
    return this.http.put(`${this.apiBaseUrl}/admin/deny`, null, { params, responseType: 'text' });
  }

  abeyanceLoan(applicationId: number): Observable<string> {
    const params = new HttpParams().set('applicationId', applicationId.toString());
    return this.http.put(`${this.apiBaseUrl}/admin/abeyance`, null, { params, responseType: 'text' });
  }
}
