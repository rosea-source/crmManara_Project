// src/app/features/parent/parent.routes.ts
import { Routes } from '@angular/router';

export const PARENT_ROUTES: Routes = [

  // ── Dashboard ────────────────────────────────────────────────────────────
  {
    path: 'dashboard',
    loadComponent: () =>
      import('./components/dashboard/dashboard.component').then(
        m => m.DashboardComponent
      )
  },

  // ── Enfants ──────────────────────────────────────────────────────────────
  {
    path: 'children/list',
    loadComponent: () =>
      import('./components/enfants/list/enfants-list.component').then(
        m => m.EnfantsListComponent
      )
  },
  {
    path: 'children/add',
    loadComponent: () =>
      import('./components/enfants/add/enfants-add.component').then(
        m => m.EnfantsAddComponent
      )
  },
  {
    path: 'children/edit/:id',
    loadComponent: () =>
      import('./components/enfants/edit/enfants-edit.component').then(
        m => m.EnfantsEditComponent
      )
  },
    {
  path: 'activities/list',
  loadComponent: () => import('./components/activities/list/activites-list.component')
    .then(m => m.ActivitesListComponent)
},
{
  path: 'activities/sessions/:activiteId',
  loadComponent: () => import('./components/activities/sessions/sessions.component')
    .then(m => m.SessionsComponent)
},
{
  path: 'activities/register/:sessionId',
  loadComponent: () => import('./components/activities/register/register-session.component')
    .then(m => m.RegisterSessionComponent)
},
  

  // ── Planning ──────────────────────────────────────────────────────────────
  {
    path: 'planning/list',
    loadComponent: () =>
      import('./components/planning/planning.component').then(
        m => m.PlanningComponent
      )
  },

  // ── Incidents ─────────────────────────────────────────────────────────────
  {
    path: 'incidents',
    loadComponent: () =>
      import('./components/incidents/incidents.component').then(
        m => m.IncidentsComponent
      )
  },

  // ── Messages enfant ───────────────────────────────────────────────────────
  {
    path: 'enfants/:enfantId/messages',
    loadComponent: () =>
      import('./components/messages/messages-enfant.component').then(
        m => m.MessagesEnfantComponent
      )
  },

  // ── Compte (composant partagé animateur/parent) ───────────────────────────
  {
    path: 'compte',
    loadComponent: () =>
      import('../../features/compte/compte.component').then(
        m => m.CompteComponent
      )
  },
  {
  path: 'contact',
  loadComponent: () => import('./components/contact/contact-admin.component')
    .then(m => m.ContactAdminParentComponent)
  },

  // ── Redirect par défaut ───────────────────────────────────────────────────
  {
    path: '',
    redirectTo: 'dashboard',
    pathMatch: 'full'
  }
];