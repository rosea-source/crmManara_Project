import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { AdminService } from '../../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-session-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarAdminComponent],
  templateUrl: './session-detail.component.html'
})
export class SessionDetailComponent implements OnInit {
  private adminService = inject(AdminService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  session: any = null;
  id!: number;
  inscrits = 0;
  pourcentage = 0;
  deleting = false;
  error = '';

  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.adminService.getSession(this.id).subscribe({
      next: (data) => {
        this.session = data;
        this.inscrits = data.inscrits || 0;
        const cap = data.capaciteMax || 1;
        this.pourcentage = Math.round((this.inscrits * 100) / cap);
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  fermerSession() {
    this.adminService.updateSession(this.id, {
      ...this.session,
      statut: 'Fermée'
    }).subscribe({
      next: () => this.router.navigate(['/admin/sessions']),
      error: (err) => console.error(err)
    });
  }

  supprimerSession() {
    if (!confirm('Supprimer cette session ? Cette action est irréversible.')) return;

    this.deleting = true;
    this.error = '';
    this.cdr.detectChanges();

    this.adminService.deleteSession(this.id).subscribe({
      next: () => this.router.navigate(['/admin/sessions']),
      error: (err) => {
        this.error = 'Impossible de supprimer la session.';
        this.deleting = false;
        this.cdr.detectChanges();
      }
    });
  }

  getStatutClass(statut: string): string {
    switch (statut) {
      case 'Prévue':   return 'bg-success';
      case 'Annulée':  return 'bg-danger';
      case 'En cours': return 'bg-warning text-dark';
      default:         return 'bg-secondary';
    }
  }
}