// src/app/features/admin/components/users/list/list-user.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { AdminService } from '../../../../../core/services/admin.service';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-list-user',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAdminComponent],
  templateUrl: './list-user.component.html'
})
export class ListUserComponent implements OnInit {
  private adminService = inject(AdminService);
  private cdr = inject(ChangeDetectorRef);

  users:    any[] = [];
  filtered: any[] = [];
  search    = '';
  filtreRole = '';          // ← nouveau

  errorMessage   = '';
  successMessage = '';

  ngOnInit() {
    this.loadUsers();
  }

  loadUsers() {
    this.adminService.getUsersList().subscribe({
      next: (data) => {
        this.users    = data;
        this.applyFilters();
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  // ── Filtres combinés ──────────────────────────────────────────────────────

  applyFilters() {
    const q = this.search.toLowerCase();
    this.filtered = this.users.filter(u => {
      const matchSearch = !q ||
        (u.prenom + ' ' + u.nom).toLowerCase().includes(q) ||
        u.email?.toLowerCase().includes(q);
      const matchRole = !this.filtreRole || u.role === this.filtreRole;
      return matchSearch && matchRole;
    });
    this.cdr.detectChanges();
  }

  onSearch()     { this.applyFilters(); }
  onFiltreRole() { this.applyFilters(); }

  // ── Suppression ───────────────────────────────────────────────────────────

  onDelete(id: number) {
    if (!confirm('Supprimer cet utilisateur ? Cette action est irréversible.')) return;

    this.errorMessage   = '';
    this.successMessage = '';

    this.adminService.deleteUser(id).subscribe({
      next: () => {
        // Mise à jour locale immédiate — pas de setTimeout
        this.users    = this.users.filter(u => u.id !== id);
        this.applyFilters();
        this.successMessage = 'Utilisateur supprimé.';
        this.cdr.detectChanges();
        setTimeout(() => { this.successMessage = ''; this.cdr.detectChanges(); }, 3000);
      },
      error: (err) => {
        // Affiche le message d'erreur du backend (ex: contrainte FK)
        this.errorMessage = err.error?.message
          ?? err.error
          ?? 'Impossible de supprimer cet utilisateur (il a peut-être des données liées).';
        this.cdr.detectChanges();
        setTimeout(() => { this.errorMessage = ''; this.cdr.detectChanges(); }, 5000);
      }
    });
  }

  // ── Helpers badge ─────────────────────────────────────────────────────────

  getRoleLabel(role: string): string {
    switch (role) {
      case 'ROLE_ADMIN':     return 'Administrateur';
      case 'ROLE_ANIMATEUR': return 'Animateur';
      case 'ROLE_PARENT':    return 'Parent';
      default:               return role;
    }
  }

  getRoleBadge(role: string): string {
    switch (role) {
      case 'ROLE_ADMIN':     return 'bg-danger';
      case 'ROLE_ANIMATEUR': return 'bg-warning text-dark';
      case 'ROLE_PARENT':    return 'bg-dark';
      default:               return 'bg-secondary';
    }
  }
}