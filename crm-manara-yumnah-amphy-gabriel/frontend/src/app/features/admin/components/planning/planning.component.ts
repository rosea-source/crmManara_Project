import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-planning',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarAdminComponent],
  templateUrl: './planning.component.html'
})
export class PlanningComponent implements OnInit {
  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  sessions: any[] = [];
  jours = ['Lundi', 'Mardi', 'Mercredi', 'Jeudi', 'Vendredi'];
  lundiCourant: Date = this.getLundiDeLaSemaine(new Date());

  ngOnInit() {
    this.adminService.getSessionsList().subscribe({
      next: (data) => {
        this.sessions = data;
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

  semainePrecedente() {
    const d = new Date(this.lundiCourant);
    d.setDate(d.getDate() - 7);
    this.lundiCourant = d;
  }

  semaineSuivante() {
    const d = new Date(this.lundiCourant);
    d.setDate(d.getDate() + 7);
    this.lundiCourant = d;
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

  getSessionsParJour(jour: string): any[] {
    const jourMap: any = {
      'Lundi': 1, 'Mardi': 2, 'Mercredi': 3, 'Jeudi': 4, 'Vendredi': 5
    };
    const vendredi = this.getVendrediDeLaSemaine();
    return this.sessions.filter(s => {
      if (!s.dateDebut) return false;
      const date = new Date(s.dateDebut);
      return date.getDay() === jourMap[jour]
        && date >= this.lundiCourant
        && date <= vendredi;
    });
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'Prévue':   return 'status-prevue';
      case 'Annulée':  return 'status-annulee';
      case 'Terminée': return 'status-terminee';
      default:         return 'status-prevue';
    }
  }
  allerAujourdhui() {
  this.lundiCourant = this.getLundiDeLaSemaine(new Date());
}
}