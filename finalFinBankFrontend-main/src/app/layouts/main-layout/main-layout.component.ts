import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CustomerService } from '../../services/customer.service';

@Component({
    selector: 'app-main-layout',
    standalone: false,
    templateUrl: './main-layout.component.html',
    styleUrl: './main-layout.component.css'
})
export class MainLayoutComponent implements OnInit {
    customer: any;
    sidebarOpen = false;

    constructor(
        private customerService: CustomerService,
        private router: Router
    ) { }

    ngOnInit() {
        this.customerService.getCustomer().subscribe({
            next: (data) => {
                this.customer = data;
            },
            error: (err) => console.error("Failed to load customer details", err)
        });
    }

    toggleSidebar() {
        this.sidebarOpen = !this.sidebarOpen;
    }

    logout() {
        localStorage.removeItem("token");
        localStorage.removeItem("authRole");
        localStorage.removeItem("authUsername");
        this.router.navigate(['/']);
    }
}
