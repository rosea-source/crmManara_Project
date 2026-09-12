// src/app/features/animateur/components/presences/presence-session/presence-session.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { AnimateurService, PresenceItem, PresenceSaveRequest } from '../../../../../core/services/animateur.service';
import { SidebarAnimateurComponent } from '../../../../../shared/components/sidebar-animateur/sidebar-animateur.component';

interface PresenceRow {
  inscriptionId: number;
  nomComplet: string;
  statut: string;
  note: string;
}

@Component({
  selector: 'app-presence-session',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAnimateurComponent],
  templateUrl: './presence-session.component.html'
})
export class PresenceSessionComponent implements OnInit {

  private service = inject(AnimateurService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  sessionId!: number;
  participants: PresenceRow[] = [];
  loading = true;
  successMessage = '';
  errorMessage = '';

  ngOnInit() {
    this.sessionId = Number(this.route.snapshot.paramMap.get('sessionId'));
    this.service.getParticipants(this.sessionId).subscribe({
      next: (data) => {
        this.participants = data.map(p => ({
          inscriptionId: p.inscriptionId,
          nomComplet: p.nomComplet,
          statut: 'Présent',
          note: ''
        }));
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Erreur lors du chargement des participants';
        this.cdr.detectChanges();
      }
    });
  }

  submit() {
    const presences: PresenceSaveRequest[] = this.participants.map(p => ({
      inscriptionId: p.inscriptionId,
      statut: p.statut,
      note: p.note
    }));

    this.service.savePresences(this.sessionId, presences).subscribe({
      next: () => {
        this.successMessage = 'Présences enregistrées avec succès !';
        this.errorMessage = '';
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/animateur/presences']), 1500);
      },
      error: () => {
        this.errorMessage = "Erreur lors de l'enregistrement";
        this.successMessage = '';
        this.cdr.detectChanges();
      }
    });
  }
}