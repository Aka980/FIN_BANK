import { Component, OnInit, OnDestroy } from '@angular/core';
import { ProfileService } from '../../services/profile.service';
import { CustomerService } from '../../services/customer.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-dashboard',
  standalone: false,
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, OnDestroy {
  profile: any;
  customer: any;

  heroImages: string[] = [
    '/assets/hero-section.png'
  ];
  currentImageIndex: number = 0;
  imageInterval: any;

  constructor(
    private service: ProfileService,
    private customerService: CustomerService,
    private router: Router
  ) { }

  ngOnInit() {
    this.startImageCarousel();

    // Fetch customer details
    this.customerService.getCustomer()
      .subscribe({
        next: (data) => {
          this.customer = data;
        },
        error: (err) => console.error("Failed to load customer details", err)
      });

    // Fetch KYC profile
    this.service.getProfile()
      .subscribe({
        next: (data) => {
          this.profile = data;
        },
        error: (err) => console.error("No KYC profile found or error", err)
      });

  }

  logout() {
    localStorage.removeItem("token");
    localStorage.removeItem("authRole");
    localStorage.removeItem("authUsername");
    this.router.navigate(['/']);
  }

  sidebarOpen = false;

  toggleSidebar() {
    this.sidebarOpen = !this.sidebarOpen;
  }

  openAiAssistant() {
    this.router.navigate(['/ai-assistant']);
  }

  startImageCarousel() {
    this.imageInterval = setInterval(() => {
      if (this.heroImages.length > 1) {
        this.currentImageIndex = (this.currentImageIndex + 1) % this.heroImages.length;
      }
    }, 4000);
  }

  ngOnDestroy() {
    if (this.imageInterval) {
      clearInterval(this.imageInterval);
    }
  }

}
