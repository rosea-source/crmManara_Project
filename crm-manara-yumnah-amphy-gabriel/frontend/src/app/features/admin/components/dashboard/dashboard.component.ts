import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarAdminComponent, DatePipe],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  stats: any = {
    usersCount: 0,
    activitiesCount: 0,
    sessionsCount: 0,
    animateursCount: 0,
    prevues: 0,
    enCours: 0,
    terminees: 0,
    annulees: 0,
    lastUpdate: null
  };

  recentSessions: any[] = [];

  ngOnInit() {
    console.log('=== DASHBOARD INIT ===');

    this.adminService.getDashboard().subscribe({
      next: (data) => {
        console.log('=== DATA REÇUE ===', data);
        console.log(typeof data.lastUpdate, data.lastUpdate);
        this.stats = data;
        this.recentSessions = data.recentSessions || [];

        // 🔥 force refresh UI
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('=== ERREUR ===', err);
      }
    });
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'Prévue': return 'bg-success';
      case 'Annulée': return 'bg-danger';
      case 'En cours': return 'bg-warning text-dark';
      default: return 'bg-secondary';
    }
  }
}