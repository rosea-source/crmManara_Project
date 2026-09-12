import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterModule, Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { FooterComponent } from '../../../shared/components/footer/footer.component';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterModule, NavbarComponent, FooterComponent],
  templateUrl: './register.component.html'
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private authService = inject(AuthService);
  private router = inject(Router);

  loading = false;
  errorMessage = '';
  successMessage = '';

  //  Regex lettres uniquement (avec accents)
  private nameRegex = /^[A-Za-zÀ-ÿ\s'-]+$/;

  form = this.fb.group({
    prenom: ['', [Validators.required, Validators.pattern(this.nameRegex)]],
    nom: ['', [Validators.required, Validators.pattern(this.nameRegex)]],
    email: ['', [Validators.required, Validators.email]],
    telephone: ['', [Validators.pattern(/^[0-9\s\+\-\(\)]{7,15}$/)]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  get f() {
    return this.form.controls;
  }

  extractError(err: any): string {
    if (typeof err.error === 'string') return err.error;
    if (err.error?.error) return err.error.error;
    if (err.error?.message) return err.error.message;
    return 'Une erreur est survenue.';
  }

  // Bloquer caractères invalides à la saisie
  allowOnlyLetters(event: KeyboardEvent) {
    const regex = /^[A-Za-zÀ-ÿ\s'-]$/;
    if (!regex.test(event.key)) {
      event.preventDefault();
    }
  }

  onSubmit() {
    this.errorMessage = '';
    this.successMessage = '';

    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.loading = true;

    this.authService.register(this.form.value).subscribe({
      next: () => {
        this.successMessage = 'Compte créé avec succès ! Redirection...';
        this.loading = false;
        setTimeout(() => this.router.navigate(['/login']), 1500);
      },
      error: (err) => {
        this.errorMessage = this.extractError(err);
        this.loading = false;
      }
    });
  }
}