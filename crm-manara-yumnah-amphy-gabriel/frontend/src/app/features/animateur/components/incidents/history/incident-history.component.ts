// src/app/features/animateur/components/incidents/history/incident-history.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AnimateurService, IncidentHistoryDTO } from '../../../../../core/services/animateur.service';
import { SidebarAnimateurComponent } from '../../../../../shared/components/sidebar-animateur/sidebar-animateur.component';

@Component({
  selector: 'app-incident-history',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAnimateurComponent],
  templateUrl: './incident-history.component.html'
})
export class IncidentHistoryComponent implements OnInit {

  private service = inject(AnimateurService);
  private cdr     = inject(ChangeDetectorRef);

  incidents: IncidentHistoryDTO[] = [];
  loading        = true;
  confirmDeleteId: number | null = null; // id en attente de confirmation

  // ─── Filtres ─────────────────────────────────
  filtreGravite  = '';
  filtreActivite = '';
  filtreEnfant   = '';

  get activitesDisponibles(): string[] {
    return [...new Set(this.incidents.map(i => i.activiteTitre))].sort();
  }

  get incidentsFiltres(): IncidentHistoryDTO[] {
    return this.incidents.filter(i => {
      const matchGravite  = !this.filtreGravite  || i.gravite === this.filtreGravite;
      const matchActivite = !this.filtreActivite || i.activiteTitre === this.filtreActivite;
      const matchEnfant   = !this.filtreEnfant   ||
        i.enfantNomComplet.toLowerCase().includes(this.filtreEnfant.toLowerCase());
      return matchGravite && matchActivite && matchEnfant;
    });
  }

  resetFiltres() {
    this.filtreGravite  = '';
    this.filtreActivite = '';
    this.filtreEnfant   = '';
  }

  ngOnInit() {
    this.service.getIncidentHistory().subscribe({
      next: (data) => {
        this.incidents = data;
        this.loading   = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  // ─── Suppression ─────────────────────────────

  demandeSuppression(id: number) {
    this.confirmDeleteId = id;
    this.cdr.detectChanges();
  }

  annulerSuppression() {
    this.confirmDeleteId = null;
    this.cdr.detectChanges();
  }

  confirmerSuppression(id: number) {
    this.service.deleteIncident(id).subscribe({
      next: () => {
        this.incidents       = this.incidents.filter(i => i.id !== id);
        this.confirmDeleteId = null;
        this.cdr.detectChanges();
      },
      error: () => {
        this.confirmDeleteId = null;
        this.cdr.detectChanges();
      }
    });
  }
}