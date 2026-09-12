import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { AdminService } from '../../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-user-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarAdminComponent],
  templateUrl: './user-detail.component.html'
})
export class UserDetailComponent implements OnInit {
  private adminService = inject(AdminService);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  user: any = null;
  id!: number;

  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.adminService.getUserDetail(this.id).subscribe({
      next: (data) => {
        this.user = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  getRoleLabel(role: string): string {
    return role?.replace('ROLE_', '').charAt(0) +
           role?.replace('ROLE_', '').slice(1).toLowerCase() || '';
  }
}