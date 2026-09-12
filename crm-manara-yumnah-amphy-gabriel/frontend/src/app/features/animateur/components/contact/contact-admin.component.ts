import { Component, inject, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { SidebarAnimateurComponent } from '../../../../shared/components/sidebar-animateur/sidebar-animateur.component';

@Component({
  selector: 'app-contact-admin-animateur',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, SidebarAnimateurComponent],
  templateUrl: './contact-admin.component.html'
})
export class ContactAdminAnimateurComponent {
  private http = inject(HttpClient);
  private cdr = inject(ChangeDetectorRef);

  sujet = '';
  message = '';
  loading = false;
  success = '';
  error = '';

  private validate(): boolean {
    if (!this.sujet.trim()) { this.error = 'Le sujet est requis.'; return false; }
    if (!this.message.trim()) { this.error = 'Le message est requis.'; return false; }
    return true;
  }

  onSubmit(): void {
    this.error = '';
    this.success = '';

    if (!this.validate()) { this.cdr.detectChanges(); return; }

    this.loading = true;
    this.cdr.detectChanges();

    this.http.post('http://localhost:8080/api/contact', {
      sujet: this.sujet,
      message: this.message
    }, { withCredentials: true }).subscribe({
      next: () => {
        this.success = 'Message envoyé avec succès ! Nous vous répondrons sous 24-48h.';
        this.sujet = '';
        this.message = '';
        this.loading = false;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.error = 'Erreur lors de l\'envoi. Veuillez réessayer.';
        this.loading = false;
        this.cdr.detectChanges();
      }
    });
  }
}