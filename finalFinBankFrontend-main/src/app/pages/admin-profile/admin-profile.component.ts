import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AdminService } from '../../services/admin.service';

interface AdminProfileView {
    username: string;
    role: string;
    authScope: string;
    serviceStatus: string;
}

@Component({
    selector: 'app-admin-profile',
    standalone: false,
    templateUrl: './admin-profile.component.html',
    styleUrl: './admin-profile.component.css'
})
export class AdminProfileComponent implements OnInit {
    sidebarOpen = false;
    adminUsername = 'Administrator';
    adminProfile: AdminProfileView = {
        username: 'Administrator',
        role: 'ROLE_ADMIN',
        authScope: 'Admin Controller Access',
        serviceStatus: 'Checking...'
    };

    pingStatus: 'checking' | 'healthy' | 'unhealthy' = 'checking';
    pingMessage = 'Checking admin service connectivity...';

    constructor(
        private readonly adminService: AdminService,
        private readonly router: Router
    ) { }

    ngOnInit(): void {
        this.loadAdminProfile();
        this.checkHealth();
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

    private loadAdminProfile(): void {
        const usernameFromStorage = localStorage.getItem('authUsername');
        if (usernameFromStorage && usernameFromStorage.trim().length > 0) {
            this.adminUsername = usernameFromStorage.trim();
        }
        this.adminProfile.username = this.adminUsername;
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
}
