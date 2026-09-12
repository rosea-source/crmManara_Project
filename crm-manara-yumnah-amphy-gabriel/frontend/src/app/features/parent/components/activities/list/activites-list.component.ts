import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParentService, ActiviteDTO } from '../../../../../core/services/parent.service';
import { SidebarParentComponent } from '../../../../../shared/components/sidebar-parent/sidebar-parent.component';

@Component({
  selector: 'app-activites-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, SidebarParentComponent],
  templateUrl: './activites-list.component.html'
})
export class ActivitesListComponent implements OnInit {
  private parentService = inject(ParentService);
  private cdr = inject(ChangeDetectorRef);

  activites: ActiviteDTO[] = [];
  filtered: ActiviteDTO[] = [];
  searchTerm = '';
  loading = true;

  ngOnInit(): void {
    this.parentService.getActivites().subscribe({
      next: (data) => {
        this.activites = Array.isArray(data) ? data : [];
        this.filtered = [...this.activites];
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Erreur activités:', err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  onSearch(): void {
    const term = this.searchTerm.toLowerCase().trim();
    this.filtered = term
      ? this.activites.filter(a => a.titre?.toLowerCase().includes(term))
      : [...this.activites];
    this.cdr.detectChanges();
  }
}