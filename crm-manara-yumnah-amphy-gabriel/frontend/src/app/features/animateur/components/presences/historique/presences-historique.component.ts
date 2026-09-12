// src/app/features/animateur/components/presences/historique/presences-historique.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AnimateurService, PresenceDTO } from '../../../../../core/services/animateur.service';
import { SidebarAnimateurComponent } from '../../../../../shared/components/sidebar-animateur/sidebar-animateur.component';

@Component({
  selector: 'app-presences-historique',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAnimateurComponent],
  templateUrl: './presences-historique.component.html'
})
export class PresencesHistoriqueComponent implements OnInit {

  private service = inject(AnimateurService);
  private cdr = inject(ChangeDetectorRef);

  presences: PresenceDTO[] = [];
  loading = true;

  // ─── Filtres ────────────────────────────────
  filtreSession = '';
  filtreDate    = '';
  filtreStatut  = '';

  // ─── Listes déroulantes dynamiques ──────────
  get sessionsDisponibles(): string[] {
    const all = this.presences.map(p => p.activiteTitre);
    return [...new Set(all)].sort();
  }

  get datesDisponibles(): string[] {
    const all = this.presences.map(p => p.sessionDate);
    return [...new Set(all)].sort();
  }

  // ─── Presences filtrées ──────────────────────
  get presencesFiltrees(): PresenceDTO[] {
    return this.presences.filter(p => {
      const matchSession = !this.filtreSession || p.activiteTitre === this.filtreSession;
      const matchDate    = !this.filtreDate    || p.sessionDate    === this.filtreDate;
      const matchStatut  = !this.filtreStatut  || p.statut         === this.filtreStatut;
      return matchSession && matchDate && matchStatut;
    });
  }

  // ─── Stats sur la sélection filtrée ─────────
  get totalPresents(): number {
    return this.presencesFiltrees.filter(p => p.statut === 'Présent').length;
  }

  get totalAbsents(): number {
    return this.presencesFiltrees.filter(p => p.statut === 'Absent').length;
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'Présent': return 'bg-success';
      case 'Absent':  return 'bg-danger';
      default:        return 'bg-warning';
    }
  }

  resetFiltres() {
    this.filtreSession = '';
    this.filtreDate    = '';
    this.filtreStatut  = '';
  }

  ngOnInit() {
    this.service.getHistoriquePresences().subscribe({
      next: (data) => {
        this.presences = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}