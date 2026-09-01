import { Component, inject, signal, computed } from '@angular/core';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { Store } from '@ngrx/store';
import { MatBadgeModule } from '@angular/material/badge';
import { MatIconModule } from '@angular/material/icon';
import { MatButtonModule } from '@angular/material/button';
import { MatMenuModule } from '@angular/material/menu';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../../core/services/auth.service';
import { AuthActions } from '../../../store/auth/auth.store';
import { selectCartItemCount } from '../../../store/cart/cart.store';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink,
            MatBadgeModule, MatIconModule, MatButtonModule, MatMenuModule, MatDividerModule],
  template: `
    <nav class="sticky top-0 z-50 bg-white dark:bg-gray-900 shadow-md border-b border-gray-200 dark:border-gray-700">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between h-16">

          <!-- Logo -->
          <a routerLink="/" class="flex items-center gap-2 text-xl font-bold text-primary-600">
            <mat-icon>shopping_bag</mat-icon>
            <span>ShopMicro</span>
          </a>

          <!-- Search bar -->
          <div class="hidden md:flex flex-1 max-w-lg mx-8">
            <div class="relative w-full">
              <mat-icon class="absolute left-3 top-2.5 text-gray-400 text-lg">search</mat-icon>
              <input
                type="text"
                placeholder="Search products..."
                (keyup.enter)="onSearch($event)"
                class="w-full pl-10 pr-4 py-2 rounded-full border border-gray-300 dark:border-gray-600
                       bg-gray-50 dark:bg-gray-800 text-sm focus:outline-none focus:ring-2
                       focus:ring-primary-500 dark:text-white"
              />
            </div>
          </div>

          <!-- Right actions -->
          <div class="flex items-center gap-2">

            <!-- Dark mode toggle -->
            <button mat-icon-button (click)="toggleDarkMode()" class="text-gray-600 dark:text-gray-300">
              <mat-icon>{{ isDark() ? 'light_mode' : 'dark_mode' }}</mat-icon>
            </button>

            <!-- Cart -->
            <a routerLink="/cart" mat-icon-button class="relative text-gray-600 dark:text-gray-300">
              <mat-icon [matBadge]="cartCount() || null" matBadgeColor="warn" matBadgeSize="small">
                shopping_cart
              </mat-icon>
            </a>

            <!-- Auth menu -->
            @if (authService.isLoggedIn()) {
              <button mat-button [matMenuTriggerFor]="userMenu"
                      class="text-gray-700 dark:text-gray-200 flex items-center gap-1">
                <mat-icon>account_circle</mat-icon>
                <span class="hidden sm:inline text-sm">{{ authService.currentUser()?.email }}</span>
                <mat-icon class="text-sm">arrow_drop_down</mat-icon>
              </button>
              <mat-menu #userMenu="matMenu">
                <a mat-menu-item routerLink="/orders">
                  <mat-icon>receipt_long</mat-icon> My Orders
                </a>
                <a mat-menu-item routerLink="/profile">
                  <mat-icon>person</mat-icon> Profile
                </a>
                @if (authService.isAdmin()) {
                  <a mat-menu-item routerLink="/admin">
                    <mat-icon>admin_panel_settings</mat-icon> Admin
                  </a>
                }
                <mat-divider></mat-divider>
                <button mat-menu-item (click)="logout()">
                  <mat-icon>logout</mat-icon> Logout
                </button>
              </mat-menu>
            } @else {
              <a routerLink="/auth/login" mat-stroked-button color="primary" class="text-sm">
                Login
              </a>
              <a routerLink="/auth/register" mat-flat-button color="primary" class="text-sm hidden sm:inline-flex">
                Sign Up
              </a>
            }
          </div>
        </div>
      </div>
    </nav>
  `
})
export class NavbarComponent {
  authService = inject(AuthService);
  private store = inject(Store);

  cartCount = this.store.selectSignal(selectCartItemCount);
  isDark = signal(false);

  toggleDarkMode(): void {
    this.isDark.update(v => !v);
    document.documentElement.classList.toggle('dark');
  }

  logout(): void {
    this.store.dispatch(AuthActions.logout());
  }

  onSearch(event: Event): void {
    const value = (event.target as HTMLInputElement).value;
    if (value.trim()) {
      window.location.href = `/?keyword=${encodeURIComponent(value)}`;
    }
  }
}
