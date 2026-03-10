import { Component, OnInit } from '@angular/core';
import { LoanService } from '../../../services/loan.service';
import { LoanResponse, LOAN_TYPES_INFO } from '../../../models/loan.model';

@Component({
    selector: 'app-loan-history',
    standalone: false,
    templateUrl: './loan-history.component.html',
    styleUrl: './loan-history.component.css'
})
export class LoanHistoryComponent implements OnInit {
    loans: LoanResponse[] = [];
    loading = true;
    error?: string;
    successMessage?: string;

    constructor(private loanService: LoanService) { }

    ngOnInit() {
        this.fetchLoans();
    }

    fetchLoans() {
        this.loading = true;
        this.loanService.getLoans().subscribe({
            next: (data) => {
                this.loans = data;
                this.loading = false;
            },
            error: (err) => {
                this.error = "Failed to load loan history.";
                this.loading = false;
                console.error(err);
            }
        });
    }

    getStatusClass(status: string) {
        switch (status.toLowerCase()) {
            case 'approved': return 'status-approved';
            case 'rejected': return 'status-rejected';
            case 'pending': return 'status-pending';
            case 'cancelled': return 'status-cancelled';
            default: return '';
        }
    }

    getLoanLabel(type: string): string {
        const info = LOAN_TYPES_INFO.find(l => l.type === type);
        return info ? info.label : type;
    }

    onCancelLoan(id: number) {
        this.loading = true;
        this.successMessage = undefined;
        this.error = undefined;

        this.loanService.cancelLoan(id).subscribe({
            next: () => {
                this.successMessage = "Loan Application Cancelled Successfully!";
                this.fetchLoans();
            },
            error: (err) => {
                this.error = "Failed to cancel loan application.";
                this.loading = false;
                console.error(err);
            }
        });
    }
}
