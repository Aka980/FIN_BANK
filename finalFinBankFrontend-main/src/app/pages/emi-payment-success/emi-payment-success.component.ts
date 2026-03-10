import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-emi-payment-success',
  standalone: false,
  templateUrl: './emi-payment-success.component.html',
  styleUrl: './emi-payment-success.component.css'
})
export class EmiPaymentSuccessComponent implements OnInit {
  amountPaid = 0;
  transactionId = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.route.queryParamMap.subscribe((params) => {
      const amount = Number(params.get('amount'));
      const txnId = String(params.get('txnId') ?? '').trim();

      this.amountPaid = Number.isFinite(amount) && amount > 0 ? amount : 0;
      this.transactionId = txnId || this.generateFallbackTxnId();
    });
  }

  returnToDashboard(): void {
    this.router.navigate(['/dashboard']);
  }

  private generateFallbackTxnId(): string {
    const stamp = Date.now().toString().slice(-6);
    const random = Math.floor(1000 + Math.random() * 9000);
    return `TXN${stamp}${random}`;
  }
}
