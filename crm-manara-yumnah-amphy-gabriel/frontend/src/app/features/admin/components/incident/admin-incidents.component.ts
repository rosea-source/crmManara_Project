import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-admin-incidents',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, SidebarAdminComponent],
  templateUrl: './admin-incidents.component.html'
})
export class AdminIncidentsComponent implements OnInit {
  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  incidents: any[] = [];
  searchTerm = '';
  selectedGravite = '';

  get filteredIncidents(): any[] {
    const term = this.searchTerm.toLowerCase();
    return this.incidents.filter(i => {
      const matchSearch =
        !term ||
        i.enfantNomComplet?.toLowerCase().includes(term) ||
        i.activiteTitre?.toLowerCase().includes(term) ||
        i.description?.toLowerCase().includes(term);

      const matchGravite =
        !this.selectedGravite || i.gravite === this.selectedGravite;

      return matchSearch && matchGravite;
    });
  }

  ngOnInit() {
    this.adminService.getIncidents().subscribe({
      next: (data) => {
        this.incidents = data;
        this.cdr.detectChanges();
      }
    });
  }

  getBadgeClass(gravite: string): string {
    switch (gravite) {
      case 'Haute':   return 'bg-danger';
      case 'Moyenne': return 'bg-warning text-dark';
      case 'Faible':  return 'bg-dark';       // ← noir au lieu de bleu
      default:        return 'bg-dark';
    }
  }
}