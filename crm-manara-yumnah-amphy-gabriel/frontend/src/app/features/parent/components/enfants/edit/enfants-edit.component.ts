import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParentService, EnfantDTO } from '../../../../../core/services/parent.service';
import { SidebarParentComponent } from '../../../../../shared/components/sidebar-parent/sidebar-parent.component';

@Component({
  selector: 'app-enfants-edit',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, SidebarParentComponent],
  templateUrl: './enfants-edit.component.html'
})
export class EnfantsEditComponent implements OnInit {

  private parentService = inject(ParentService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  enfant: EnfantDTO = {
    nom: '', prenom: '', dateNaissance: '',
    allergies: '', notesMedicales: ''
  };

  loading = false;
  errorMsg: string | null = null;
  successMsg: string | null = null;

  private enfantId!: number;

  ngOnInit(): void {
    this.enfantId = Number(this.route.snapshot.paramMap.get('id'));

    this.parentService.getChildren().subscribe({
      next: (res: any) => {
        const liste = res.enfants ?? res;
        const found = liste.find((e: any) => e.id === this.enfantId);
        if (found) this.enfant = { ...found };
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMsg = 'Impossible de charger les données.';
        this.cdr.detectChanges();
      }
    });
  }

  valider(): string | null {
    if (!this.enfant.dateNaissance)
      return 'La date de naissance est obligatoire.';

    const today = new Date();
    const birth = new Date(this.enfant.dateNaissance);

    if (birth >= today)
      return 'La date de naissance doit être dans le passé.';

    const age = today.getFullYear() - birth.getFullYear();
    const mois = today.getMonth() - birth.getMonth();
    const ageEnMois = age * 12 + mois;

    if (ageEnMois < 12)
      return 'L\'enfant doit avoir au moins 1 an.';
    if (age > 18)
      return 'L\'enfant doit avoir moins de 18 ans.';

    return null;
  }

  onSubmit(): void {
    this.errorMsg = null;
    this.successMsg = null;

    const erreur = this.valider();
    if (erreur) {
      this.errorMsg = erreur;
      this.cdr.detectChanges();
      return;
    }

    this.loading = true;

    this.parentService.updateChild(this.enfantId, this.enfant).subscribe({
      next: () => {
        this.loading = false;
        this.successMsg = 'Profil mis à jour avec succès ✔️';
        this.cdr.detectChanges();
        setTimeout(() => this.router.navigate(['/parent/children/list']), 1200);
      },
      error: (err) => {
        this.loading = false;
        this.errorMsg = 'Une erreur est survenue. Veuillez réessayer.';
        console.error(err);
        this.cdr.detectChanges();
      }
    });
  }
}