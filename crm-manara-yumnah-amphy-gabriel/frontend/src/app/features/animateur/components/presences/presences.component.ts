// src/app/features/animateur/components/presences/presences.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { AnimateurService, SessionDTO } from '../../../../core/services/animateur.service';
import { SidebarAnimateurComponent } from '../../../../shared/components/sidebar-animateur/sidebar-animateur.component';

@Component({
  selector: 'app-presences',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarAnimateurComponent],
  templateUrl: './presences.component.html'
})
export class PresencesComponent implements OnInit {

  private service = inject(AnimateurService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  sessions: SessionDTO[] = [];
  loading = true;

  ngOnInit() {
    this.service.getSessions().subscribe({
      next: (data) => {
        this.sessions = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'En cours': return 'bg-success';
      case 'Prévue':   return 'bg-primary';
      default:         return 'bg-secondary';
    }
  }

  goToPresence(sessionId: number) {
    this.router.navigate(['/animateur/presences', sessionId, 'presence']);
  }

  goToZone(sessionId: number) {
    this.router.navigate(['/animateur/presences', sessionId, 'zone-enfant']);
  }
}