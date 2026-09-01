import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, RouterLink, MatButtonModule, MatIconModule, MatDividerModule],
  template: `
    <div class="max-w-2xl mx-auto">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white mb-6 flex items-center gap-2">
        <mat-icon>person</mat-icon> My Profile
      </h1>

      <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-700 p-8">

        <!-- Avatar -->
        <div class="flex items-center gap-6 mb-8">
          <div class="w-20 h-20 bg-blue-100 dark:bg-blue-900 rounded-full flex items-center justify-center">
            <mat-icon class="text-4xl text-blue-600">account_circle</mat-icon>
          </div>
          <div>
            <h2 class="text-xl font-bold text-gray-900 dark:text-white">
              {{ authService.currentUser()?.email }}
            </h2>
            <span [class]="authService.isAdmin()
              ? 'bg-purple-100 text-purple-700'
              : 'bg-blue-100 text-blue-700'"
                  class="text-xs font-semibold px-3 py-1 rounded-full">
              {{ authService.currentUser()?.role }}
            </span>
          </div>
        </div>

        <mat-divider class="mb-6" />

        <!-- Quick Links -->
        <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
          <a routerLink="/orders"
             class="flex items-center gap-3 p-4 bg-gray-50 dark:bg-gray-700 rounded-xl hover:bg-blue-50 dark:hover:bg-blue-900/20 transition-colors">
            <mat-icon class="text-blue-600">receipt_long</mat-icon>
            <div>
              <p class="font-semibold text-gray-900 dark:text-white text-sm">My Orders</p>
              <p class="text-xs text-gray-500">View order history</p>
            </div>
          </a>
          <a routerLink="/cart"
             class="flex items-center gap-3 p-4 bg-gray-50 dark:bg-gray-700 rounded-xl hover:bg-blue-50 dark:hover:bg-blue-900/20 transition-colors">
            <mat-icon class="text-blue-600">shopping_cart</mat-icon>
            <div>
              <p class="font-semibold text-gray-900 dark:text-white text-sm">My Cart</p>
              <p class="text-xs text-gray-500">View current cart</p>
            </div>
          </a>
          @if (authService.isAdmin()) {
            <a routerLink="/admin"
               class="flex items-center gap-3 p-4 bg-purple-50 dark:bg-purple-900/20 rounded-xl hover:bg-purple-100 transition-colors">
              <mat-icon class="text-purple-600">admin_panel_settings</mat-icon>
              <div>
                <p class="font-semibold text-gray-900 dark:text-white text-sm">Admin Dashboard</p>
                <p class="text-xs text-gray-500">Manage the store</p>
              </div>
            </a>
          }
        </div>

        <mat-divider class="my-6" />

        <button mat-stroked-button color="warn" (click)="authService.logout()" class="w-full h-11">
          <mat-icon class="mr-2">logout</mat-icon> Sign Out
        </button>
      </div>
    </div>
  `
})
export class ProfileComponent {
  authService = inject(AuthService);
}
