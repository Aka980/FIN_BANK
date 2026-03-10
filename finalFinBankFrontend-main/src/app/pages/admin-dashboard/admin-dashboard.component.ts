import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { Observable } from 'rxjs';
import { AdminCustomerProfile, AdminLoan } from '../../models/admin.model';
import { AdminService } from '../../services/admin.service';

type HealthStatus = 'checking' | 'healthy' | 'unhealthy';

interface AdminDashboardStats {
  total: number;
  pending: number;
  approved: number;
  denied: number;
  abeyance: number;
  totalAmount: number;
}

interface AdminProfileView {
  username: string;
  role: string;
  authScope: string;
  serviceStatus: string;
}

@Component({
  selector: 'app-admin-dashboard',
  standalone: false,
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.css'
})
export class AdminDashboardComponent implements OnInit {
  readonly statusFilters: string[] = ['ALL', 'PENDING', 'APPROVED', 'DENIED', 'ABEYANCE'];
  readonly pageSize = 5;

  sidebarOpen = false;
  adminUsername = 'Administrator';
  adminProfile: AdminProfileView = {
    username: 'Administrator',
    role: 'ROLE_ADMIN',
    authScope: 'Admin Controller Access',
    serviceStatus: 'Checking...'
  };

  loans: AdminLoan[] = [];
  filteredLoans: AdminLoan[] = [];

  loadingLoans = false;
  selectedStatus = 'ALL';
  accountNoFilter = '';
  currentPage = 1;
  totalPages = 1;
  pagedLoans: AdminLoan[] = [];

  actionError = '';
  actionSuccess = '';
  denyReasonByLoan: Record<number, string> = {};
  processingLoanIds = new Set<number>();

  customerLookupAccountNo = '';
  customerLookupLoading = false;
  customerLookupError = '';
  customerProfile: AdminCustomerProfile | null = null;

  pingStatus: HealthStatus = 'checking';
  pingMessage = 'Checking admin service connectivity...';

  stats: AdminDashboardStats = {
    total: 0,
    pending: 0,
    approved: 0,
    denied: 0,
    abeyance: 0,
    totalAmount: 0
  };

  constructor(
    private readonly adminService: AdminService,
    private readonly router: Router
  ) { }

  ngOnInit(): void {
    this.loadAdminProfile();
    this.checkHealth();
    this.loadLoans();
  }

  toggleSidebar(): void {
    this.sidebarOpen = !this.sidebarOpen;
  }

  logout(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('authRole');
    localStorage.removeItem('authUsername');
    this.router.navigate(['/']);
  }

  refreshData(): void {
    this.clearMessages();
    this.checkHealth();
    this.loadLoans();
  }

  onStatusChange(status: string): void {
    this.selectedStatus = status;
    this.applyFilters();
  }

  setStatusFilter(status: string): void {
    this.selectedStatus = status;
    this.applyFilters();
  }

  onAccountNoFilterChange(accountNo: string | number | null): void {
    this.accountNoFilter = this.normalizeAccountNo(accountNo);
    this.applyFilters();
  }

  goToPreviousPage(): void {
    if (!this.canGoPrevious) {
      return;
    }
    this.currentPage -= 1;
    this.updatePagination();
  }

  goToNextPage(): void {
    if (!this.canGoNext) {
      return;
    }
    this.currentPage += 1;
    this.updatePagination();
  }

  get canGoPrevious(): boolean {
    return !this.isAccountFilterActive && this.currentPage > 1;
  }

  get canGoNext(): boolean {
    return !this.isAccountFilterActive && this.currentPage < this.totalPages;
  }

  get pageStartItem(): number {
    if (this.filteredLoans.length === 0) {
      return 0;
    }
    return (this.currentPage - 1) * this.pageSize + 1;
  }

  get pageEndItem(): number {
    return Math.min(this.currentPage * this.pageSize, this.filteredLoans.length);
  }

  loadLoans(): void {
    this.loadingLoans = true;
    this.adminService.getAllLoans().subscribe({
      next: (loans) => {
        this.loans = loans ?? [];
        this.updateStats();
        this.applyFilters();
      },
      error: (error) => {
        this.actionError = this.getErrorMessage(error, 'Unable to load loan applications.');
      }
    }).add(() => {
      this.loadingLoans = false;
    });
  }

