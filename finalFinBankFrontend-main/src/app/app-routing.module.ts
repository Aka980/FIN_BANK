import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { DashboardComponent } from './pages/dashboard/dashboard.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { AuthGuard } from './guards/auth.guard';
import { MainLayoutComponent } from './layouts/main-layout/main-layout.component';
import { LoanProductsComponent } from './pages/loans/loan-products/loan-products.component';
import { LoanApplicationComponent } from './pages/loans/loan-application/loan-application.component';
import { LoanReviewComponent } from './pages/loans/loan-review/loan-review.component';
import { LoanStatusTrackingComponent } from './pages/loans/loan-status-tracking/loan-status-tracking.component';
import { LoanHistoryComponent } from './pages/loans/loan-history/loan-history.component';
import { LoanDetailsComponent } from './pages/loans/loan-details/loan-details.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';
import { AdminGuard } from './guards/admin.guard';
import { EmiCalculatorComponent } from './pages/emi-calculator/emi-calculator.component';
import { AiAssistantComponent } from './pages/ai-assistant/ai-assistant.component';
import { EmiCenterComponent } from './pages/emi-center/emi-center.component';
import { EmiPaymentComponent } from './pages/emi-payment/emi-payment.component';
import { EmiPaymentSuccessComponent } from './pages/emi-payment-success/emi-payment-success.component';
import { AdminRegisterComponent } from './pages/admin-register/admin-register.component';
import { AdminProfileComponent } from './pages/admin-profile/admin-profile.component';

const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'admin/register', component: AdminRegisterComponent },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard] },
  { path: 'admin/dashboard', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'admin/profile', component: AdminProfileComponent, canActivate: [AdminGuard] },

  {
    path: '',
    component: MainLayoutComponent,
    canActivate: [AuthGuard],
    children: [
      { path: 'profile', component: ProfileComponent },
      { path: 'emi-calculator', component: EmiCalculatorComponent },
      { path: 'emi-center', component: EmiCenterComponent },
      { path: 'emi-payment', component: EmiPaymentComponent },
      { path: 'emi-payment/success', component: EmiPaymentSuccessComponent },
      { path: 'ai-assistant', component: AiAssistantComponent },
      { path: 'loans/products', component: LoanProductsComponent },
      { path: 'loans/apply', component: LoanApplicationComponent },
      { path: 'loans/review', component: LoanReviewComponent },
      { path: 'loans/status-tracking', component: LoanStatusTrackingComponent },
      { path: 'loans/history', component: LoanHistoryComponent },
      { path: 'loans/details/:id', component: LoanDetailsComponent }
    ]
  }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
