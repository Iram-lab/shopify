import { createAction, createReducer, createSelector, createFeatureSelector, on, props } from '@ngrx/store';
import { inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { Router } from '@angular/router';
import { catchError, exhaustMap, map, of, tap } from 'rxjs';
import { AuthService } from '../../core/services/auth.service';
import { ToastService } from '../../core/services/toast.service';
import { AuthResponse, LoginRequest, RegisterRequest, User } from '../../core/models/models';

// --- State ---
export interface AuthState {
  user: User | null;
  loading: boolean;
  error: string | null;
}

const initialState: AuthState = {
  user: null,
  loading: false,
  error: null,
};

// --- Actions ---
export const AuthActions = {
  login: createAction('[Auth] Login', props<{ request: LoginRequest }>()),
  loginSuccess: createAction('[Auth] Login Success', props<{ response: AuthResponse }>()),
  loginFailure: createAction('[Auth] Login Failure', props<{ error: string }>()),

  register: createAction('[Auth] Register', props<{ request: RegisterRequest }>()),
  registerSuccess: createAction('[Auth] Register Success', props<{ response: AuthResponse }>()),
  registerFailure: createAction('[Auth] Register Failure', props<{ error: string }>()),

  logout: createAction('[Auth] Logout'),
};

// --- Reducer ---
export const authReducer = createReducer(
  initialState,
  on(AuthActions.login, AuthActions.register, state => ({ ...state, loading: true, error: null })),
  on(AuthActions.loginSuccess, AuthActions.registerSuccess, (state, { response }) => ({
    ...state, loading: false, user: { email: response.email, role: response.role }
  })),
  on(AuthActions.loginFailure, AuthActions.registerFailure, (state, { error }) => ({
    ...state, loading: false, error
  })),
  on(AuthActions.logout, () => initialState),
);

// --- Selectors ---
export const selectAuthState = createFeatureSelector<AuthState>('auth');
export const selectCurrentUser = createSelector(selectAuthState, s => s.user);
export const selectAuthLoading = createSelector(selectAuthState, s => s.loading);
export const selectAuthError = createSelector(selectAuthState, s => s.error);

// --- Effects ---
export class AuthEffects {
  private actions$ = inject(Actions);
  private authService = inject(AuthService);
  private toastService = inject(ToastService);
  private router = inject(Router);

  login$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.login),
      exhaustMap(({ request }) =>
        this.authService.login(request).pipe(
          map(response => AuthActions.loginSuccess({ response })),
          catchError(err => of(AuthActions.loginFailure({
            error: err.error?.detail || 'Login failed'
          })))
        )
      )
    )
  );

  loginSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.loginSuccess),
      tap(({ response }) => {
        this.toastService.success(`Welcome back, ${response.email}!`);
        this.router.navigate(['/']);
      })
    ), { dispatch: false }
  );

  register$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.register),
      exhaustMap(({ request }) =>
        this.authService.register(request).pipe(
          map(response => AuthActions.registerSuccess({ response })),
          catchError(err => of(AuthActions.registerFailure({
            error: err.error?.detail || 'Registration failed'
          })))
        )
      )
    )
  );

  registerSuccess$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.registerSuccess),
      tap(() => {
        this.toastService.success('Account created successfully!');
        this.router.navigate(['/']);
      })
    ), { dispatch: false }
  );

  logout$ = createEffect(() =>
    this.actions$.pipe(
      ofType(AuthActions.logout),
      tap(() => {
        this.authService.logout();
        this.toastService.info('Logged out successfully.');
      })
    ), { dispatch: false }
  );
}
