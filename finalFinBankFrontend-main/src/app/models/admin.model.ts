export type AdminLoanStatus = 'PENDING' | 'APPROVED' | 'DENIED' | 'ABEYANCE' | string;

export interface AdminLoan {
  applicationNo: number;
  accountNo: number;
  applicationDate: string;
  amount: number;
  pan: string;
  tenure: number;
  roi: number;
  status: AdminLoanStatus;
  loanType: string;
}

export interface AdminCustomerProfile {
  accountNo: number;
  name: string;
  pan: string;
  income: number;
}
