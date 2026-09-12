import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParentService, SessionDTO, EnfantDTO } from '../../../../../core/services/parent.service';
import { SidebarParentComponent } from '../../../../../shared/components/sidebar-parent/sidebar-parent.component';

@Component({
  selector: 'app-register-session',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, SidebarParentComponent],
  templateUrl: './register-session.component.html'
})
export class RegisterSessionComponent implements OnInit {
  private parentService = inject(ParentService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  session: SessionDTO | null = null;
  enfants: EnfantDTO[] = [];
  selectedEnfantId: number | null = null;
  sessionId!: number;

  loading = true;
  submitting = false;
  error = '';
  success = '';

  ngOnInit(): void {
    this.sessionId = Number(this.route.snapshot.paramMap.get('sessionId'));

    if (!this.sessionId) {
      this.error = 'Session invalide.';
      this.loading = false;
      this.cdr.detectChanges();
      return;
    }

    this.parentService.getRegistrationData(this.sessionId).subscribe({
      next: (data) => {
        this.session = data.session;
        this.enfants = data.enfants ?? [];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur chargement:', err);
        this.error = 'Impossible de charger la session.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  submit(): void {
    if (!this.selectedEnfantId) {
      this.error = 'Veuillez sélectionner un enfant.';
      this.cdr.detectChanges();
      return;
    }

    this.submitting = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    this.parentService.registerToSession({
      sessionId: this.sessionId,
      enfantId: this.selectedEnfantId
    }).subscribe({
      next: () => {
        this.success = 'Inscription réussie ✔️';
        this.submitting = false;
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/parent/activities/list']), 1500);
      },
      error: (err) => {
        this.error = err?.error?.error ?? 'Une erreur est survenue.';
        this.submitting = false;
        this.cdr.detectChanges();
      }
    });
  }
}