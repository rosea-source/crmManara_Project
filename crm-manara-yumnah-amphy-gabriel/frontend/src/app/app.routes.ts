import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  // PUBLIC
  {
    path: 'login',
    loadComponent: () =>
      import('./features/auth/login/login.component').then(m => m.LoginComponent)
  },
  
  {
    path: 'register',
    loadComponent: () =>
      import('./features/auth/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'access-denied',
    loadComponent: () =>
      import('./features/auth/access-denied/access-denied.component').then(m => m.AccessDeniedComponent)
  },

  // ADMIN
  // app.routes.ts — remplace les deux guards par un seul
{
  path: 'admin',
  canActivate: [roleGuard('ROLE_ADMIN')],  // ← roleGuard vérifie déjà l'auth
  loadChildren: () =>
    import('./features/admin/admin.routes').then(m => m.adminRoutes)
},
{
  path: 'animateur',
  canActivate: [roleGuard('ROLE_ANIMATEUR')],
  loadChildren: () =>
    import('./features/animateur/animateur.routes').then(m => m.animateurRoutes)
},
{
  path: 'parent',
  canActivate: [roleGuard('ROLE_PARENT')],
  loadChildren: () =>
    import('./features/parent/parent.routes').then(m => m.PARENT_ROUTES)
},

  // PUBLIC PAGES
  {
    path: 'accueil',
    loadComponent: () =>
      import('./features/accueil/accueil.component').then(m => m.AccueilComponent)
  },
  {
    path: 'evenements',
    loadComponent: () =>
      import('./features/evenements/evenements.component').then(m => m.EvenementsComponent)
  },
  {
    path: 'a-propos',
    loadComponent: () =>
      import('./features/about/about.component').then(m => m.AboutComponent)
  },
 
  {
    path: 'contact',
    loadComponent: () =>
      import('./features/contact/contact.component').then(m => m.ContactComponent)
  },

  // DEFAULT
  { path: '', redirectTo: 'accueil', pathMatch: 'full' },

  // FALLBACK (important)
  { path: '**', redirectTo: 'accueil' }
];