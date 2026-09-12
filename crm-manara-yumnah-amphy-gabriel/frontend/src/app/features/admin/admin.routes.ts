import { Routes } from '@angular/router';

export const adminRoutes: Routes = [
  {
    path: 'dashboard',
    loadComponent: () => import('./components/dashboard/dashboard.component')
      .then(m => m.DashboardComponent)
  },
  // SESSIONS
  {
    path: 'sessions',
    children: [
      { path: '', loadComponent: () => import('./components/sessions/sessions.component').then(m => m.SessionsComponent) },
      { path: 'create', loadComponent: () => import('./components/sessions/session-create/session-create.component').then(m => m.SessionCreateComponent) },
      { path: ':id', loadComponent: () => import('./components/sessions/session-detail/session-detail.component').then(m => m.SessionDetailComponent) },
      { path: ':id/edit', loadComponent: () => import('./components/sessions/session-edit/session-edit.component').then(m => m.SessionEditComponent) },
      {path: ':id/attentes',loadComponent: () => import('./components/enfants/attentes/attentes.component').then(m => m.AttentesComponent)

      },
      {
        path: ':id/inscrits',
        loadComponent: () => import('./components/enfants/inscrits/inscrits.component')
          .then(m => m.InscritsComponent)
      }
    ]
  },
  // ACTIVITÉS
  {
    path: 'activites',
    children: [
      { path: '', loadComponent: () => import('./components/activites/listActivity/list-activity.component').then(m => m.ListActivityComponent) },
      { path: 'create', loadComponent: () => import('./components/activites/createActivity/create-activity.component').then(m => m.CreateActivityComponent) },
      { path: 'edit/:id', loadComponent: () => import('./components/activites/editActivity/edit-activity.component').then(m => m.EditActivityComponent) }
    ]
  },
  // USERS
  {
    path: 'users',
    children: [
      { path: '', loadComponent: () => import('./components/users/listUser/list-user.component').then(m => m.ListUserComponent) },
      { path: 'create', loadComponent: () => import('./components/users/createUser/create-user.component').then(m => m.CreateUserComponent) },
      { path: 'edit/:id', loadComponent: () => import('./components/users/editUser/edit-user.component').then(m => m.EditUserComponent) },
      { path: ':id', loadComponent: () => import('./components/users/detailUser/user-detail.component').then(m => m.UserDetailComponent) }
    ]
  },
  // ENFANTS ← au niveau principal, pas dans users !
  {
    path: 'enfants/:id',
    loadComponent: () => import('./components/enfants/details/enfant-detail.component')
      .then(m => m.EnfantDetailComponent)
  },
  // ÉVÉNEMENTS
  { path: 'evenements', loadComponent: () => import('./components/events/events.component').then(m => m.EventsComponent) },
  // INCIDENTS
  { path: 'incidents', loadComponent: () => import('./components/incident/admin-incidents.component').then(m => m.AdminIncidentsComponent) },
  // PLANNING
  { path: 'planning', loadComponent: () => import('./components/planning/planning.component').then(m => m.PlanningComponent) },
  // PARAMÈTRES
  { path: 'parametres', loadComponent: () => import('./components/parametres/parametres.component').then(m => m.ParametresComponent) },
  // REDIRECT
  { path: '', redirectTo: 'dashboard', pathMatch: 'full' }
];