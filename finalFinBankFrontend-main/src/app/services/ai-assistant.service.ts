import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AiAssistantService {
  private readonly apiBaseUrl = 'http://localhost:8080/ai';

  constructor(private readonly http: HttpClient) { }

  testConnection(): Observable<string> {
    return this.http.get(`${this.apiBaseUrl}/test`, { responseType: 'text' });
  }

  getLoanSummary(loanId: number): Observable<string> {
    return this.http.get(`${this.apiBaseUrl}/loan-analysis/${loanId}`, { responseType: 'text' });
  }

  chatWithLoan(loanId: number, message: string): Observable<string> {
    return this.http.post(`${this.apiBaseUrl}/chat/${loanId}`, { message }, { responseType: 'text' });
  }
}
