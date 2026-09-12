// src/app/features/animateur/animateur.routes.ts
import { Routes } from '@angular/router';

export const animateurRoutes: Routes = [

  // DASHBOARD
  {
    path: 'dashboard',
    loadComponent: () => import('./components/dashboard/dashboard-animateur.component')
      .then(m => m.AnimateurDashboardComponent)
  },

  // ACTIVITÉS
  {
    path: 'activites',
    loadComponent: () => import('./components/activityAnimateur/activite-animateur.component')
      .then(m => m.ActiviteAnimateurComponent)
  },

  // PRÉSENCES
  {
    path: 'presences',
    children: [
      {
        path: '',
        loadComponent: () => import('./components/presences/presences.component')
          .then(m => m.PresencesComponent)
      },
      {
        path: 'historique',
        loadComponent: () => import('./components/presences/historique/presences-historique.component')
          .then(m => m.PresencesHistoriqueComponent)
      },
      {
        path: ':sessionId/presence',
        loadComponent: () => import('./components/presences/presence-session/presence-session.component')
          .then(m => m.PresenceSessionComponent)
      },
      {
        path: ':sessionId/zone-enfant',
        loadComponent: () => import('./components/presences/zone-enfant/zone-enfant.component')
          .then(m => m.ZoneEnfantComponent)
      }
    ]
  },

  // INCIDENTS
  {
    path: 'incidents',
    children: [
      { path: '', redirectTo: 'history', pathMatch: 'full' },
      {
        path: 'history',
        loadComponent: () => import('./components/incidents/history/incident-history.component')
          .then(m => m.IncidentHistoryComponent)
      },
      {
        path: 'report',
        loadComponent: () => import('./components/incidents/report/incident-report.component')
          .then(m => m.IncidentReportComponent)
      }
    ]
  },

  // COMPTE (partagé parent + animateur)
  {
    path: 'compte',
    loadComponent: () => import('../../features/compte/compte.component')
      .then(m => m.CompteComponent)
  },
  {
  path: 'contact',
    loadComponent: () => import('./components/contact/contact-admin.component')
      .then(m => m.ContactAdminAnimateurComponent)
  },

  // REDIRECT
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
];