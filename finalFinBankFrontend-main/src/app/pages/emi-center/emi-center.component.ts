import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, forkJoin, of } from 'rxjs';
import { LoanResponse } from '../../models/loan.model';
import {
  EmiFullHistoryResponse,
  EmiNextDueResponse,
  EmiOutstandingResponse,
  EmiPaymentHistory
} from '../../models/emi.model';
import { LoanService } from '../../services/loan.service';
import { EmiService } from '../../services/emi.service';

@Component({
  selector: 'app-emi-center',
  standalone: false,
  templateUrl: './emi-center.component.html',
  styleUrl: './emi-center.component.css'
})
export class EmiCenterComponent implements OnInit {
  loans: LoanResponse[] = [];
  selectedLoanId: number | null = null;
  selectedLoan?: LoanResponse;
  payableAmount = 0;
  today = new Date();

  emiDetails?: EmiFullHistoryResponse;
  outstanding?: EmiOutstandingResponse;
  nextDue?: EmiNextDueResponse;
  paymentHistory: EmiPaymentHistory[] = [];

  loadingLoans = false;
  loadingEmiData = false;
  generatingEmiPlan = false;
  needsEmiPlan = false;

  errorMessage = '';
  infoMessage = '';
  successMessage = '';

  constructor(
    private readonly loanService: LoanService,
    private readonly emiService: EmiService,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.fetchLoans();
  }

  onLoanChange(rawLoanId: string | number | null): void {
    const parsedLoanId = Number(rawLoanId);
    this.selectedLoanId = Number.isFinite(parsedLoanId) ? parsedLoanId : null;
    this.selectedLoan = this.loans.find((loan) => loan.applicationNo === this.selectedLoanId);
    this.loadEmiData();
  }

  refresh(): void {
    this.loadEmiData();
  }

  initializeEmiPlan(): void {
    if (this.selectedLoanId === null || this.generatingEmiPlan) {
      return;
    }

    this.generatingEmiPlan = true;
    this.clearMessages();
    this.infoMessage = 'Generating EMI plan for selected loan...';

    this.emiService.calculateEmi(this.selectedLoanId).subscribe({
      next: () => {
        this.generatingEmiPlan = false;
        this.successMessage = 'EMI plan generated successfully.';
        this.loadEmiData();
      },
      error: (err) => {
        this.generatingEmiPlan = false;
        if (this.isEmiAlreadyCalculatedError(err)) {
          this.successMessage = 'EMI plan already exists. Loaded the latest EMI details.';
          this.loadEmiData();
          return;
        }
        this.errorMessage = this.getErrorMessage(err, 'Failed to generate EMI plan.');
      }
    });
  }

  goToPaymentPage(): void {
    if (this.selectedLoanId === null || this.loadingEmiData || this.needsEmiPlan) {
      return;
    }

    const amountToPay = this.resolvePayableAmount();
    if (!Number.isFinite(amountToPay) || amountToPay <= 0) {
      this.errorMessage = 'No payable EMI amount available for this loan.';
      return;
    }

    this.clearMessages();
    this.router.navigate(['/emi-payment'], {
      queryParams: {
        loanId: this.selectedLoanId,
        amount: amountToPay,
        paymentDate: this.todayIsoDate()
      }
    });
  }

  private fetchLoans(): void {
    this.loadingLoans = true;
    this.clearMessages();

    this.loanService.getLoans().subscribe({
      next: (loans) => {
        this.loans = [...loans].sort((a, b) => b.applicationNo - a.applicationNo);
        this.loadingLoans = false;

        if (this.loans.length === 0) {
          this.infoMessage = 'No loans found for this account.';
          return;
        }

        const preferredLoan = this.loans.find((loan) => loan.status?.toUpperCase() === 'APPROVED') ?? this.loans[0];
        this.selectedLoanId = preferredLoan.applicationNo;
        this.selectedLoan = preferredLoan;
        this.loadEmiData();
      },
      error: (err) => {
        this.loadingLoans = false;
        this.errorMessage = this.getErrorMessage(err, 'Failed to load loans.');
      }
    });
  }

