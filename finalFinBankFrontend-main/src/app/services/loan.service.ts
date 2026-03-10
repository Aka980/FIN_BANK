import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LoanRequest, LoanResponse } from '../models/loan.model';

@Injectable({
    providedIn: 'root'
})
export class LoanService {
    private apiUrl = 'http://localhost:8080/loans';

    constructor(private http: HttpClient) { }

    applyLoan(request: LoanRequest): Observable<LoanResponse> {
        return this.http.post<LoanResponse>(`${this.apiUrl}/apply`, request);
    }

    getLoans(): Observable<LoanResponse[]> {
        return this.http.get<LoanResponse[]>(`${this.apiUrl}/all`);
    }

    getLoanById(id: number): Observable<LoanResponse> {
        return this.http.get<LoanResponse>(`${this.apiUrl}/id/${id}`);
    }

    updateLoan(id: number, request: LoanRequest): Observable<LoanResponse> {
        return this.http.put<LoanResponse>(`${this.apiUrl}/update/${id}`, request);
    }

    cancelLoan(id: number): Observable<string> {
        return this.http.delete(`${this.apiUrl}/cancel/${id}`, { responseType: 'text' });
    }

    updateLoanStatus(applicationId: number, status: string, reason?: string): Observable<LoanResponse> {
        let params = `?applicationId=${applicationId}&status=${status}`;
        if (reason) {
            params += `&reason=${reason}`;
        }
        return this.http.put<LoanResponse>(`${this.apiUrl}/status${params}`, {});
    }
}
