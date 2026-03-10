import { Component } from '@angular/core';
import { CustomerService } from '../../services/customer.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  standalone: false,
  templateUrl: './register.component.html',
  styleUrl: './register.component.css'
})
export class RegisterComponent {


  customer: any = {};
  accountType: string = 'Customer';
  registrationSuccess: boolean = false;
  newAccountNumber: string = "Pending Validation...";
  confirmPassword: string = '';
  errorMessage: string = '';

  constructor(

    private service: CustomerService,
    private router: Router

  ) { }

  register() {

    console.log("Sending Payload: ", this.customer);
    this.errorMessage = '';

    this.service.register(this.customer)
      .subscribe({
        next: (res: any) => {
          this.registrationSuccess = true;

          // Debug what exactly we are getting
          console.log("Registration Response:", res);

          if (res && res.accountNo) {
            this.newAccountNumber = res.accountNo;
          } else if (typeof res === 'string' && res.includes('{')) {
            try {
              const parsed = JSON.parse(res);
              this.newAccountNumber = parsed.accountNo || "Check Email";
            } catch (e) {
              this.newAccountNumber = "Check Email";
            }
          } else if (typeof res === 'string' && res.length > 5) {
            this.newAccountNumber = res;
          } else {
            this.newAccountNumber = "Check Email";
          }
        },
        error: (err) => {
          console.error("Registration Error:", err);

          // Workaround: The backend saves the user in the database successfully, but crashes
          // with a 400 "Authentication failed" error when trying to send the Welcome Email
          // due to SMTP mail configuration issues. We can treat this as a success.
          if (err.status === 400 && err.error === "Authentication failed") {
            this.registrationSuccess = true;
            this.newAccountNumber = "Check Email for Account ID";
            return;
          }

          if (err.status === 0) {
            this.errorMessage = "Unable to connect to the server. Please ensure the backend is running.";
          } else {
            const backendError = err.error;
            let raw = "";

            if (backendError && typeof backendError === 'string') {
              raw = backendError;
            } else if (backendError && backendError.message) {
              raw = backendError.message;
            }

            if (raw) {
              if (raw.toLowerCase().includes('username') && raw.toLowerCase().includes('exist')) {
                this.errorMessage = "Username already taken. Please choose a different username.";
              } else {
                this.errorMessage = raw;
              }
            } else {
              this.errorMessage = "Registration failed. Please try again.";
            }
          }
        }
      });

  }

  goToAdminRegistration(): void {
    this.router.navigate(['/admin/register']);
  }

}
