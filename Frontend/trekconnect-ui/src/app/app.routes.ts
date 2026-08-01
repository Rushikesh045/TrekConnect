import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'auth/login', pathMatch: 'full' },
  {
    path: 'auth/login',
    loadComponent: () => import('./features/auth/pages/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'auth/register',
    loadComponent: () => import('./features/auth/pages/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'user/home',
    loadComponent: () => import('./features/user/pages/home/user-home.component').then(m => m.UserHomeComponent),
    canActivate: [authGuard],
    data: { roles: ['USER'] }
  },
  {
    path: 'user/profile',
    loadComponent: () => import('./features/user/pages/profile/profile.component').then(m => m.ProfileComponent),
    canActivate: [authGuard]
  },
  {
    path: 'organizer/apply',
    loadComponent: () => import('./features/user/pages/organizer-apply/organizer-apply.component').then(m => m.OrganizerApplyComponent),
    canActivate: [authGuard]
  },
  {
    path: 'organizer/dashboard',
    loadComponent: () => import('./features/organizer/pages/dashboard/organizer-dashboard.component').then(m => m.OrganizerDashboardComponent),
    canActivate: [authGuard],
    data: { roles: ['ORGANIZER', 'ADMIN'] }
  },
  {
    path: 'admin/dashboard',
    loadComponent: () => import('./features/admin/pages/dashboard/admin-dashboard.component').then(m => m.AdminDashboardComponent),
    canActivate: [authGuard],
    data: { roles: ['ADMIN'] }
  },
  { path: 'organizer', redirectTo: 'organizer/dashboard' },
  { path: 'admin', redirectTo: 'admin/dashboard' },
  { path: 'browse', redirectTo: 'user/home' },
  { path: '**', redirectTo: 'auth/login' }
];
