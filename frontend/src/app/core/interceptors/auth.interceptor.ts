import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, switchMap, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {

  const authService = inject(AuthService);
  const token = authService.getAccessToken();

  // Toujours envoyer les cookies
  let authReq = req.clone({ withCredentials: true });

  if (token) {
    authReq = authReq.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
  }

  return next(authReq).pipe(
    catchError(err => {

      // Ne pas tenter refresh si c’est déjà un refresh
      if (err.status === 401 && !req.url.includes('/auth/refresh')) {

        return authService.refreshAccessToken().pipe(
          switchMap(() => {

            const newToken = authService.getAccessToken();

            if (!newToken) {
              authService.logout();
              return throwError(() => err);
            }

            const retryReq = req.clone({
              withCredentials: true,
              setHeaders: {
                Authorization: `Bearer ${newToken}`
              }
            });

            return next(retryReq);
          }),
          catchError(refreshError => {
            authService.logout();
            return throwError(() => refreshError);
          })
        );
      }

      return throwError(() => err);
    })
  );
};