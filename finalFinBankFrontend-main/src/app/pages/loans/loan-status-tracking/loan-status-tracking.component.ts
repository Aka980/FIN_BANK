import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoanResponse, LOAN_TYPES_INFO, LoanTypeInfo } from '../../../models/loan.model';

@Component({
    selector: 'app-loan-status-tracking',
    standalone: false,
    templateUrl: './loan-status-tracking.component.html',
    styleUrl: './loan-status-tracking.component.css'
})
export class LoanStatusTrackingComponent implements OnInit {
    loan?: LoanResponse;
    loanTypeInfo?: LoanTypeInfo;

    stages = [
        { id: 'SUBMITTED', label: 'Application Submitted', icon: '📝' },
        { id: 'PENDING', label: 'Verification in Progress', icon: '🔍' },
        { id: 'APPROVED', label: 'Loan Approved', icon: '✅' },
        { id: 'DISBURSED', label: 'Funds Disbursed', icon: '💰' }
    ];

    constructor(private router: Router) {
        const navigation = this.router.getCurrentNavigation();
        this.loan = navigation?.extras?.state?.['loan'];
    }

    ngOnInit() {
        if (!this.loan) {
            this.router.navigate(['/loans/history']);
            return;
        }
        this.loanTypeInfo = LOAN_TYPES_INFO.find(l => l.type === this.loan?.loanType);
    }

    getCurrentStageIndex(): number {
        if (!this.loan) return 0;
        const status = this.loan.status.toUpperCase();
        if (status === 'REJECTED' || status === 'CANCELLED') return -1;
        if (status === 'APPROVED') return 2;
        if (status === 'PENDING') return 1;
        return 0;
    }

    isStageCompleted(index: number): boolean {
        return index < this.getCurrentStageIndex();
    }

    isStageActive(index: number): boolean {
        return index === this.getCurrentStageIndex();
    }
}
