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
  { path: 'browse', redirectTo: 'user/home' },
  { path: '**', redirectTo: 'auth/login' }
];
