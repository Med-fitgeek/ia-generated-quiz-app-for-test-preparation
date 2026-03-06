import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { filter, take, map } from 'rxjs/operators';

export const authGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);

  return authService.authInitialized$.pipe(
    filter(initialized => initialized === true),
    take(1),
    map(() => {
      if (authService.isLoggedIn()) {
        return true;
      } else {
        router.navigate(['/login']);
        return false;
      }
    })
  );
};