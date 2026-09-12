// src/app/features/animateur/components/presences/zone-enfant/zone-enfant.component.ts
import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute, Router } from '@angular/router';
import { AnimateurService, ZoneEnfantDTO, EnfantSimpleDTO, MessageEnfantDTO } from '../../../../../core/services/animateur.service';
import { SidebarAnimateurComponent } from '../../../../../shared/components/sidebar-animateur/sidebar-animateur.component';

@Component({
  selector: 'app-zone-enfant',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAnimateurComponent],
  templateUrl: './zone-enfant.component.html'
})
export class ZoneEnfantComponent implements OnInit {

  private service = inject(AnimateurService);
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private cdr = inject(ChangeDetectorRef);

  sessionId!: number;
  data!: ZoneEnfantDTO;
  loading = true;
  successMessage = '';
  errorMessage = '';

  // Formulaire message
  form = {
    enfantIdStr: '',
    contenu: ''
  };
  fichier: File | null = null;

  ngOnInit() {
    this.sessionId = Number(this.route.snapshot.paramMap.get('sessionId'));
    this.loadData();
  }

  loadData() {
    this.service.getZoneEnfant(this.sessionId).subscribe({
      next: (data) => {
        this.data = data;
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: () => {
        this.loading = false;
        this.errorMessage = 'Erreur lors du chargement';
        this.cdr.detectChanges();
      }
    });
  }

  onFichierChange(event: Event) {
    const input = event.target as HTMLInputElement;
    this.fichier = input.files?.[0] ?? null;
  }

  poster() {
    const formData = new FormData();
    formData.append('contenu', this.form.contenu);
    formData.append('enfantIdStr', this.form.enfantIdStr || '');
    if (this.fichier) formData.append('fichier', this.fichier);

    this.service.posterMessage(this.sessionId, formData).subscribe({
      next: () => {
        this.successMessage = 'Message publié !';
        this.errorMessage = '';
        this.form.contenu = '';
        this.form.enfantIdStr = '';
        this.fichier = null;
        this.loadData();
      },
      error: () => {
        this.errorMessage = "Erreur lors de l'envoi";
        this.successMessage = '';
        this.cdr.detectChanges();
      }
    });
  }

  supprimer(messageId: number) {
    if (!confirm('Supprimer ce message ?')) return;
    this.service.supprimerMessage(messageId).subscribe({
      next: () => this.loadData(),
      error: () => {
        this.errorMessage = 'Erreur suppression';
        this.cdr.detectChanges();
      }
    });
  }
}