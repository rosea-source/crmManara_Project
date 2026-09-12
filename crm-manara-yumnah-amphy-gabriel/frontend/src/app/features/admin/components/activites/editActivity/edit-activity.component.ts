import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router, ActivatedRoute } from '@angular/router';
import { AdminService } from '../../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-edit-activity',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAdminComponent],
  templateUrl: './edit-activity.component.html'
})
export class EditActivityComponent implements OnInit {
  private adminService = inject(AdminService);
  private router = inject(Router);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  activite: any = null;
  id!: number;
  loading = false;

  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.adminService.getActivites().subscribe({
      next: (data) => {
        this.activite = { ...data.find((a: any) => a.id === this.id) };
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  onSubmit() {
    this.loading = true;
    this.adminService.updateActivite(this.id, this.activite).subscribe({
      next: () => this.router.navigate(['/admin/activites']),
      error: (err) => {
        console.error(err);
        this.loading = false;
      }
    });
  }
}