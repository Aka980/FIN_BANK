import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { LoanService } from '../../../services/loan.service';
import { LoanRequest, LOAN_TYPES_INFO, LoanTypeInfo } from '../../../models/loan.model';

@Component({
    selector: 'app-loan-review',
    standalone: false,
    templateUrl: './loan-review.component.html',
    styleUrl: './loan-review.component.css'
})
export class LoanReviewComponent implements OnInit {
    application?: LoanRequest;
    loanTypeInfo?: LoanTypeInfo;
    submitting = false;
    error?: string;

    constructor(
        private router: Router,
        private loanService: LoanService
    ) {
        const navigation = this.router.getCurrentNavigation();
        this.application = navigation?.extras?.state?.['application'];
    }

    ngOnInit() {
        if (!this.application) {
            this.router.navigate(['/loans/apply']);
            return;
        }
        this.loanTypeInfo = LOAN_TYPES_INFO.find(l => l.type === this.application?.loanType);
    }

    confirmAndSubmit() {
        if (!this.application) return;

        this.submitting = true;
        this.error = undefined;

        this.loanService.applyLoan(this.application).subscribe({
            next: (response) => {
                this.submitting = false;
                this.router.navigate(['/loans/status-tracking'], { state: { loan: response } });
            },
            error: (err) => {
                this.submitting = false;
                // Specifically handling KYC error if the user implies this is the main reason
                this.error = "Please update KYC details and then come back for applying loan";
                console.error("Submission error:", err);
            }
        });
    }
}