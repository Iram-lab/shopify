import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthActions, selectAuthLoading, selectAuthError } from '../../../store/auth/auth.store';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink,
            MatFormFieldModule, MatInputModule, MatButtonModule,
            MatIconModule, MatProgressSpinnerModule],
  template: `
    <div class="min-h-[80vh] flex items-center justify-center">
      <div class="w-full max-w-md">
        <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-xl p-8">

          <div class="text-center mb-8">
            <div class="inline-flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4">
              <mat-icon class="text-blue-600 text-3xl">lock</mat-icon>
            </div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Welcome back</h1>
            <p class="text-gray-500 dark:text-gray-400 mt-1">Sign in to your account</p>
          </div>

          @if (error$ | async; as error) {
            <div class="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm flex items-center gap-2">
              <mat-icon class="text-sm">error_outline</mat-icon> {{ error }}
            </div>
          }

          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4" novalidate>

            <!-- Email -->
            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Email</mat-label>
              <mat-icon matPrefix class="mr-2 text-gray-400">email</mat-icon>
              <input matInput type="email" formControlName="email"
                     placeholder="you@example.com"
                     (blur)="markTouched('email')" />
              @if (f['email'].touched && f['email'].hasError('required')) {
                <mat-error>Email is required</mat-error>
              } @else if (f['email'].touched && f['email'].hasError('email')) {
                <mat-error>Enter a valid email address</mat-error>
              }
            </mat-form-field>

            <!-- Password -->
            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Password</mat-label>
              <mat-icon matPrefix class="mr-2 text-gray-400">lock</mat-icon>
              <input matInput [type]="showPassword ? 'text' : 'password'"
                     formControlName="password"
                     (blur)="markTouched('password')" />
              <button mat-icon-button matSuffix type="button"
                      (click)="showPassword = !showPassword"
                      [attr.aria-label]="showPassword ? 'Hide password' : 'Show password'">
                <mat-icon>{{ showPassword ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              @if (f['password'].touched && f['password'].hasError('required')) {
                <mat-error>Password is required</mat-error>
              }
            </mat-form-field>

            <button mat-flat-button color="primary" type="submit"
                    [disabled]="form.invalid || (loading$ | async)"
                    class="w-full h-12 text-base font-semibold">
              @if (loading$ | async) {
                <mat-progress-spinner diameter="20" mode="indeterminate"
                  class="inline-block mr-2"></mat-progress-spinner>
                Signing in...
              } @else {
                Sign In
              }
            </button>
          </form>

          <p class="text-center mt-6 text-sm text-gray-600 dark:text-gray-400">
            Don't have an account?
            <a routerLink="/auth/register" class="text-blue-600 font-semibold hover:underline">Sign up</a>
          </p>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  private fb = inject(FormBuilder);
  private store = inject(Store);

  showPassword = false;
  loading$ = this.store.select(selectAuthLoading);
  error$   = this.store.select(selectAuthError);

  form = this.fb.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
  });

  get f() { return this.form.controls; }

  markTouched(field: string): void {
    this.form.get(field)?.markAsTouched();
  }

  onSubmit(): void {
    this.form.markAllAsTouched();
    if (this.form.valid) {
      this.store.dispatch(AuthActions.login({ request: this.form.value as any }));
    }
  }
}
