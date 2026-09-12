import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule, DatePipe } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParentService, EnfantDTO } from '../../../../../core/services/parent.service';
import { SidebarParentComponent } from '../../../../../shared/components/sidebar-parent/sidebar-parent.component';

@Component({
  selector: 'app-enfants-list',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, DatePipe, SidebarParentComponent],
  templateUrl: './enfants-list.component.html'
})
export class EnfantsListComponent implements OnInit {
  private parentService = inject(ParentService);
  private cdr = inject(ChangeDetectorRef);

  enfants: EnfantDTO[] = [];
  filteredEnfants: EnfantDTO[] = [];
  searchTerm = '';

  ngOnInit(): void {
    this.load();
  }

  load(): void {
    this.parentService.getChildren().subscribe({
      next: (res: any) => {
        this.enfants = res.enfants ?? res;
        this.filteredEnfants = this.enfants;
        this.cdr.detectChanges();
      },
      error: (err: any) => console.error(err)
    });
  }

  onSearch(): void {
    const term = this.searchTerm.toLowerCase().trim();
    this.filteredEnfants = this.enfants.filter(e =>
      e.prenom.toLowerCase().includes(term) ||
      e.nom.toLowerCase().includes(term)
    );
    this.cdr.detectChanges();
  }

  onDelete(id: number, prenom: string): void {
    if (!confirm(`Supprimer ${prenom} ? Cette action est irréversible.`)) return;
    this.parentService.deleteChild(id).subscribe({
      next: () => setTimeout(() => this.load(), 300),
      error: (err) => console.error(err)
    });
  }

  calculerAge(dateNaissance: string): number {
    if (!dateNaissance) return 0;
    const today = new Date();
    const birth = new Date(dateNaissance);
    let age = today.getFullYear() - birth.getFullYear();
    const m = today.getMonth() - birth.getMonth();
    if (m < 0 || (m === 0 && today.getDate() < birth.getDate())) age--;
    return age;
  }
}