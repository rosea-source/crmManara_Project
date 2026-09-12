// src/app/features/animateur/components/dashboard/dashboard.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AnimateurService, AnimateurDashboardDTO } from '../../../../core/services/animateur.service';
import { SidebarAnimateurComponent } from '../../../../shared/components/sidebar-animateur/sidebar-animateur.component';

@Component({
  selector: 'app-animateur-dashboard',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarAnimateurComponent],
  templateUrl: './dashboard-animateur.component.html'
})
export class AnimateurDashboardComponent implements OnInit {

  private animateurService = inject(AnimateurService);
  private cdr = inject(ChangeDetectorRef);

  dashboard: AnimateurDashboardDTO = {
    prenomAnimateur: 'Animateur',
    sessionsActives: 0,
    enfantsInscrits: 0,
    prochaineSession: '-'
  };

  ngOnInit() {
    this.animateurService.getDashboard().subscribe({
      next: (data) => {
        this.dashboard = data;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur dashboard animateur :', err);
      }
    });
  }
}