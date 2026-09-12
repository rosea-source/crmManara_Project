// src/app/features/animateur/components/incidents/report/incident-report.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AnimateurService, EnfantOption, SessionOption } from '../../../../../core/services/animateur.service';
import { SidebarAnimateurComponent } from '../../../../../shared/components/sidebar-animateur/sidebar-animateur.component';

@Component({
  selector: 'app-incident-report',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAnimateurComponent],
  templateUrl: './incident-report.component.html'
})
export class IncidentReportComponent implements OnInit {

  private service = inject(AnimateurService);
  private cdr     = inject(ChangeDetectorRef);

  allEnfants:  EnfantOption[]  = [];
  allSessions: SessionOption[] = [];

  // Sessions filtrées selon l'enfant sélectionné
  sessionsDisponibles: SessionOption[] = [];

  form = {
    enfantId:     null as number | null,
    sessionId:    null as number | null,
    destinataire: 'admin',
    gravite:      'Faible',
    description:  ''
  };

  loading        = true;
  successMessage = '';
  errorMessage   = '';

  ngOnInit() {
    this.service.getIncidentFormData().subscribe({
      next: (data) => {
        this.allEnfants  = data.enfants;
        this.allSessions = data.sessions;
        this.loading     = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading      = false;
        this.errorMessage = 'Erreur chargement données';
        this.cdr.detectChanges();
      }
    });
  }

  onEnfantChange() {
    this.form.sessionId = null;

    if (!this.form.enfantId) {
      this.sessionsDisponibles = [];
      this.cdr.detectChanges();
      return;
    }

    const enfantId = Number(this.form.enfantId);

    // Récupère tous les sessionId liés à cet enfant depuis allEnfants
    // (un enfant peut apparaître plusieurs fois si inscrit à plusieurs sessions)
    const sessionIds = this.allEnfants
      .filter(e => e.id === enfantId)
      .map(e => e.sessionId);

    // Filtre les sessions disponibles pour cet enfant
    this.sessionsDisponibles = this.allSessions.filter(s =>
      sessionIds.includes(s.id)
    );

    // Auto-sélection si une seule session disponible
    if (this.sessionsDisponibles.length === 1) {
      this.form.sessionId = this.sessionsDisponibles[0].id;
    }

    this.cdr.detectChanges();
  }

  isFirstOccurrence(enfantId: number): boolean {
    return this.allEnfants.findIndex(e => e.id === enfantId) ===
           this.allEnfants.indexOf(this.allEnfants.find(e => e.id === enfantId)!);
  }

  submit() {
    if (!this.form.enfantId) {
      this.errorMessage = 'Veuillez choisir un enfant.';
      return;
    }
    if (!this.form.sessionId) {
      this.errorMessage = 'Veuillez choisir une session.';
      return;
    }
    if (!this.form.description.trim() || this.form.description.trim().length < 5) {
      this.errorMessage = 'La description doit contenir au moins 5 caractères.';
      return;
    }

    this.errorMessage = '';

    const payload = {
      enfantId:     Number(this.form.enfantId),
      sessionId:    Number(this.form.sessionId),
      description:  this.form.description.trim(),
      gravite:      this.form.gravite,
      destinataire: this.form.destinataire
    };

    this.service.reportIncident(payload).subscribe({
      next: () => {
        this.successMessage      = 'Incident signalé avec succès ✔️';
        this.errorMessage        = '';
        this.form.description    = '';
        this.form.enfantId       = null;
        this.form.sessionId      = null;
        this.sessionsDisponibles = [];
        this.cdr.detectChanges();
        setTimeout(() => { this.successMessage = ''; this.cdr.detectChanges(); }, 4000);
      },
      error: (err) => {
        const detail = err.error?.errors?.[0]?.defaultMessage ?? err.error?.message;
        this.errorMessage   = detail ?? "Erreur lors de l'envoi";
        this.successMessage = '';
        this.cdr.detectChanges();
        setTimeout(() => { this.errorMessage = ''; this.cdr.detectChanges(); }, 4000);
      }
    });
  }
}