// src/app/features/parent/components/dashboard/dashboard.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { ParentService, DashboardDTO } from '../../../../core/services/parent.service';
import { SidebarParentComponent } from '../../../../shared/components/sidebar-parent/sidebar-parent.component';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe, SidebarParentComponent],
  templateUrl: './dashboard.component.html'
})
export class DashboardComponent implements OnInit {

  private parentService = inject(ParentService);
  private cdr = inject(ChangeDetectorRef);

  dashboard: DashboardDTO | null = null;
  loading = true;
  error: string | null = null;

  ngOnInit(): void {
    this.parentService.getDashboard().subscribe({
      next: (data) => {
        this.dashboard = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.error = 'Impossible de charger le tableau de bord.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}