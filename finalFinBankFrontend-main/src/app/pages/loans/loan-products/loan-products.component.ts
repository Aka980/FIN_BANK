import { Component } from '@angular/core';
import { LoanType, LOAN_TYPES_INFO, LoanTypeInfo } from '../../../models/loan.model';

@Component({
    selector: 'app-loan-products',
    standalone: false,
    templateUrl: './loan-products.component.html',
    styleUrl: './loan-products.component.css'
})
export class LoanProductsComponent {
    loanProducts: LoanTypeInfo[] = LOAN_TYPES_INFO;

    getProductImage(type: LoanType): string {
        switch (type) {
            case LoanType.CAR_LOAN: return '/assets/car-loan.png';
            case LoanType.BIKE_LOAN: return '/assets/bike-loan.png';
            case LoanType.COMMERCIAL_VEHICLE_LOAN: return '/assets/truck-loan.png';
            case LoanType.USED_VEHICLE_LOAN: return '/assets/car-hero.png';
            default: return '/assets/car-loan.png';
        }
    }
}
