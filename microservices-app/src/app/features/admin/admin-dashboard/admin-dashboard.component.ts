import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatTableModule } from '@angular/material/table';
import { MatChipsModule } from '@angular/material/chips';
import { MatTabsModule } from '@angular/material/tabs';
import { OrderService } from '../../../core/services/order.service';
import { ToastService } from '../../../core/services/toast.service';
import { Order } from '../../../core/models/models';

@Component({
  selector: 'app-admin-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, MatButtonModule, MatIconModule,
            MatTableModule, MatChipsModule, MatTabsModule],
  template: `
    <div class="space-y-6">

      <!-- Header -->
      <div class="flex items-center justify-between">
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white flex items-center gap-2">
          <mat-icon>admin_panel_settings</mat-icon> Admin Dashboard
        </h1>
      </div>

      <!-- Stats Cards -->
      <div class="grid grid-cols-2 lg:grid-cols-4 gap-4">
        @for (stat of stats; track stat.label) {
          <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 p-5">
            <div class="flex items-center justify-between mb-3">
              <span class="text-sm text-gray-500 dark:text-gray-400">{{ stat.label }}</span>
              <div [class]="stat.iconBg" class="w-10 h-10 rounded-lg flex items-center justify-center">
                <mat-icon [class]="stat.iconColor" class="text-lg">{{ stat.icon }}</mat-icon>
              </div>
            </div>
            <p class="text-2xl font-bold text-gray-900 dark:text-white">{{ stat.value }}</p>
          </div>
        }
      </div>

      <!-- Tabs -->
      <mat-tab-group>
        <mat-tab label="Recent Orders">
          <div class="mt-4 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 overflow-hidden">
            @if (orders().length === 0) {
              <div class="text-center py-12 text-gray-500">No orders found</div>
            } @else {
              <table mat-table [dataSource]="orders()" class="w-full">
                <ng-container matColumnDef="orderNumber">
                  <th mat-header-cell *matHeaderCellDef class="font-semibold">Order #</th>
                  <td mat-cell *matCellDef="let o" class="font-mono text-sm">{{ o.orderNumber }}</td>
                </ng-container>
                <ng-container matColumnDef="user">
                  <th mat-header-cell *matHeaderCellDef class="font-semibold">Customer</th>
                  <td mat-cell *matCellDef="let o" class="text-sm">{{ o.userEmail }}</td>
                </ng-container>
                <ng-container matColumnDef="amount">
                  <th mat-header-cell *matHeaderCellDef class="font-semibold">Amount</th>
                  <td mat-cell *matCellDef="let o" class="font-bold text-blue-600">
                    \${{ o.totalAmount | number:'1.2-2' }}
                  </td>
                </ng-container>
                <ng-container matColumnDef="status">
                  <th mat-header-cell *matHeaderCellDef class="font-semibold">Status</th>
                  <td mat-cell *matCellDef="let o">
                    <span [class]="getStatusClass(o.status)"
                          class="px-2 py-1 rounded-full text-xs font-semibold">
                      {{ o.status }}
                    </span>
                  </td>
                </ng-container>
                <ng-container matColumnDef="date">
                  <th mat-header-cell *matHeaderCellDef class="font-semibold">Date</th>
                  <td mat-cell *matCellDef="let o" class="text-sm text-gray-500">
                    {{ o.createdAt | date:'shortDate' }}
                  </td>
                </ng-container>
                <ng-container matColumnDef="actions">
                  <th mat-header-cell *matHeaderCellDef></th>
                  <td mat-cell *matCellDef="let o">
                    <a [routerLink]="['/orders', o.orderNumber]" mat-icon-button>
                      <mat-icon class="text-sm">open_in_new</mat-icon>
                    </a>
                  </td>
                </ng-container>
                <tr mat-header-row *matHeaderRowDef="displayedColumns" class="bg-gray-50 dark:bg-gray-700"></tr>
                <tr mat-row *matRowDef="let row; columns: displayedColumns;"
                    class="hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"></tr>
              </table>
            }
          </div>
        </mat-tab>

        <mat-tab label="Quick Links">
          <div class="mt-4 grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
            @for (link of adminLinks; track link.label) {
              <a [routerLink]="link.route"
                 class="flex items-center gap-3 p-5 bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 hover:border-blue-300 transition-colors">
                <div class="w-10 h-10 bg-blue-100 dark:bg-blue-900/30 rounded-lg flex items-center justify-center">
                  <mat-icon class="text-blue-600">{{ link.icon }}</mat-icon>
                </div>
                <div>
                  <p class="font-semibold text-gray-900 dark:text-white text-sm">{{ link.label }}</p>
                  <p class="text-xs text-gray-500">{{ link.desc }}</p>
                </div>
              </a>
            }
          </div>
        </mat-tab>
      </mat-tab-group>
    </div>
  `
})
export class AdminDashboardComponent implements OnInit {
  private orderService = inject(OrderService);
  private toastService = inject(ToastService);

  orders = signal<Order[]>([]);
  displayedColumns = ['orderNumber', 'user', 'amount', 'status', 'date', 'actions'];

  stats = [
    { label: 'Total Orders', value: '—', icon: 'receipt_long', iconBg: 'bg-blue-100', iconColor: 'text-blue-600' },
    { label: 'Products', value: '—', icon: 'inventory_2', iconBg: 'bg-green-100', iconColor: 'text-green-600' },
    { label: 'Revenue', value: '—', icon: 'attach_money', iconBg: 'bg-yellow-100', iconColor: 'text-yellow-600' },
    { label: 'Customers', value: '—', icon: 'people', iconBg: 'bg-purple-100', iconColor: 'text-purple-600' },
  ];

  adminLinks = [
    { label: 'Manage Products', icon: 'inventory_2', route: '/', desc: 'Add, edit, delete products' },
    { label: 'View Orders', icon: 'receipt_long', route: '/orders', desc: 'All customer orders' },
    { label: 'My Profile', icon: 'person', route: '/profile', desc: 'Account settings' },
  ];

  ngOnInit(): void {
    this.orderService.getAllOrders(0, 20).subscribe({
      next: data => {
        this.orders.set(data.content);
        this.stats[0].value = data.totalElements.toString();
        const revenue = data.content.reduce((sum, o) => sum + o.totalAmount, 0);
        this.stats[2].value = `$${revenue.toFixed(0)}`;
      },
      error: () => this.toastService.error('Failed to load orders')
    });
  }

  getStatusClass(status: string): string {
    const classes: Record<string, string> = {
      PENDING:   'bg-yellow-100 text-yellow-700',
      CONFIRMED: 'bg-blue-100 text-blue-700',
      SHIPPED:   'bg-purple-100 text-purple-700',
      DELIVERED: 'bg-green-100 text-green-700',
      CANCELLED: 'bg-red-100 text-red-700',
    };
    return classes[status] ?? 'bg-gray-100 text-gray-700';
  }
}
