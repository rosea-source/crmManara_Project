// src/app/shared/components/navbar/navbar.component.ts
import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './navbar.component.html'
})
export class NavbarComponent {
  auth = inject(AuthService);

  // Retourne la route du dashboard selon le rôle
  get dashboardRoute(): string {
    const role = this.auth.getRole(); // ex: 'ROLE_PARENT', 'ROLE_ANIMATEUR', 'ROLE_ADMIN'
    if (role === 'ROLE_ADMIN')     return '/admin/dashboard';
    if (role === 'ROLE_ANIMATEUR') return '/animateur/dashboard';
    return '/parent/dashboard'; // défaut ROLE_PARENT
  }
}