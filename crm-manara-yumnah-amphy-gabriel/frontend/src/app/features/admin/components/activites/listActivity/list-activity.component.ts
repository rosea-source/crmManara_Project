import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-list-activity',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAdminComponent],
  templateUrl: './list-activity.component.html'
})
export class ListActivityComponent implements OnInit {
  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  activites: any[] = [];
  filtered: any[] = [];
  search = '';

  ngOnInit() {
    this.loadActivites();
  }

  loadActivites() {
    this.adminService.getActivites().subscribe({
      next: (data) => {
        this.activites = data;
        this.filtered = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  onSearch() {
    const q = this.search.toLowerCase();
    this.filtered = this.activites.filter(a =>
      a.titre?.toLowerCase().includes(q) ||
      a.description?.toLowerCase().includes(q)
    );
  }

  onDelete(id: number) {
    if (!confirm('Supprimer cette activité ?')) return;
    this.adminService.deleteActivite(id).subscribe({
      next: () => {
        setTimeout(() => this.loadActivites(), 300);
      },
      error: (err) => console.error(err)
    });
  }
}