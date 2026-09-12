import { Component, inject, OnInit, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule, ActivatedRoute } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest } from '../../../shared/models/user.model';
import { NavbarComponent } from '../../../shared/components/navbar/navbar.component';
import { FooterComponent } from '../../../shared/components/footer/footer.component';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule, NavbarComponent, FooterComponent],
  templateUrl: './login.component.html'
})
export class LoginComponent implements OnInit {
  private authService = inject(AuthService);
  private route = inject(ActivatedRoute);
  private cdr = inject(ChangeDetectorRef);

  credentials: LoginRequest = { email: '', password: '' };

  errors: Record<string, string> = {};
  errorMessage = '';
  logoutSuccess = false;
  loading = false;

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.logoutSuccess = !!params['logout'];
      if (params['error'] === 'notfound') {
        this.errorMessage = 'Compte non trouvé';
      }
    });
  }

  private validate(): boolean {
    this.errors = {};

    if (!this.credentials.email?.trim()) {
      this.errors['email'] = "L'email est requis.";
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.credentials.email)) {
      this.errors['email'] = "L'email n'est pas valide.";
    }

    if (!this.credentials.password?.trim()) {
      this.errors['password'] = 'Le mot de passe est requis.';
    }

    return Object.keys(this.errors).length === 0;
  }

  onSubmit(): void {
    this.errorMessage = '';
    this.logoutSuccess = false;

    if (!this.validate()) {
      this.cdr.detectChanges();
      return;
    }

    this.loading = true;
    this.cdr.detectChanges();

    this.authService.login(this.credentials.email, this.credentials.password).subscribe({
      next: () => {
        // loading reste true pendant la redirection — normal
        this.authService.redirectByRole();
      },
      error: (err) => {
        // Toujours remettre loading à false en cas d'erreur
        this.loading = false;

        if (err.status === 401 || err.status === 403) {
          this.errorMessage = 'Email ou mot de passe incorrect.';
        } else if (err.status === 0) {
          this.errorMessage = 'Impossible de contacter le serveur.';
        } else {
          this.errorMessage = 'Une erreur est survenue. Réessayez.';
        }

        this.cdr.detectChanges();
      }
    });
  }
}