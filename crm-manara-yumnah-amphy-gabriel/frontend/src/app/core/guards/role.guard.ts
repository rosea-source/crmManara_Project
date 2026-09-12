import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { catchError, map, of } from 'rxjs';

export const roleGuard = (requiredRole: string): CanActivateFn => () => {
  const http = inject(HttpClient);
  const router = inject(Router);

  return http.get<any>('http://localhost:8080/api/auth/me', { 
    withCredentials: true 
  }).pipe(
    map((user) => {
      if (user.role === requiredRole) return true;
      router.navigate(['/access-denied']);
      return false;
    }),
    catchError(() => {
      router.navigate(['/login']);
      return of(false);
    })
  );
};