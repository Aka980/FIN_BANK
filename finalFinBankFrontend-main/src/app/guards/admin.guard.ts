import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {
  constructor(private readonly router: Router) { }

  canActivate(): boolean {
    const token = localStorage.getItem('token');
    const authRole = localStorage.getItem('authRole');

    if (token && authRole === 'ADMIN') {
      return true;
    }

    this.router.navigate(['/']);
    return false;
  }
}
