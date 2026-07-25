import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { TokenService } from '../services/token.service';
import { Role } from '../models/auth.model';

/**
 * Route Guard enforcing authentication and role-based access control.
 * 
 * WHY THIS GUARD WAS CREATED:
 * Protects platform feature routes (/user/home, /organizer/dashboard, /admin/dashboard) from
 * unauthorized access. Redirects unauthenticated users to /auth/login and unauthorized roles
 * to their respective home pages.
 */
export const authGuard: CanActivateFn = (route, state) => {
  const tokenService = inject(TokenService);
  const router = inject(Router);

  // Step 1: Verify if user has a valid access token
  if (!tokenService.isAuthenticated()) {
    router.navigate(['/auth/login'], { queryParams: { returnUrl: state.url } });
    return false;
  }

  // Step 2: Check expected role if specified in route data
  const expectedRoles = route.data['roles'] as Role[] | undefined;
  const userRole = tokenService.getRole();

  if (expectedRoles && expectedRoles.length > 0 && userRole) {
    if (!expectedRoles.includes(userRole)) {
      // Step 3: Redirect user to their role-appropriate home route if role doesn't match
      if (userRole === 'ADMIN') {
        router.navigate(['/admin/dashboard']);
      } else if (userRole === 'ORGANIZER') {
        router.navigate(['/organizer/dashboard']);
      } else {
        router.navigate(['/user/home']);
      }
      return false;
    }
  }

  return true;
};
