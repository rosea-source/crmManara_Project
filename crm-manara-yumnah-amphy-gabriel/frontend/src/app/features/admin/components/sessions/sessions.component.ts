import { Component, inject, OnInit, ChangeDetectorRef, NgZone } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-sessions',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, SidebarAdminComponent],
  templateUrl: './sessions.component.html'
})
export class SessionsComponent implements OnInit {

  private adminService = inject(AdminService);
  private router = inject(Router);
  private zone = inject(NgZone);
  private cdr = inject(ChangeDetectorRef);

  sessions: any[] = [];
  filtered: any[] = [];
  search = '';
  selectedStatut = '';

  ngOnInit() {
    this.loadData();
  }

  loadData() {
    this.adminService.getSessionsList().subscribe({
      next: (data) => {
        this.zone.run(() => {
          this.sessions = data ?? [];
          this.filtered = [...this.sessions];
          this.cdr.detectChanges();
        });
      },
      error: (err) => console.error(err)
    });
  }

  onFilter() {
    const q = this.search.toLowerCase();

    this.filtered = this.sessions.filter(s => {
      const matchSearch =
        !q ||
        s.activiteTitre?.toLowerCase().includes(q) ||
        s.lieu?.toLowerCase().includes(q) ||
        s.statut?.toLowerCase().includes(q);

      const matchStatut =
        !this.selectedStatut || s.statut === this.selectedStatut;

      return matchSearch && matchStatut;
    });
  }

  goToSession(id: number) {
    this.router.navigate(['/admin/sessions', id]);
  }

  trackById(index: number, item: any) {
    return item.id;
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'Prévue':   return 'bg-success';
      case 'En cours': return 'bg-warning text-dark';
      case 'Terminée': return 'bg-dark';
      case 'Annulée':  return 'bg-danger';
      default:         return 'bg-secondary';
    }
  }
}