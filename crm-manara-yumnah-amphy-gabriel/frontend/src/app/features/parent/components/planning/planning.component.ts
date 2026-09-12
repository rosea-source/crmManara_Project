import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParentService, EnfantDTO, InscriptionDTO } from '../../../../core/services/parent.service';
import { SidebarParentComponent } from '../../../../shared/components/sidebar-parent/sidebar-parent.component';

@Component({
  selector: 'app-planning',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, DatePipe, SidebarParentComponent],
  templateUrl: './planning.component.html'
})
export class PlanningComponent implements OnInit {

  private parentService = inject(ParentService);
  private cdr = inject(ChangeDetectorRef);

  enfants: EnfantDTO[] = [];
  inscriptions: InscriptionDTO[] = [];
  filteredInscriptions: InscriptionDTO[] = [];
  selectedEnfantId: number | null = null;
  loading = true;
  lundiCourant: Date = this.getLundiDeLaSemaine(new Date());

  ngOnInit(): void {
    this.parentService.getPlanning().subscribe({
      next: (data) => {
        this.enfants = data.enfants;
        this.inscriptions = data.inscriptions; // toutes les inscriptions sans filtrer
        this.appliquerFiltres();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur chargement planning', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  getLundiDeLaSemaine(date: Date): Date {
    const d = new Date(date);
    const jour = d.getDay() === 0 ? 7 : d.getDay();
    d.setDate(d.getDate() - jour + 1);
    d.setHours(0, 0, 0, 0);
    return d;
  }

  getVendrediDeLaSemaine(): Date {
    const v = new Date(this.lundiCourant);
    v.setDate(v.getDate() + 4);
    v.setHours(23, 59, 59, 999);
    return v;
  }

  semainePrecedente(): void {
    const d = new Date(this.lundiCourant);
    d.setDate(d.getDate() - 7);
    this.lundiCourant = d;
    this.appliquerFiltres();
  }

  semaineSuivante(): void {
    const d = new Date(this.lundiCourant);
    d.setDate(d.getDate() + 7);
    this.lundiCourant = d;
    this.appliquerFiltres();
  }

  allerAujourdhui(): void {
    this.lundiCourant = this.getLundiDeLaSemaine(new Date());
    this.appliquerFiltres();
  }

  estSemaineCourante(): boolean {
    const lundiAujourdhui = this.getLundiDeLaSemaine(new Date());
    return this.lundiCourant.toDateString() === lundiAujourdhui.toDateString();
  }

  getLabelSemaine(): string {
    const vendredi = this.getVendrediDeLaSemaine();
    const options: Intl.DateTimeFormatOptions = { day: 'numeric', month: 'long' };
    const debut = this.lundiCourant.toLocaleDateString('fr-CA', options);
    const fin = vendredi.toLocaleDateString('fr-CA', options);
    const annee = vendredi.getFullYear();
    return `${debut} – ${fin} ${annee}`;
  }

  onFilterEnfant(): void {
    this.appliquerFiltres();
  }

  appliquerFiltres(): void {
    const vendredi = this.getVendrediDeLaSemaine();
    let result = this.inscriptions.filter(i => {
      if (!i.session?.dateDebut) return false;
      const date = new Date(i.session.dateDebut);
      date.setHours(0, 0, 0, 0);
      return date >= this.lundiCourant && date <= vendredi;
    });
    if (this.selectedEnfantId !== null) {
      result = result.filter(i => i.enfant.id === this.selectedEnfantId);
    }
    this.filteredInscriptions = result;
    this.cdr.detectChanges();
  }
}