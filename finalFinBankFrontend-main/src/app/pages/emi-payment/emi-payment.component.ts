import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { EmiService } from '../../services/emi.service';

type CheckoutMethod = 'UPI' | 'NET_BANKING' | 'DEBIT_CREDIT';

interface CheckoutMethodOption {
  value: CheckoutMethod;
  label: string;
  subtitle: string;
}

@Component({
  selector: 'app-emi-payment',
  standalone: false,
  templateUrl: './emi-payment.component.html',
  styleUrl: './emi-payment.component.css'
})
export class EmiPaymentComponent implements OnInit {
  loanId: number | null = null;
  amount = 0;
  paymentDate = '';

  selectedMethod: CheckoutMethod = 'UPI';
  processing = false;
  errorMessage = '';

  readonly paymentMethods: CheckoutMethodOption[] = [
    { value: 'UPI', label: 'UPI', subtitle: 'Pay instantly via UPI app' },
    { value: 'NET_BANKING', label: 'Net Banking', subtitle: 'Pay directly using bank portal' },
    { value: 'DEBIT_CREDIT', label: 'Debit/Credit', subtitle: 'Card payment with OTP' }
  ];

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly emiService: EmiService
  ) { }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const loanId = Number(params.get('loanId'));
      const amount = Number(params.get('amount'));
      const paymentDate = params.get('paymentDate');

      if (!Number.isFinite(loanId) || loanId <= 0 || !Number.isFinite(amount) || amount <= 0) {
        this.router.navigate(['/emi-center']);
        return;
      }

      this.loanId = loanId;
      this.amount = Number(amount.toFixed(2));
      this.paymentDate = paymentDate && paymentDate.trim().length > 0 ? paymentDate : this.todayIsoDate();
    });
  }

  selectMethod(method: CheckoutMethod): void {
    this.selectedMethod = method;
  }

  proceedToPay(): void {
    if (this.loanId === null || this.processing) {
      return;
    }

    this.processing = true;
    this.errorMessage = '';

    this.emiService.recordPayment({
      loanId: this.loanId,
      amountPaid: this.amount,
      paymentDate: this.paymentDate
    }).subscribe({
      next: (payment) => {
        this.processing = false;
        const transactionId = this.buildTransactionId(payment?.paymentId);
        this.router.navigate(['/emi-payment/success'], {
          queryParams: {
            amount: Number(payment?.amountPaid ?? this.amount),
            txnId: transactionId
          }
        });
      },
      error: (err: unknown) => {
        this.processing = false;
        this.errorMessage = this.getErrorMessage(err, 'Unable to complete EMI payment right now.');
      }
    });
  }

  backToEmiCenter(): void {
    this.router.navigate(['/emi-center']);
  }

  private buildTransactionId(paymentId: number | undefined): string {
    if (Number.isFinite(paymentId) && Number(paymentId) > 0) {
      return `TXN${String(paymentId).padStart(6, '0')}`;
    }
    const stamp = Date.now().toString().slice(-6);
    const random = Math.floor(1000 + Math.random() * 9000);
    return `TXN${stamp}${random}`;
  }

  private getErrorMessage(err: unknown, fallback: string): string {
    const response = err as { status?: number; error?: unknown; message?: string };
    if (response?.status === 0) {
      return 'Unable to connect to EMI service. Please try again.';
    }
    if (response?.status === 401 || response?.status === 403) {
      return 'Authorization failed. Please login again.';
    }

    if (typeof response?.error === 'string' && response.error.trim().length > 0) {
      return response.error;
    }
    if (response?.error && typeof response.error === 'object') {
      const payload = response.error as { message?: string };
      if (payload.message && payload.message.trim().length > 0) {
        return payload.message;
      }
    }
    if (typeof response?.message === 'string' && response.message.trim().length > 0) {
      return response.message;
    }
    return fallback;
  }

  private todayIsoDate(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = `${now.getMonth() + 1}`.padStart(2, '0');
    const day = `${now.getDate()}`.padStart(2, '0');
    return `${year}-${month}-${day}`;
  }
}
