import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { LOAN_TYPES_INFO, LoanType, LoanTypeInfo } from '../../models/loan.model';

@Component({
  selector: 'app-emi-calculator',
  standalone: false,
  templateUrl: './emi-calculator.component.html',
  styleUrl: './emi-calculator.component.css'
})
export class EmiCalculatorComponent implements OnInit {
  emiForm!: FormGroup;
  readonly loanTypes: LoanTypeInfo[] = LOAN_TYPES_INFO;

  selectedLoanType?: LoanTypeInfo;
  monthlyEmi = 0;
  totalInterest = 0;
  totalPayable = 0;

  constructor(
    private fb: FormBuilder,
    private router: Router
  ) { }

  ngOnInit(): void {
    this.emiForm = this.fb.group({
      principalAmount: [null, [Validators.required, Validators.min(10000)]],
      tenure: [null, [Validators.required, Validators.min(12), Validators.max(360)]],
      loanType: [this.loanTypes[0]?.type ?? '', Validators.required]
    });

    if (this.loanTypes.length > 0) {
      this.selectedLoanType = this.loanTypes[0];
    }

    this.emiForm.valueChanges.subscribe(() => this.calculateEmi());
    this.calculateEmi();
  }

  calculateEmi(): void {
    const principal = Number(this.emiForm.get('principalAmount')?.value);
    const tenure = Number(this.emiForm.get('tenure')?.value);
    const loanType = this.emiForm.get('loanType')?.value as LoanType;

    this.selectedLoanType = this.loanTypes.find(type => type.type === loanType);

    if (!this.selectedLoanType || !Number.isFinite(principal) || !Number.isFinite(tenure) || principal <= 0 || tenure <= 0) {
      this.monthlyEmi = 0;
      this.totalInterest = 0;
      this.totalPayable = 0;
      return;
    }

    const monthlyRate = this.selectedLoanType.roi / 1200;

    let emiValue = 0;
    if (monthlyRate === 0) {
      emiValue = principal / tenure;
    } else {
      const growthFactor = Math.pow(1 + monthlyRate, tenure);
      emiValue = (principal * monthlyRate * growthFactor) / (growthFactor - 1);
    }

    this.monthlyEmi = Number(emiValue.toFixed(2));
    this.totalPayable = Number((emiValue * tenure).toFixed(2));
    this.totalInterest = Number((this.totalPayable - principal).toFixed(2));
  }

  get canApplyForLoan(): boolean {
    return this.emiForm.valid && !!this.selectedLoanType && this.monthlyEmi > 0;
  }

  applyForLoanFromCalculator(): void {
    if (!this.selectedLoanType || !this.canApplyForLoan) {
      return;
    }

    const amount = Number(this.emiForm.get('principalAmount')?.value);
    const tenure = Number(this.emiForm.get('tenure')?.value);

    const sanitizedAmount = Number.isFinite(amount) && amount > 0 ? amount : undefined;
    const sanitizedTenure = Number.isFinite(tenure) && tenure >= 12 && tenure <= 84 ? tenure : undefined;

    this.router.navigate(['/loans/apply'], {
      queryParams: {
        type: this.selectedLoanType.type,
        amount: sanitizedAmount,
        tenure: sanitizedTenure
      }
    });
  }
}
