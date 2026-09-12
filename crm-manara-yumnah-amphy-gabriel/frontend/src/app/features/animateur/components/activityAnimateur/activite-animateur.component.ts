// src/app/features/animateur/components/activityAnimateur/activite-animateur.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AnimateurService, ActivitesParJour, ActiviteItemDTO } from '../../../../core/services/animateur.service';
import { SidebarAnimateurComponent } from '../../../../shared/components/sidebar-animateur/sidebar-animateur.component';

@Component({
  selector: 'app-activite-animateur',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAnimateurComponent],
  templateUrl: './activite-animateur.component.html'
})
export class ActiviteAnimateurComponent implements OnInit {

  private animateurService = inject(AnimateurService);
  private cdr = inject(ChangeDetectorRef);

  activitesParJour: ActivitesParJour = {};
  tousLesJours: string[] = [];
  isEmpty = false;
  loading = true;

  // ─── Filtres ────────────────────────────────
  filtreJour  = '';
  filtreTitre = '';

  // ─── Jours filtrés ───────────────────────────
  get jours(): string[] {
    if (!this.filtreJour) return this.tousLesJours;
    return this.tousLesJours.filter(j => j === this.filtreJour);
  }

  // ─── Items filtrés par titre dans chaque jour ─
  getItemsFiltres(jour: string): ActiviteItemDTO[] {
    const items = this.activitesParJour[jour] ?? [];
    if (!this.filtreTitre) return items;
    return items.filter(i =>
      i.titre.toLowerCase().includes(this.filtreTitre.toLowerCase())
    );
  }

  // ─── Vérifier si tout est vide après filtre ──
  get isVideApresFiltre(): boolean {
    return this.jours.every(j => this.getItemsFiltres(j).length === 0);
  }

  resetFiltres() {
    this.filtreJour  = '';
    this.filtreTitre = '';
  }

  ngOnInit() {
    this.animateurService.getActivites().subscribe({
      next: (data) => {
        this.activitesParJour = data;
        this.tousLesJours = Object.keys(data);
        this.isEmpty = this.tousLesJours.length === 0;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur activités animateur :', err);
        this.isEmpty = true;
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}