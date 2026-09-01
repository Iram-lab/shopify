import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthActions, selectAuthLoading, selectAuthError } from '../../../store/auth/auth.store';

// Custom validator: at least one uppercase, one number, one special char
function passwordStrength(control: AbstractControl): ValidationErrors | null {
  const value = control.value || '';
  if (!value) return null;
  const hasUpper   = /[A-Z]/.test(value);
  const hasNumber  = /[0-9]/.test(value);
  const hasSpecial = /[!@#$%^&*(),.?":{}|<>]/.test(value);
  if (!hasUpper)   return { noUppercase: true };
  if (!hasNumber)  return { noNumber: true };
  if (!hasSpecial) return { noSpecial: true };
  return null;
}

// Cross-field validator: confirmPassword must match password
function passwordMatch(group: AbstractControl): ValidationErrors | null {
  const password = group.get('password')?.value;
  const confirm  = group.get('confirmPassword')?.value;
  return password && confirm && password !== confirm ? { passwordMismatch: true } : null;
}

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink,
            MatFormFieldModule, MatInputModule, MatButtonModule,
            MatIconModule, MatProgressSpinnerModule],
  template: `
    <div class="min-h-[80vh] flex items-center justify-center py-8">
      <div class="w-full max-w-md">
        <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-xl p-8">

          <div class="text-center mb-8">
            <div class="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4">
              <mat-icon class="text-green-600 text-3xl">person_add</mat-icon>
            </div>
            <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Create account</h1>
            <p class="text-gray-500 dark:text-gray-400 mt-1">Join us today</p>
          </div>

          @if (error$ | async; as error) {
            <div class="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm flex items-center gap-2">
              <mat-icon class="text-sm">error_outline</mat-icon> {{ error }}
            </div>
          }

          <form [formGroup]="form" (ngSubmit)="onSubmit()" class="space-y-4" novalidate>

            <!-- First & Last Name -->
            <div class="grid grid-cols-2 gap-4">
              <mat-form-field appearance="outline">
                <mat-label>First Name</mat-label>
                <input matInput formControlName="firstName"
                       placeholder="John"
                       (blur)="markTouched('firstName')" />
                @if (f['firstName'].touched && f['firstName'].hasError('required')) {
                  <mat-error>First name is required</mat-error>
                } @else if (f['firstName'].touched && f['firstName'].hasError('minlength')) {
                  <mat-error>Min 2 characters</mat-error>
                } @else if (f['firstName'].touched && f['firstName'].hasError('pattern')) {
                  <mat-error>Letters only</mat-error>
                }
              </mat-form-field>

              <mat-form-field appearance="outline">
                <mat-label>Last Name</mat-label>
                <input matInput formControlName="lastName"
                       placeholder="Doe"
                       (blur)="markTouched('lastName')" />
                @if (f['lastName'].touched && f['lastName'].hasError('required')) {
                  <mat-error>Last name is required</mat-error>
                } @else if (f['lastName'].touched && f['lastName'].hasError('minlength')) {
                  <mat-error>Min 2 characters</mat-error>
                } @else if (f['lastName'].touched && f['lastName'].hasError('pattern')) {
                  <mat-error>Letters only</mat-error>
                }
              </mat-form-field>
            </div>

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
                      (click)="showPassword = !showPassword">
                <mat-icon>{{ showPassword ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              @if (f['password'].touched && f['password'].hasError('required')) {
                <mat-error>Password is required</mat-error>
              } @else if (f['password'].touched && f['password'].hasError('minlength')) {
                <mat-error>Minimum 8 characters</mat-error>
              } @else if (f['password'].touched && f['password'].hasError('noUppercase')) {
                <mat-error>Must contain at least one uppercase letter</mat-error>
              } @else if (f['password'].touched && f['password'].hasError('noNumber')) {
                <mat-error>Must contain at least one number</mat-error>
              } @else if (f['password'].touched && f['password'].hasError('noSpecial')) {
                <mat-error>Must contain at least one special character</mat-error>
              }
            </mat-form-field>

            <!-- Password strength indicator -->
            @if (f['password'].value) {
              <div class="space-y-1 -mt-2">
                <div class="flex gap-1">
                  @for (i of [1,2,3,4]; track i) {
                    <div class="h-1 flex-1 rounded-full transition-colors duration-300"
                         [class]="getStrengthColor(i)"></div>
                  }
                </div>
                <p class="text-xs" [class]="getStrengthTextColor()">
                  {{ getStrengthLabel() }}
                </p>
              </div>
            }

            <!-- Confirm Password -->
            <mat-form-field appearance="outline" class="w-full">
              <mat-label>Confirm Password</mat-label>
              <mat-icon matPrefix class="mr-2 text-gray-400">lock_reset</mat-icon>
              <input matInput [type]="showConfirm ? 'text' : 'password'"
                     formControlName="confirmPassword"
                     (blur)="markTouched('confirmPassword')" />
              <button mat-icon-button matSuffix type="button"
                      (click)="showConfirm = !showConfirm">
                <mat-icon>{{ showConfirm ? 'visibility_off' : 'visibility' }}</mat-icon>
              </button>
              @if (f['confirmPassword'].touched && f['confirmPassword'].hasError('required')) {
                <mat-error>Please confirm your password</mat-error>
              } @else if (f['confirmPassword'].touched && form.hasError('passwordMismatch')) {
                <mat-error>Passwords do not match</mat-error>
              }
            </mat-form-field>

            <button mat-flat-button color="primary" type="submit"
                    [disabled]="form.invalid || (loading$ | async)"
                    class="w-full h-12 text-base font-semibold">
              @if (loading$ | async) {
                <mat-progress-spinner diameter="20" mode="indeterminate"
                  class="inline-block mr-2"></mat-progress-spinner>
                Creating account...
              } @else {
                Create Account
              }
            </button>
          </form>

          <p class="text-center mt-6 text-sm text-gray-600 dark:text-gray-400">
            Already have an account?
            <a routerLink="/auth/login" class="text-blue-600 font-semibold hover:underline">Sign in</a>
          </p>
        </div>
      </div>
    </div>
  `
})
export class RegisterComponent {
  private fb = inject(FormBuilder);
  private store = inject(Store);

