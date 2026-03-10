export interface EmiRecord {
  emiId?: number;
  loanId: number;
  accountNo: number;
  principalAmount: number;
  roi: number;
  tenure: number;
  monthlyEmiAmount: number;
  createdDate: string;
}

export interface EmiFullHistoryResponse {
  accnumber: number;
  emiPlans: EmiRecord[];
}

export interface EmiOutstandingResponse {
  loanId: number;
  totalLoanAmount: number;
  totalPaid: number;
  remainingAmount: number;
  monthlyEmi: number;
}

export interface EmiNextDueResponse {
  loanId: number;
  nextDueDate: string | null;
  emiAmount: number;
  remainingEmis: number;
  loanClosed: boolean;
}

export interface EmiPaymentRequest {
  loanId: number;
  amountPaid: number;
  paymentDate: string;
}

export interface EmiPaymentHistory {
  paymentId: number;
  loanId: number;
  amountPaid: number;
  paymentDate: string;
}
