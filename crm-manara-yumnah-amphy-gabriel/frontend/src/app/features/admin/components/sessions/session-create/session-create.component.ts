import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AdminService } from '../../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-session-create',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAdminComponent],
  templateUrl: './session-create.component.html'
})
export class SessionCreateComponent implements OnInit {
  private adminService = inject(AdminService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  activites: any[] = [];
  animateurs: any[] = [];
  loading = false;
  success = '';
  error = '';

  session: any = {
    activiteId: null,
    animateurId: null,
    dateDebut: '',
    dateFin: '',
    heureDebut: '',
    heureFin: '',
    lieu: '',
    capaciteMax: null,
    statut: 'Prévue'
  };

  ngOnInit() {
    this.adminService.getActivites().subscribe({
      next: (data) => { this.activites = data; this.cdr.detectChanges(); },
      error: (err) => console.error('Erreur activités:', err)
    });
    this.adminService.getAnimateurs().subscribe({
      next: (data) => { this.animateurs = data; this.cdr.detectChanges(); },
      error: (err) => console.error('Erreur animateurs:', err)
    });
  }

  onSubmit() {
    this.loading = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    // Forcer les types corrects avant envoi
    const payload = {
      ...this.session,
      activiteId: this.session.activiteId ? Number(this.session.activiteId) : null,
      animateurId: this.session.animateurId ? Number(this.session.animateurId) : null,
      capaciteMax: this.session.capaciteMax ? Number(this.session.capaciteMax) : null
    };

    this.adminService.createSession(payload).subscribe({
      next: () => {
        this.success = 'Session créée avec succès !';
        this.loading = false;
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/admin/sessions']), 1200);
      },
      error: (err) => {
        this.error = err?.error ?? 'Erreur lors de la création.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}