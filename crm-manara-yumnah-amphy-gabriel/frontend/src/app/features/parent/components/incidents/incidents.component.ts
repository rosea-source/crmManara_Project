import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParentService } from '../../../../core/services/parent.service';
import { SidebarParentComponent } from '../../../../shared/components/sidebar-parent/sidebar-parent.component';

@Component({
  selector: 'app-incidents',
  standalone: true,
  imports: [CommonModule, RouterModule, DatePipe, FormsModule, SidebarParentComponent],
  templateUrl: './incidents.component.html'
})
export class IncidentsComponent implements OnInit {
  private parentService = inject(ParentService);
  private cdr = inject(ChangeDetectorRef);

  incidents: any[] = [];
  filtered: any[] = [];
  loading = true;

  filterGravite = 'Tous';
  gravites = ['Tous', 'Haute', 'Moyenne', 'Faible'];

  searchQuery = '';
  filterEnfant = '';
  filterDate = '';

  get enfantsUniques(): string[] {
    const noms = this.incidents.map(i => i.enfantNomComplet).filter(Boolean);
    return [...new Set(noms)].sort();
  }

  ngOnInit(): void {
    this.parentService.getIncidents().subscribe({
      next: (data: any) => {
        this.incidents = data;
        this.applyFilters();
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error(err);
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }

  applyFilters(): void {
    const query = this.searchQuery.toLowerCase().trim();

    this.filtered = this.incidents.filter(i => {
      const matchGravite = this.filterGravite === 'Tous' || i.gravite === this.filterGravite;

      const matchEnfant = !this.filterEnfant || i.enfantNomComplet === this.filterEnfant;

      const matchDate = !this.filterDate || (i.date && i.date.startsWith(this.filterDate));

      const matchSearch = !query ||
        i.enfantNomComplet?.toLowerCase().includes(query) ||
        i.description?.toLowerCase().includes(query) ||
        i.activiteTitre?.toLowerCase().includes(query);

      return matchGravite && matchEnfant && matchDate && matchSearch;
    });

    this.cdr.detectChanges();
  }

  onFilter(gravite: string): void {
    this.filterGravite = gravite;
    this.applyFilters();
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onEnfantChange(): void {
    this.applyFilters();
  }

  onDateChange(): void {
    this.applyFilters();
  }

  resetFilters(): void {
    this.searchQuery = '';
    this.filterEnfant = '';
    this.filterDate = '';
    this.filterGravite = 'Tous';
    this.applyFilters();
  }

  getBadgeClass(gravite: string): string {
    switch (gravite) {
      case 'Haute':   return 'bg-danger';
      case 'Moyenne': return 'bg-warning text-dark';
      default:        return 'bg-dark';
    }
  }
}