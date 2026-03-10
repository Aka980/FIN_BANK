import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { LoanService } from '../../../services/loan.service';
import { LoanResponse, LOAN_TYPES_INFO, LoanTypeInfo } from '../../../models/loan.model';

@Component({
    selector: 'app-loan-details',
    standalone: false,
    templateUrl: './loan-details.component.html',
    styleUrl: './loan-details.component.css'
})
export class LoanDetailsComponent implements OnInit {
    loan?: LoanResponse;
    loanTypeInfo?: LoanTypeInfo;
    loading = true;
    error?: string;
    successMessage?: string;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private loanService: LoanService
    ) { }

    ngOnInit() {
        this.route.params.subscribe(params => {
            const id = params['id'];
            if (id) {
                this.fetchLoanDetails(+id);
            }
        });
    }

    fetchLoanDetails(id: number) {
        this.loading = true;
        this.loanService.getLoanById(id).subscribe({
            next: (data) => {
                this.loan = data;
                this.loanTypeInfo = LOAN_TYPES_INFO.find(l => l.type === data.loanType);
                this.loading = false;
            },
            error: (err) => {
                this.error = "Failed to load loan details.";
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

    onCancelLoan() {
        if (!this.loan) return;

        this.loading = true;
        this.successMessage = undefined;
        this.error = undefined;

        this.loanService.cancelLoan(this.loan.applicationNo).subscribe({
            next: () => {
                this.successMessage = "Loan Application Cancelled Successfully!";
                setTimeout(() => {
                    this.router.navigate(['/loans/history']);
                }, 1500);
            },
            error: (err) => {
                this.error = "Failed to cancel loan application.";
                this.loading = false;
                console.error(err);
            }
        });
    }
}
