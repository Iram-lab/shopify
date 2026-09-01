import { HttpInterceptorFn, HttpRequest, HttpHandlerFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';
import { AuthService } from '../services/auth.service';
import { ToastService } from '../services/toast.service';

export const jwtInterceptor: HttpInterceptorFn = (
  req: HttpRequest<unknown>,
  next: HttpHandlerFn
) => {
  const authService = inject(AuthService);
  const toastService = inject(ToastService);

  const token = authService.getToken();
  const authReq = token
    ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
    : req;

  return next(authReq).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401) {
        const refreshToken = authService.getRefreshToken();

        // Try refresh token if available
        if (refreshToken) {
          return authService.refreshToken().pipe(
            switchMap(res => {
              const retryReq = req.clone({
                setHeaders: { Authorization: `Bearer ${res.accessToken}` }
              });
              return next(retryReq);
            }),
            catchError(() => {
              // Refresh also failed — session truly expired
              toastService.error('Session expired. Please log in again.');
              authService.logout();
              return throwError(() => error);
            })
          );
        }

        // No refresh token — not logged in or session expired
        toastService.error('Please log in to continue.');
        authService.logout();
        return throwError(() => error);
      }

      if (error.status === 403) {
        toastService.error('Access denied.');
      } else if (error.status === 0) {
        toastService.error('Cannot connect to server. Make sure all services are running.');
      }

      return throwError(() => error);
    })
  );
};