  lookupCustomerProfile(): void {
    this.customerLookupError = '';
    this.customerProfile = null;

    const accountNo = Number(this.customerLookupAccountNo);
    if (!Number.isInteger(accountNo) || accountNo <= 0) {
      this.customerLookupError = 'Enter a valid account number to fetch profile details.';
      return;
    }

    this.customerLookupLoading = true;
    this.adminService.getCustomerProfile(accountNo).subscribe({
      next: (profile) => {
        this.customerProfile = profile;
      },
      error: (error) => {
        this.customerLookupError = this.getErrorMessage(error, 'Customer profile not found.');
      }
    }).add(() => {
      this.customerLookupLoading = false;
    });
  }

  viewCustomerProfile(accountNo: number): void {
    this.customerLookupAccountNo = accountNo.toString();
    this.lookupCustomerProfile();
  }

  approveLoan(loan: AdminLoan): void {
    if (this.isLoanDenied(loan)) {
      this.actionError = `Application ${loan.applicationNo} is denied and cannot be moved to another status.`;
      this.actionSuccess = '';
      return;
    }

    this.executeLoanAction(
      loan.applicationNo,
      () => this.adminService.approveLoan(loan.applicationNo),
      `Application ${loan.applicationNo} approved successfully.`
    );
  }

  denyLoan(loan: AdminLoan): void {
    const reason = (this.denyReasonByLoan[loan.applicationNo] ?? '').trim();
    if (!reason) {
      this.actionError = `Please provide a deny reason for application ${loan.applicationNo}.`;
      this.actionSuccess = '';
      return;
    }

    this.executeLoanAction(
      loan.applicationNo,
      () => this.adminService.denyLoan(loan.applicationNo, reason),
      `Application ${loan.applicationNo} denied.`
    );
  }

  abeyanceLoan(loan: AdminLoan): void {
    if (this.isLoanDenied(loan)) {
      this.actionError = `Application ${loan.applicationNo} is denied and cannot be moved to another status.`;
      this.actionSuccess = '';
      return;
    }

    this.executeLoanAction(
      loan.applicationNo,
      () => this.adminService.abeyanceLoan(loan.applicationNo),
      `Application ${loan.applicationNo} moved to abeyance.`
    );
  }

  deleteLoanEmi(loan: AdminLoan): void {
    if (!confirm(`Are you sure you want to delete EMI records for application ${loan.applicationNo}?`)) {
      return;
    }

    this.executeLoanAction(
      loan.applicationNo,
      () => this.adminService.deleteLoanEmi(loan.applicationNo),
      `EMI records for application ${loan.applicationNo} deleted successfully.`
    );
  }

  isProcessing(applicationNo: number): boolean {
    return this.processingLoanIds.has(applicationNo);
  }

  isLoanDenied(loan: AdminLoan): boolean {
    return this.isDeniedStatus(loan.status);
  }