  showPassword = false;
  showConfirm  = false;
  loading$ = this.store.select(selectAuthLoading);
  error$   = this.store.select(selectAuthError);

  form = this.fb.group({
    firstName:       ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-Z]+$/)]],
    lastName:        ['', [Validators.required, Validators.minLength(2), Validators.pattern(/^[a-zA-Z]+$/)]],
    email:           ['', [Validators.required, Validators.email]],
    password:        ['', [Validators.required, Validators.minLength(8), passwordStrength]],
    confirmPassword: ['', [Validators.required]],
  }, { validators: passwordMatch });

  get f() { return this.form.controls; }

  markTouched(field: string): void {
    this.form.get(field)?.markAsTouched();
  }

  getStrengthScore(): number {
    const pwd = this.f['password'].value || '';
    let score = 0;
    if (pwd.length >= 8)                          score++;
    if (/[A-Z]/.test(pwd))                        score++;
    if (/[0-9]/.test(pwd))                        score++;
    if (/[!@#$%^&*(),.?":{}|<>]/.test(pwd))      score++;
    return score;
  }

  getStrengthColor(index: number): string {
    const score = this.getStrengthScore();
    if (score === 0) return 'bg-gray-200';
    const colors = ['bg-red-400', 'bg-orange-400', 'bg-yellow-400', 'bg-green-500'];
    return index <= score ? colors[score - 1] : 'bg-gray-200';
  }

  getStrengthLabel(): string {
    const labels = ['', 'Weak', 'Fair', 'Good', 'Strong'];
    return labels[this.getStrengthScore()];
  }

  getStrengthTextColor(): string {
    const colors = ['', 'text-red-500', 'text-orange-500', 'text-yellow-600', 'text-green-600'];
    return colors[this.getStrengthScore()];
  }

  onSubmit(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) return;
    const { firstName, lastName, email, password } = this.form.value;
    this.store.dispatch(AuthActions.register({
      request: { firstName: firstName!, lastName: lastName!, email: email!, password: password! }
    }));
  }
}
