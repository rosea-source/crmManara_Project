import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, BehaviorSubject } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { of } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = 'http://localhost:8080';

  private currentUser$ = new BehaviorSubject<any>(null);

  login(email: string, password: string): Observable<any> {
    const body = new URLSearchParams();
    body.set('email', email);
    body.set('password', password);

    return this.http.post(`${this.apiUrl}/login`, body.toString(), {
      headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
      withCredentials: true
    }).pipe(
      tap(() => this.loadUser())
    );
  }

  register(user: any): Observable<any> {
  return this.http.post(`${this.apiUrl}/api/auth/register`, user, {
    withCredentials: true
  });
}

  logout(): void {
  this.http.post(`${this.apiUrl}/logout`, {}, {
    withCredentials: true
  }).subscribe({
    next: () => {
      this.currentUser$.next(null);
      //  Ajouter le queryParam que login.component attend
      this.router.navigate(['/login'], { queryParams: { logout: true } });
    },
    error: () => {
      // Même en cas d'erreur, on déconnecte localement
      this.currentUser$.next(null);
      this.router.navigate(['/login'], { queryParams: { logout: true } });
    }
  });
}

  getMe(): Observable<any> {
    return this.http.get(`${this.apiUrl}/api/auth/me`, {
      withCredentials: true
    });
  }

  loadUser(): void {
    this.getMe().subscribe({
      next: (user) => this.currentUser$.next(user),
      error: () => this.currentUser$.next(null)
    });
  }

  // ← Fix : vérifie via /me si currentUser est null
  isLoggedIn(): boolean {
    return this.currentUser$.value !== null;
  }

  getPrenom(): string {
    return this.currentUser$.value?.prenom || '';
  }

  getRole(): string {
    return this.currentUser$.value?.role || '';
  }

  getCurrentUser(): any {
    return this.currentUser$.value;
  }

  redirectByRole(): void {
    this.getMe().subscribe({
      next: (user) => {
        this.currentUser$.next(user);
        if (user.role === 'ROLE_ADMIN') {
          this.router.navigate(['/admin/dashboard']);
        } else if (user.role === 'ROLE_ANIMATEUR') {
          this.router.navigate(['/animateur/dashboard']);
        } else if (user.role === 'ROLE_PARENT') {
          this.router.navigate(['/parent/dashboard']);
        } else {
          this.router.navigate(['/login']);
        }
      },
      error: () => this.router.navigate(['/login'])
    });
  }
  
}