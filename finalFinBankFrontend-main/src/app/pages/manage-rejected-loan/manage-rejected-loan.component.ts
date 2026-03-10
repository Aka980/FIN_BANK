import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AdminService } from '../../services/admin.service';
import { AdminLoan } from '../../models/admin.model';

@Component({
    selector: 'app-manage-rejected-loan',
    standalone: false,
    templateUrl: './manage-rejected-loan.component.html',
    styleUrl: './manage-rejected-loan.component.css'
})
export class ManageRejectedLoanComponent implements OnInit {
    applicationId: number | null = null;
    loan: AdminLoan | null = null;
    loading = false;
    error = '';
    success = '';
    deleting = false;

    constructor(
        private readonly route: ActivatedRoute,
        private readonly router: Router,
        private readonly adminService: AdminService
    ) { }

    ngOnInit(): void {
        const idParam = this.route.snapshot.paramMap.get('id');
        if (idParam) {
            this.applicationId = Number(idParam);
            this.loadLoanDetails();
        } else {
            this.error = 'Invalid Application ID';
        }
    }

    loadLoanDetails(): void {
        this.loading = true;
        this.adminService.getAllLoans().subscribe({
            next: (loans) => {
                this.loan = loans.find(l => l.applicationNo === this.applicationId) || null;
                if (!this.loan) {
                    this.error = 'Loan application not found.';
                } else if (this.loan.status !== 'DENIED') {
                    this.error = 'This application is not in DENIED status and cannot be managed here.';
                }
            },
            error: (err) => {
                this.error = 'Failed to load loan details.';
                console.error(err);
            }
        }).add(() => this.loading = false);
    }

    deletePermanently(): void {
        if (!this.applicationId || !confirm('Are you absolutely sure you want to delete this loan application and all associated records permanently? This action cannot be undone.')) {
            return;
        }

        this.deleting = true;
        this.adminService.deleteRejectedLoan(this.applicationId).subscribe({
            next: (msg) => {
                this.success = msg;
                setTimeout(() => {
                    this.router.navigate(['/admin/dashboard']);
                }, 2000);
            },
            error: (err) => {
                this.error = 'Failed to delete loan application.';
                console.error(err);
            }
        }).add(() => this.deleting = false);
    }

    goBack(): void {
        this.router.navigate(['/admin/dashboard']);
    }
}
