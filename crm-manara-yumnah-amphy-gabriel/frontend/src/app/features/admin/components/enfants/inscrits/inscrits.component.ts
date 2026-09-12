import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';
import { AdminService } from '../../../../../core/services/admin.service';


@Component({
  selector: 'app-inscrits',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarAdminComponent],
  templateUrl: './inscrits.component.html'
})
export class InscritsComponent implements OnInit {
  private adminService = inject(AdminService);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  data: any = null;
  sessionId!: number;

  ngOnInit() {
    this.sessionId = Number(this.route.snapshot.paramMap.get('id'));
    this.load();
  }

  load() {
    this.adminService.getInscrits(this.sessionId).subscribe({
      next: (d) => { this.data = d; this.cdr.detectChanges(); },
      error: (err) => console.error(err)
    });
  }

  retirer(id: number) {
    if (!confirm('Retirer cet enfant de la session ?')) return;
    this.adminService.retirerInscription(id).subscribe({
      next: () => setTimeout(() => this.load(), 300),
      error: (err) => console.error(err)
    });
  }
}