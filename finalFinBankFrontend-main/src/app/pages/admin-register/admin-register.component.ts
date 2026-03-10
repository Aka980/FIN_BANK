import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService } from '../../services/admin.service';

@Component({
  selector: 'app-admin-register',
  standalone: false,
  templateUrl: './admin-register.component.html',
  styleUrl: './admin-register.component.css'
})
export class AdminRegisterComponent {
  adminData: { fullName: string; username: string; password: string } = {
    fullName: '',
    username: '',
    password: ''
  };
  confirmPassword = '';
  submitting = false;
  errorMessage = '';
  successMessage = '';

  constructor(
    private readonly adminService: AdminService,
    private readonly router: Router
  ) { }

  registerAdmin(): void {
    this.errorMessage = '';
    this.successMessage = '';

    const fullName = (this.adminData.fullName ?? '').trim();
    const username = (this.adminData.username ?? '').trim();
    const password = this.adminData.password ?? '';
    const confirmPassword = this.confirmPassword ?? '';

    if (!fullName || !username || !password || !confirmPassword) {
      this.errorMessage = 'All fields are required.';
      return;
    }

    if (password !== confirmPassword) {
      this.errorMessage = 'Passwords do not match.';
      return;
    }

    this.submitting = true;

    this.adminService.register({ fullName, username, password }).subscribe({
      next: (response) => {
        this.submitting = false;
        this.successMessage = response || 'Admin registered successfully.';
      },
      error: (err) => {
        this.submitting = false;
        this.errorMessage = this.getErrorMessage(err);
      }
    });
  }

  goToLogin(): void {
    this.router.navigate(['/']);
  }

  private getErrorMessage(err: unknown): string {
    if (typeof err !== 'object' || err === null) {
      return 'Admin registration failed. Please try again.';
    }

    const parsedError = err as { status?: number; error?: unknown; message?: string };

    if (parsedError.status === 0) {
      return 'Unable to connect to the server. Ensure backend services are running.';
    }

    if (typeof parsedError.error === 'string' && parsedError.error.trim().length > 0) {
      if (parsedError.error.trim().startsWith('{')) {
        try {
          const jsonError = JSON.parse(parsedError.error);
          if (jsonError && typeof jsonError.message === 'string' && jsonError.message.trim().length > 0) {
            return jsonError.message;
          }
        } catch (e) {
          // Not valid JSON, continue
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

    return 'Admin registration failed. Please try again.';
  }
}
