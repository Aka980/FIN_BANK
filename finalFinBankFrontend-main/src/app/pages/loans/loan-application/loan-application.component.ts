import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { LoanType, LOAN_TYPES_INFO, LoanTypeInfo } from '../../../models/loan.model';

@Component({
    selector: 'app-loan-application',
    standalone: false,
    templateUrl: './loan-application.component.html',
    styleUrl: './loan-application.component.css'
})
export class LoanApplicationComponent implements OnInit {
    loanForm!: FormGroup;
    selectedLoanType?: LoanTypeInfo;

    constructor(
        private fb: FormBuilder,
        private route: ActivatedRoute,
        private router: Router
    ) { }

    ngOnInit() {
        this.initForm();

        this.route.queryParams.subscribe(params => {
            const typeStr = params['type'] as LoanType | undefined;
            const amountParam = Number(params['amount']);
            const tenureParam = Number(params['tenure']);
            const patch: Partial<{ loanType: LoanType; amount: number; tenure: number }> = {};

            if (typeStr) {
                this.selectedLoanType = LOAN_TYPES_INFO.find(l => l.type === typeStr);
                if (this.selectedLoanType) {
                    patch.loanType = this.selectedLoanType.type;
                }
            }

            if (Number.isFinite(amountParam) && amountParam >= 10000) {
                patch.amount = amountParam;
            }

            if (Number.isFinite(tenureParam) && tenureParam >= 12 && tenureParam <= 84) {
                patch.tenure = tenureParam;
            }

            if (Object.keys(patch).length > 0) {
                this.loanForm.patchValue(patch);
            }
        });
    }

    initForm() {
        this.loanForm = this.fb.group({
            loanType: ['', Validators.required],
            amount: [null, [Validators.required, Validators.min(10000)]],
            tenure: [null, [Validators.required, Validators.min(12), Validators.max(84)]]
        });

        this.loanForm.get('loanType')?.valueChanges.subscribe(value => {
            this.selectedLoanType = LOAN_TYPES_INFO.find(l => l.type === value);
        });
    }

    onSubmit() {
        if (this.loanForm.valid) {
            // Store application in a temporary location (e.g., service or state) and go to review
            // For now, I'll pass via state or query params, but better to use a shared state in LoanService
            console.log("Form values:", this.loanForm.value);
            this.router.navigate(['/loans/review'], { state: { application: this.loanForm.value } });
        } else {
            this.markFormGroupTouched(this.loanForm);
        }
    }

    private markFormGroupTouched(formGroup: FormGroup) {
        Object.values(formGroup.controls).forEach(control => {
            control.markAsTouched();
            if ((control as any).controls) {
                this.markFormGroupTouched(control as any);
            }
        });
    }

    get loanTypes() {
        return LOAN_TYPES_INFO;
    }
}
