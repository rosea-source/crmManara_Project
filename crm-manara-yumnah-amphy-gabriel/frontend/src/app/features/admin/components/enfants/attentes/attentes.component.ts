// src/app/features/admin/components/sessions/attentes/attentes.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';
import { AdminService } from '../../../../../core/services/admin.service';

@Component({
  selector: 'app-attentes',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAdminComponent],
  templateUrl: './attentes.component.html'
})
export class AttentesComponent implements OnInit {
  private adminService = inject(AdminService);
  private route        = inject(ActivatedRoute);
  private cdr          = inject(ChangeDetectorRef);

  data:      any    = null;
  sessionId!: number;

  // ── Sélection multiple ──────────────────────────────
  selectedIds = new Set<number>();

  // ── État loading par action ─────────────────────────
  loadingId:      number | null = null;  // spinner sur une ligne
  loadingBulk     = false;               // spinner action groupée

  ngOnInit() {
    this.sessionId = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load() {
    this.adminService.getAttentes(this.sessionId).subscribe({
      next: (d) => {
        this.data        = d;
        this.selectedIds = new Set();
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }

  // ── Sélection ───────────────────────────────────────

  toggleSelect(id: number) {
    if (this.selectedIds.has(id)) this.selectedIds.delete(id);
    else                          this.selectedIds.add(id);
    this.cdr.detectChanges();
  }

  toggleAll(checked: boolean) {
    if (checked) {
      this.data?.inscriptions?.forEach((i: any) => this.selectedIds.add(i.id));
    } else {
      this.selectedIds.clear();
    }
    this.cdr.detectChanges();
  }

  get allSelected(): boolean {
    return (this.data?.inscriptions?.length ?? 0) > 0 &&
           this.data.inscriptions.every((i: any) => this.selectedIds.has(i.id));
  }

  // ── Action individuelle ─────────────────────────────

  accepter(id: number) {
    if (!confirm('Accepter cette inscription ?')) return;
    this.loadingId = id;
    this.cdr.detectChanges();

    this.adminService.acceptInscription(id).subscribe({
      next: () => { this.loadingId = null; window.location.reload(); },
      error: (err) => {
        this.loadingId = null;
        console.error('Erreur accepter:', err);
        this.cdr.detectChanges();
      }
    });
  }

  refuser(id: number) {
    if (!confirm('Refuser cette inscription ?')) return;
    this.loadingId = id;
    this.cdr.detectChanges();

    this.adminService.refuseInscription(id).subscribe({
      next: () => { this.loadingId = null; window.location.reload(); },
      error: (err) => {
        this.loadingId = null;
        console.error('Erreur refuser:', err);
        this.cdr.detectChanges();
      }
    });
  }

  // ── Actions groupées ────────────────────────────────

  accepterSelection() {
    if (this.selectedIds.size === 0) return;
    if (!confirm(`Accepter ${this.selectedIds.size} inscription(s) ?`)) return;

    this.loadingBulk = true;
    this.cdr.detectChanges();

    const ids = [...this.selectedIds];
    let done  = 0;

    ids.forEach(id => {
      this.adminService.acceptInscription(id).subscribe({
        next: () => {
          done++;
          if (done === ids.length) window.location.reload();
        },
        error: (err) => {
          done++;
          console.error(`Erreur accepter #${id}:`, err);
          if (done === ids.length) window.location.reload();
        }
      });
    });
  }

  refuserSelection() {
    if (this.selectedIds.size === 0) return;
    if (!confirm(`Refuser ${this.selectedIds.size} inscription(s) ?`)) return;

    this.loadingBulk = true;
    this.cdr.detectChanges();

    const ids = [...this.selectedIds];
    let done  = 0;

    ids.forEach(id => {
      this.adminService.refuseInscription(id).subscribe({
        next: () => {
          done++;
          if (done === ids.length) window.location.reload();
        },
        error: (err) => {
          done++;
          console.error(`Erreur refuser #${id}:`, err);
          if (done === ids.length) window.location.reload();
        }
      });
    });
  }
}