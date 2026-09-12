import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { AdminService } from '../../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-session-edit',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAdminComponent],
  templateUrl: './session-edit.component.html'
})
export class SessionEditComponent implements OnInit {
  private adminService = inject(AdminService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  session: any = null;
  activites: any[] = [];
  animateurs: any[] = [];
  id!: number;
  loading = false;
  success = '';
  error = '';

  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));

    this.adminService.getActivites().subscribe({
      next: (data) => { this.activites = data; this.cdr.detectChanges(); },
      error: (err) => console.error('Erreur activités:', err)
    });

    this.adminService.getAnimateurs().subscribe({
      next: (data) => { this.animateurs = data; this.cdr.detectChanges(); },
      error: (err) => console.error('Erreur animateurs:', err)
    });

    this.adminService.getSessionsList().subscribe({
      next: (sessions) => {
        const found = sessions.find((s: any) => s.id === this.id);
        if (found) {
          // ✅ Forcer Number() pour que [ngValue] matche correctement
          this.session = {
            ...found,
            activiteId:  found.activiteId  ? Number(found.activiteId)  : null,
            animateurId: found.animateurId ? Number(found.animateurId) : null,
            capaciteMax: found.capaciteMax ? Number(found.capaciteMax) : null
          };
          this.cdr.detectChanges();
        }
      },
      error: (err) => console.error('Erreur session:', err)
    });
  }

  private extractError(err: any): string {
    if (typeof err.error === 'string') return err.error;
    if (err.error?.error)             return err.error.error;
    if (err.error?.message)           return err.error.message;
    return 'Une erreur est survenue.';
  }

  onSubmit() {
    this.loading = true;
    this.error = '';
    this.success = '';
    this.cdr.detectChanges();

    const payload = {
      ...this.session,
      activiteId:  this.session.activiteId  ? Number(this.session.activiteId)  : null,
      animateurId: this.session.animateurId ? Number(this.session.animateurId) : null,
      capaciteMax: this.session.capaciteMax ? Number(this.session.capaciteMax) : null
    };

    this.adminService.updateSession(this.id, payload).subscribe({
      next: () => {
        this.success = 'Session mise à jour avec succès !';
        this.loading = false;
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/admin/sessions', this.id]), 1200);
      },
      error: (err) => {
        this.error = this.extractError(err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}