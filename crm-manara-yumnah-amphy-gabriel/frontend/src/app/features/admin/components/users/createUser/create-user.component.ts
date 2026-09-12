import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AdminService } from '../../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-create-user',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAdminComponent],
  templateUrl: './create-user.component.html'
})
export class CreateUserComponent {
  private adminService = inject(AdminService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  user: any = {
    prenom: '', nom: '', email: '',
    password: '', telephone: '', role: 'ROLE_PARENT'
  };
  loading = false;
  errorMessage = '';
  successMessage = '';

  onSubmit() {
    this.loading = true;
    this.errorMessage = '';
    this.successMessage = '';
    this.cdr.detectChanges();

    this.adminService.createUser(this.user).subscribe({
      next: () => {
        this.successMessage = 'Utilisateur créé avec succès !';
        this.loading = false;
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/admin/users']), 1200);
      },
     error: (err) => {
  // err.error peut être un string OU un objet {error: "..."}
  if (typeof err.error === 'string') {
    this.errorMessage = err.error;
  } else if (err.error?.error) {
    this.errorMessage = err.error.error;
  } else if (err.error?.message) {
    this.errorMessage = err.error.message;
  } else {
    this.errorMessage = 'Erreur lors de la création.';
  }
  this.loading = false;
  this.cdr.detectChanges();
}
    });
  }
}