import { Component, OnInit } from '@angular/core';
import { ProfileService } from '../../services/profile.service';

@Component({
    selector: 'app-profile',
    standalone: false,
    templateUrl: './profile.component.html',
    styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

    customer: any = {};
    kyc: any = {};
    profileExists = false;
    editMode = false;
    toastMessage: string = '';
    toastType: 'success' | 'error' = 'success';

    constructor(private service: ProfileService) { }

    ngOnInit() {
        this.service.getCustomer()
            .subscribe((data: any) => {
                this.customer = data;
                console.log("Customer:", data);
            });

        this.service.getProfile()
            .subscribe({
                next: (data: any) => {
                    this.kyc = data;
                    this.profileExists = true;
                    console.log("KYC:", data);
                },
                error: (err: any) => {
                    this.profileExists = false;
                    console.log("No KYC Yet");
                }
            });
    }

    showToast(message: string, type: 'success' | 'error' = 'success') {
        this.toastMessage = message;
        this.toastType = type;
        setTimeout(() => { this.toastMessage = ''; }, 2000);
    }

    createProfile() {
        if (this.kyc.pan) this.kyc.pan = this.kyc.pan.toUpperCase();
        this.service.createProfile(this.kyc).subscribe({
            next: (res: any) => {
                this.showToast('Profile created successfully! ✓');
                setTimeout(() => location.reload(), 2000);
            },
            error: (err: any) => {
                console.error("Profile Creation Error:", err);
                const msg = err.error?.message || err.error || 'Failed to create profile.';
                this.showToast(msg, 'error');
            }
        });
    }

    updateProfile() {
        if (this.kyc.pan) this.kyc.pan = this.kyc.pan.toUpperCase();
        this.service.updateProfile(this.kyc).subscribe({
            next: (res: any) => {
                this.showToast('Profile updated successfully! ✓');
                this.editMode = false;
            },
            error: (err: any) => {
                console.error("Profile Update Error:", err);
                const msg = err.error?.message || err.error || 'Failed to update profile.';
                this.showToast(msg, 'error');
            }
        });
    }
}