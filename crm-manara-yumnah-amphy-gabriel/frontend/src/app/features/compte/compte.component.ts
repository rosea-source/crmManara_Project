// src/app/features/compte/compte.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { CompteService, CompteDTO, CompteUpdateRequest } from '../../core/services/compte.service';
import { SidebarAnimateurComponent } from '../../shared/components/sidebar-animateur/sidebar-animateur.component';
import { SidebarParentComponent } from '../../shared/components/sidebar-parent/sidebar-parent.component';

@Component({
  selector: 'app-compte',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAnimateurComponent, SidebarParentComponent],
  templateUrl: './compte.component.html'
})
export class CompteComponent implements OnInit {

  private service = inject(CompteService);
  private cdr = inject(ChangeDetectorRef);

  compte: CompteDTO | null = null;
  loading = true;
  successMessage = '';
  errorMessage = '';

  form: CompteUpdateRequest = {
    prenom: '',
    nom: '',
    email: '',
    telephone: '',
    motDePasse: '',
    motDePasseConf: '',
    adresse: '',
    diplome: '',
    specialite: ''
  };

  get isAnimateur(): boolean {
    return this.compte?.role === 'ROLE_ANIMATEUR';
  }

  get isParent(): boolean {
    return this.compte?.role === 'ROLE_PARENT';
  }

  ngOnInit() {
    this.service.getCompte().subscribe({
      next: (data) => {
        this.compte = data;
        this.form.prenom     = data.prenom     ?? '';
        this.form.nom        = data.nom        ?? '';
        this.form.email      = data.email      ?? '';
        this.form.telephone  = data.telephone  ?? '';
        this.form.adresse    = data.adresse    ?? '';
        this.form.diplome    = data.diplome    ?? '';
        this.form.specialite = data.specialite ?? '';
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Erreur lors du chargement du profil';
        this.cdr.detectChanges();
      }
    });
  }

  submit() {
    if (this.form.motDePasse && this.form.motDePasse !== this.form.motDePasseConf) {
      this.errorMessage = 'Les mots de passe ne correspondent pas';
      this.successMessage = '';
      return;
    }

    this.service.updateCompte(this.form).subscribe({
      next: () => {
        this.successMessage = 'Profil mis à jour avec succès !';
        this.errorMessage = '';
        this.form.motDePasse = '';
        this.form.motDePasseConf = '';
        this.cdr.detectChanges();
      },
      error: () => {
        this.errorMessage = 'Erreur lors de la mise à jour';
        this.successMessage = '';
        this.cdr.detectChanges();
      }
    });
  }
}