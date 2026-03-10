export enum LoanType {
  CAR_LOAN = 'CAR_LOAN',
  BIKE_LOAN = 'BIKE_LOAN',
  COMMERCIAL_VEHICLE_LOAN = 'COMMERCIAL_VEHICLE_LOAN',
  USED_VEHICLE_LOAN = 'USED_VEHICLE_LOAN'
}

export interface LoanTypeInfo {
  type: LoanType;
  label: string;
  roi: number;
  description: string;
  icon: string;
}

export const LOAN_TYPES_INFO: LoanTypeInfo[] = [
  {
    type: LoanType.CAR_LOAN,
    label: 'Car Loan',
    roi: 9.0,
    description: 'New car financing with quick processing.',
    icon: '🚗'
  },
  {
    type: LoanType.BIKE_LOAN,
    label: 'Bike Loan',
    roi: 11.5,
    description: 'Flexible options for your dream two-wheeler.',
    icon: '🏍️'
  },
  {
    type: LoanType.COMMERCIAL_VEHICLE_LOAN,
    label: 'Commercial Vehicle Loan',
    roi: 10.5,
    description: 'Empower your business with commercial financing.',
    icon: '🚛'
  },
  {
    type: LoanType.USED_VEHICLE_LOAN,
    label: 'Used Vehicle Loan',
    roi: 13.0,
    description: 'Affordable financing for pre-owned vehicles.',
    icon: '🚘'
  }
];

export interface LoanRequest {
  accountNo?: number;
  amount: number;
  tenure: number;
  loanType: LoanType;
}

export interface LoanResponse {
  applicationNo: number;
  accountNo: number;
  applicationDate: string;
  amount: number;
  pan: string;
  tenure: number;
  roi: number;
  status: string;
  loanType: LoanType;
}
