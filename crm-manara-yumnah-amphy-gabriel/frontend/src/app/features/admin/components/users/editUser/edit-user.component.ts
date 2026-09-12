import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { AdminService } from '../../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-edit-user',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAdminComponent],
  templateUrl: './edit-user.component.html'
})
export class EditUserComponent implements OnInit {
  private adminService = inject(AdminService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  user: any = null;
  id!: number;
  loading = false;
  success = '';
  error = '';

  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.adminService.getUserDetail(this.id).subscribe({
      next: (data) => {
        this.user = { ...data, password: '' };
        this.cdr.detectChanges();
      },
      error: (err) => console.error('Erreur chargement user:', err)
    });
  }

  onSubmit() {
    this.loading = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    this.adminService.updateUser(this.id, this.user).subscribe({
      next: () => {
        this.success = 'Utilisateur mis à jour avec succès !';
        this.loading = false;
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/admin/users', this.id]), 1200);
      },
      error: (err) => {
        this.error = err?.error ?? 'Erreur lors de la mise à jour.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}