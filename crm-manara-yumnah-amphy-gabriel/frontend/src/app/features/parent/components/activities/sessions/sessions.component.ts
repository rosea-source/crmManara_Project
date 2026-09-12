import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParentService, SessionDTO } from '../../../../../core/services/parent.service';
import { SidebarParentComponent } from '../../../../../shared/components/sidebar-parent/sidebar-parent.component';

@Component({
  selector: 'app-sessions',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, SidebarParentComponent],
  templateUrl: './sessions.component.html'
})
export class SessionsComponent implements OnInit {
  private parentService = inject(ParentService);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  sessions: SessionDTO[] = [];
  filtered: SessionDTO[] = [];
  loading = true;
  activiteId!: number;

  filterDate = '';
  filterTime = '';
  sortPlaces = '';

  // Statuts et sessions qui ne doivent jamais apparaître
  private readonly STATUTS_EXCLUS = ['Terminée', 'Annulée', 'Fermée'];

  private isDisponible(s: SessionDTO): boolean {
    // Exclure si statut fermé/terminé/annulé
    if (this.STATUTS_EXCLUS.includes(s.statut ?? '')) return false;
    // Exclure si complet (capaciteMax === 0)
    if ((s.capaciteMax ?? 0) === 0) return false;
    return true;
  }

  ngOnInit(): void {
    this.activiteId = Number(this.route.snapshot.paramMap.get('activiteId'));

    if (!this.activiteId) {
      this.loading = false;
      this.cdr.detectChanges();
      return;
    }

    this.parentService.getSessionsByActivite(this.activiteId).subscribe({
      next: (data) => {
        const all = Array.isArray(data) ? data : [];
        //  On garde seulement les sessions disponibles
        this.sessions = all.filter(s => this.isDisponible(s));
        this.filtered = [...this.sessions];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur sessions:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyFilters(): void {
    // On part toujours des sessions déjà filtrées (disponibles uniquement)
    let result = [...this.sessions];

    if (this.filterDate) {
      result = result.filter(s => s.dateDebut?.startsWith(this.filterDate));
    }

    if (this.filterTime) {
      result = result.filter(s => {
        const h = parseInt((s.heureDebut ?? '0').split(':')[0], 10);
        if (this.filterTime === 'matin')      return h >= 6  && h < 12;
        if (this.filterTime === 'apres-midi') return h >= 12 && h < 18;
        if (this.filterTime === 'soir')       return h >= 18;
        return true;
      });
    }

    if (this.sortPlaces === 'asc') {
      result.sort((a, b) => (a.capaciteMax ?? 0) - (b.capaciteMax ?? 0));
    } else if (this.sortPlaces === 'desc') {
      result.sort((a, b) => (b.capaciteMax ?? 0) - (a.capaciteMax ?? 0));
    }

    this.filtered = result;
    this.cdr.detectChanges();
  }

  resetFilters(): void {
    this.filterDate = '';
    this.filterTime = '';
    this.sortPlaces = '';
    this.filtered = [...this.sessions];
    this.cdr.detectChanges();
  }
}