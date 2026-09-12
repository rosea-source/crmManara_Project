import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ParentService, EnfantDTO } from '../../../../../core/services/parent.service';
import { SidebarParentComponent } from '../../../../../shared/components/sidebar-parent/sidebar-parent.component';

@Component({
  selector: 'app-enfants-add',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule, SidebarParentComponent],
  templateUrl: './enfants-add.component.html'
})
export class EnfantsAddComponent {
  private parentService = inject(ParentService);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  enfant: EnfantDTO = {
    nom: '', prenom: '', dateNaissance: '',
    allergies: '', notesMedicales: ''
  };

  loading = false;
  errorMsg: string | null = null;
  successMsg: string | null = null;

  valider(): string | null {
    const lettresOnly = /^[a-zA-ZÀ-ÿ\s\-']+$/;

    if (!this.enfant.prenom.trim())
      return 'Le prénom est obligatoire.';
    if (!lettresOnly.test(this.enfant.prenom))
      return 'Le prénom ne peut contenir que des lettres.';
    if (!this.enfant.nom.trim())
      return 'Le nom est obligatoire.';
    if (!lettresOnly.test(this.enfant.nom))
      return 'Le nom ne peut contenir que des lettres.';
    if (!this.enfant.dateNaissance)
      return 'La date de naissance est obligatoire.';

    // Date dans le futur ?
    const today = new Date();
    const birth = new Date(this.enfant.dateNaissance);
    if (birth >= today)
      return 'La date de naissance doit être dans le passé.';

    // Âge max raisonnable
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

    // Vérifie si le nom+prénom existe déjà
    this.parentService.getChildren().subscribe({
      next: (res: any) => {
        const liste: EnfantDTO[] = res.enfants ?? res;
        const existe = liste.some(e =>
          e.prenom.toLowerCase() === this.enfant.prenom.toLowerCase() &&
          e.nom.toLowerCase() === this.enfant.nom.toLowerCase()
        );

        if (existe) {
          this.errorMsg = 'Un enfant avec ce nom et prénom existe déjà.';
          this.cdr.detectChanges();
          return;
        }

        this.loading = true;
        this.parentService.addChild(this.enfant).subscribe({
          next: () => {
            this.loading = false;
            this.successMsg = 'Enfant ajouté avec succès ✔️';
            this.cdr.detectChanges();
            setTimeout(() => this.router.navigate(['/parent/children/list']), 1200);
          },
          error: (err) => {
            this.loading = false;
            this.errorMsg = err.error?.error || 'Une erreur est survenue.';
            this.cdr.detectChanges();
          }
        });
      }
    });
  }
}