  formatCurrency(value: number): string {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 2
    }).format(value || 0);
  }

  formatCurrencyInr(value: number): string {
    return new Intl.NumberFormat('en-IN', {
      style: 'currency',
      currency: 'INR',
      maximumFractionDigits: 2
    }).format(value || 0);
  }

  trackByLoan(_: number, loan: AdminLoan): number {
    return loan.applicationNo;
  }

  private executeLoanAction(
    applicationNo: number,
    action: () => Observable<string>,
    successMessage: string
  ): void {
    this.clearMessages();
    this.processingLoanIds.add(applicationNo);

    action().subscribe({
      next: () => {
        this.actionSuccess = successMessage;
        this.denyReasonByLoan[applicationNo] = '';
        this.loadLoans();
      },
      error: (error) => {
        this.actionError = this.getErrorMessage(error, `Action failed for application ${applicationNo}.`);
      }
    }).add(() => {
      this.processingLoanIds.delete(applicationNo);
    });
  }

  private clearMessages(): void {
    this.actionError = '';
    this.actionSuccess = '';
  }

  private applyFilters(): void {
    const accountFilter = this.normalizeAccountNo(this.accountNoFilter);

    this.filteredLoans = this.loans.filter((loan) => {
      const loanStatus = (loan.status ?? '').toUpperCase();
      const statusMatches = this.selectedStatus === 'ALL' || loanStatus === this.selectedStatus;
      if (!statusMatches) {
        return false;
      }

      const loanAccountNo = this.normalizeAccountNo(loan.accountNo);
      if (accountFilter && loanAccountNo !== accountFilter) {
        return false;
      }

      return true;
    });

    this.currentPage = 1;
    this.updatePagination();
  }

  private updatePagination(): void {
    if (this.isAccountFilterActive) {
      this.totalPages = 1;
      this.currentPage = 1;
      this.pagedLoans = [...this.filteredLoans];
      return;
    }

    this.totalPages = Math.max(1, Math.ceil(this.filteredLoans.length / this.pageSize));

    if (this.currentPage > this.totalPages) {
      this.currentPage = this.totalPages;
    }

    const startIndex = (this.currentPage - 1) * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.pagedLoans = this.filteredLoans.slice(startIndex, endIndex);
  }

  private get isAccountFilterActive(): boolean {
    return this.normalizeAccountNo(this.accountNoFilter).length > 0;
  }

  private normalizeAccountNo(value: unknown): string {
    const raw = String(value ?? '').trim();
    if (!raw) {
      return '';
    }

    const asNumber = Number(raw);
    if (Number.isFinite(asNumber) && asNumber > 0) {
      return Math.trunc(asNumber).toString();
    }

    return raw.replace(/\D/g, '');
  }

  private isDeniedStatus(status: string | null | undefined): boolean {
    return (status ?? '').toUpperCase() === 'DENIED';
  }

  private updateStats(): void {
    const updatedStats: AdminDashboardStats = {
      total: this.loans.length,
      pending: 0,
      approved: 0,
      denied: 0,
      abeyance: 0,
      totalAmount: 0
    };

    this.loans.forEach((loan) => {
      const status = (loan.status ?? '').toUpperCase();
      updatedStats.totalAmount += loan.amount ?? 0;

      if (status === 'PENDING') {
        updatedStats.pending += 1;
      } else if (status === 'APPROVED') {
        updatedStats.approved += 1;
      } else if (status === 'DENIED') {
        updatedStats.denied += 1;
      } else if (status === 'ABEYANCE') {
        updatedStats.abeyance += 1;
      }
    });

    this.stats = updatedStats;
  }

  private checkHealth(): void {
    this.pingStatus = 'checking';
    this.pingMessage = 'Checking admin service connectivity...';
    this.adminProfile.serviceStatus = 'Checking...';

    this.adminService.ping().subscribe({
      next: (response) => {
        this.pingStatus = 'healthy';
        this.pingMessage = response || 'Admin service is healthy.';
        this.adminProfile.serviceStatus = 'Connected';
      },
      error: () => {
        this.pingStatus = 'unhealthy';
        this.pingMessage = 'Unable to reach admin service.';
        this.adminProfile.serviceStatus = 'Unavailable';
      }
    });
  }

  private loadAdminProfile(): void {
    const usernameFromStorage = localStorage.getItem('authUsername');
    if (usernameFromStorage && usernameFromStorage.trim().length > 0) {
      this.adminUsername = usernameFromStorage.trim();
    }
    this.adminProfile.username = this.adminUsername;
  }

  private getErrorMessage(error: unknown, fallback: string): string {
    if (typeof error !== 'object' || error === null) {
      return fallback;
    }

    const parsedError = error as {
      status?: number;
      message?: string;
      error?: unknown;
    };

    if (parsedError.status === 0) {
      return 'Unable to connect to the backend. Verify admin service and gateway are running.';
    }

    if (typeof parsedError.error === 'string' && parsedError.error.trim().length > 0) {
      return parsedError.error;
    }

    if (typeof parsedError.error === 'object' && parsedError.error !== null) {
      const nestedError = parsedError.error as { message?: string; error?: string };
      if (typeof nestedError.message === 'string' && nestedError.message.trim().length > 0) {
        return nestedError.message;
      }
      if (typeof nestedError.error === 'string' && nestedError.error.trim().length > 0) {
        return nestedError.error;
      }
    }

    if (typeof parsedError.message === 'string' && parsedError.message.trim().length > 0) {
      return parsedError.message;
    }

    return fallback;
  }

  goToAdminProfile(): void {
    this.router.navigate(['/admin/profile']);
  }
}
