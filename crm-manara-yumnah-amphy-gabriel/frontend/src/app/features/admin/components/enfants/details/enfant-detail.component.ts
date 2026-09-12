import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { SidebarAdminComponent } from '../../../../../shared/components/sidebar-admin/sidebar-admin.component';

@Component({
  selector: 'app-enfant-detail',
  standalone: true,
  imports: [CommonModule, RouterModule, SidebarAdminComponent],
  templateUrl: './enfant-detail.component.html'
})
export class EnfantDetailComponent implements OnInit {
  private http = inject(HttpClient);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  enfant: any = null;
  id!: number;

  ngOnInit() {
    this.id = Number(this.route.snapshot.paramMap.get('id'));
    this.http.get<any>(`http://localhost:8080/api/admin/enfants/${this.id}`, {
      withCredentials: true
    }).subscribe({
      next: (data) => {
        this.enfant = data;
        this.cdr.detectChanges();
      },
      error: (err) => console.error(err)
    });
  }
}