  private loadEmiData(): void {
    if (this.selectedLoanId === null) {
      return;
    }

    this.loadingEmiData = true;
    this.needsEmiPlan = false;
    this.emiDetails = undefined;
    this.outstanding = undefined;
    this.nextDue = undefined;
    this.paymentHistory = [];
    this.payableAmount = 0;
    this.clearMessages();

    this.emiService.getOutstanding(this.selectedLoanId).subscribe({
      next: (outstanding) => {
        this.outstanding = outstanding;
        this.payableAmount = this.resolvePayableAmount();
        this.loadAdditionalEmiData(this.selectedLoanId as number);
      },
      error: (err) => {
        this.loadingEmiData = false;
        if (this.isEmiPlanMissingError(err)) {
          this.needsEmiPlan = true;
          this.infoMessage = 'No EMI plan found for this loan. Generate it first to continue.';
          return;
        }
        this.errorMessage = this.getErrorMessage(err, 'Failed to load EMI details.');
      }
    });
  }

  private loadAdditionalEmiData(loanId: number): void {
    forkJoin({
      nextDue: this.emiService.getNextDue(loanId).pipe(catchError(() => of(undefined))),
      details: this.emiService.getDetails(loanId).pipe(catchError(() => of(undefined))),
      history: this.emiService.getPaymentHistory(loanId).pipe(
        catchError((err) => {
          if (this.isHistoryMissingError(err)) {
            return of([]);
          }
          throw err;
        })
      )
    }).subscribe({
      next: ({ nextDue, details, history }) => {
        this.nextDue = nextDue;
        this.emiDetails = details;
        this.paymentHistory = [...history].sort((a, b) => (a.paymentDate < b.paymentDate ? 1 : -1));
        this.payableAmount = this.resolvePayableAmount();
        this.loadingEmiData = false;
      },
      error: (err) => {
        this.loadingEmiData = false;
        this.errorMessage = this.getErrorMessage(err, 'Failed to load next-due or payment history.');
      }
    });
  }

  private isEmiPlanMissingError(err: unknown): boolean {
    const message = this.extractBackendMessage(err).toUpperCase();
    return message.includes('EMI_NOT_FOUND') || message.includes('NO EMI RECORD');
  }

  private isHistoryMissingError(err: unknown): boolean {
    const message = this.extractBackendMessage(err).toUpperCase();
    return message.includes('PAYMENT_HISTORY_NOT_FOUND') || message.includes('NO PAYMENTS FOUND');
  }

  private isEmiAlreadyCalculatedError(err: unknown): boolean {
    const message = this.extractBackendMessage(err).toUpperCase();
    return message.includes('EMI_ALREADY_CALCULATED');
  }

  private extractBackendMessage(err: unknown): string {
    const response = err as { error?: unknown; message?: string };
    if (typeof response.error === 'string') {
      return response.error;
    }
    if (response.error && typeof response.error === 'object') {
      const payload = response.error as { message?: string; errorCode?: string };
      return `${payload.errorCode ?? ''} ${payload.message ?? ''}`.trim();
    }
    return response.message ?? '';
  }

  private getErrorMessage(err: unknown, fallback: string): string {
    const response = err as { status?: number; error?: unknown; message?: string };
    if (response.status === 0) {
      return 'Unable to connect to backend services. Ensure gateway and emi-service are running.';
    }
    if (response.status === 401 || response.status === 403) {
      return 'Authorization failed. Please login again.';
    }

    const backendMessage = this.extractBackendMessage(err);
    if (backendMessage) {
      return backendMessage;
    }

    return fallback;
  }

  private clearMessages(): void {
    this.errorMessage = '';
    this.infoMessage = '';
    this.successMessage = '';
  }

  private resolvePayableAmount(): number {
    const monthlyFromOutstanding = Number(this.outstanding?.monthlyEmi ?? 0);
    if (Number.isFinite(monthlyFromOutstanding) && monthlyFromOutstanding > 0) {
      return monthlyFromOutstanding;
    }

    const monthlyFromNextDue = Number(this.nextDue?.emiAmount ?? 0);
    if (Number.isFinite(monthlyFromNextDue) && monthlyFromNextDue > 0) {
      return monthlyFromNextDue;
    }

    return 0;
  }

  private todayIsoDate(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = `${now.getMonth() + 1}`.padStart(2, '0');
    const day = `${now.getDate()}`.padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
