import { Component } from '@angular/core';
import { CustomerService } from '../../services/customer.service';
import { Router } from '@angular/router';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-login',
  standalone: false,
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {

  loginData: any = {};
  accountType: string = 'Customer';
  errorMessage: string = '';

  constructor(

    private service: CustomerService,
    private adminService: AdminService,
    private router: Router

  ) { }

  login() {
    if (this.accountType === 'Administrator') {
      this.loginAsAdministrator();
      return;
    }
    this.loginAsCustomer();
  }

  private loginAsCustomer(): void {
    this.errorMessage = '';
    this.service.login(this.loginData)
      .subscribe({
        next: (response: string) => {
          this.persistSession(response, 'CUSTOMER', this.loginData?.username);
          this.router.navigate(['/dashboard']);
        },
        error: (err) => {
          this.errorMessage = this.getErrorMessage(err);
        }
      });
  }

  private loginAsAdministrator(): void {
    this.errorMessage = '';
    this.adminService.login(this.loginData)
      .subscribe({
        next: (response: string) => {
          this.persistSession(response, 'ADMIN', this.loginData?.username);
          this.router.navigate(['/admin/dashboard']);
        },
        error: (err) => {
          this.errorMessage = this.getErrorMessage(err, 'Admin login failed. Please verify your credentials.');
        }
      });
  }

  private persistSession(token: string, role: 'CUSTOMER' | 'ADMIN', username?: string): void {
    localStorage.setItem('token', token);
    localStorage.setItem('authRole', role);
    if (typeof username === 'string' && username.trim().length > 0) {
      localStorage.setItem('authUsername', username.trim());
    }
  }

  private getErrorMessage(err: unknown, fallback: string = 'Login failed. Please check your credentials.'): string {
    if (typeof err !== 'object' || err === null) {
      return fallback;
    }

    const parsedError = err as { status?: number; error?: unknown; message?: string };

    if (parsedError.status === 0) {
      return 'Unable to connect to the server. Please ensure backend services are running.';
    }

    if (typeof parsedError.error === 'string' && parsedError.error.trim().length > 0) {
      if (parsedError.error.trim().startsWith('{')) {
        try {
          const jsonError = JSON.parse(parsedError.error);
          if (jsonError && typeof jsonError.message === 'string' && jsonError.message.trim().length > 0) {
            return jsonError.message;
          }
        } catch (e) {
          // Not valid JSON, continue with normal string handling
        }
      }
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

    if (parsedError.message && typeof parsedError.message === 'string' && parsedError.message.trim().length > 0) {
      return parsedError.message;
    }

    return fallback;
  }

}
