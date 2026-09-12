import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AdminService } from '../../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-create-activity',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAdminComponent],
  templateUrl: './create-activity.component.html'
})
export class CreateActivityComponent {
  private adminService = inject(AdminService);
  private router = inject(Router);

  activite: any = {
    titre: '',
    description: '',
    ageMin: null,
    ageMax: null
  };

  loading = false;

  onSubmit() {
    this.loading = true;
    this.adminService.createActivite(this.activite).subscribe({
      next: () => this.router.navigate(['/admin/activites']),
      error: (err) => {
        console.error(err);
        this.loading = false;
      }
    });
  }
}