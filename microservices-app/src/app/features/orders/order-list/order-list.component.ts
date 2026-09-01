import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { OrderService } from '../../../core/services/order.service';
import { ToastService } from '../../../core/services/toast.service';
import { Order, PagedResponse } from '../../../core/models/models';
import { SkeletonComponent } from '../../../shared/components/skeleton/skeleton.component';

@Component({
  selector: 'app-order-list',
  standalone: true,
  imports: [CommonModule, RouterLink, MatButtonModule, MatIconModule,
            MatChipsModule, MatPaginatorModule, SkeletonComponent],
  template: `
    <div class="max-w-4xl mx-auto">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white mb-6 flex items-center gap-2">
        <mat-icon>receipt_long</mat-icon> My Orders
      </h1>

      @if (loading()) {
        <div class="space-y-3">
          @for (i of [1,2,3]; track i) { <app-skeleton type="list-item" /> }
        </div>
      } @else if (orders().length === 0) {
        <div class="text-center py-20 bg-white dark:bg-gray-800 rounded-2xl shadow-sm">
          <mat-icon class="text-7xl text-gray-300">receipt_long</mat-icon>
          <h2 class="text-xl font-semibold text-gray-600 dark:text-gray-300 mt-4">No orders yet</h2>
          <a routerLink="/" mat-flat-button color="primary" class="mt-6">Start Shopping</a>
        </div>
      } @else {
        <div class="space-y-4">
          @for (order of orders(); track order.id) {
            <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 p-5">
              <div class="flex flex-wrap items-start justify-between gap-3 mb-4">
                <div>
                  <p class="text-xs text-gray-500 dark:text-gray-400">Order Number</p>
                  <p class="font-bold text-gray-900 dark:text-white">{{ order.orderNumber }}</p>
                </div>
                <span [class]="getStatusClass(order.status)"
                      class="px-3 py-1 rounded-full text-xs font-semibold">
                  {{ order.status }}
                </span>
                <div class="text-right">
                  <p class="text-xs text-gray-500 dark:text-gray-400">Total</p>
                  <p class="font-bold text-blue-600 text-lg">\${{ order.totalAmount | number:'1.2-2' }}</p>
                </div>
              </div>

              <div class="flex flex-wrap gap-2 mb-4">
                @for (item of order.items.slice(0, 3); track item.productId) {
                  <span class="bg-gray-100 dark:bg-gray-700 text-gray-700 dark:text-gray-300 text-xs px-2 py-1 rounded">
                    {{ item.productName }} × {{ item.quantity }}
                  </span>
                }
                @if (order.items.length > 3) {
                  <span class="text-xs text-gray-500">+{{ order.items.length - 3 }} more</span>
                }
              </div>

              <div class="flex items-center justify-between">
                <p class="text-xs text-gray-500 dark:text-gray-400">
                  {{ order.createdAt | date:'mediumDate' }}
                </p>
                <a [routerLink]="['/orders', order.orderNumber]" mat-stroked-button color="primary" class="text-sm">
                  View Details <mat-icon class="text-sm ml-1">arrow_forward</mat-icon>
                </a>
              </div>
            </div>
          }
        </div>

        @if ((pagedData()?.totalPages ?? 0) > 1) {
          <mat-paginator
            [length]="pagedData()?.totalElements ?? 0"
            [pageSize]="pageSize"
            [pageIndex]="currentPage"
            (page)="onPageChange($event)"
            class="bg-white dark:bg-gray-800 rounded-xl shadow-sm mt-4">
          </mat-paginator>
        }
      }
    </div>
  `
})
export class OrderListComponent implements OnInit {
  private orderService = inject(OrderService);
  private toastService = inject(ToastService);

  orders = signal<Order[]>([]);
  pagedData = signal<PagedResponse<Order> | null>(null);
  loading = signal(true);
  currentPage = 0;
  pageSize = 10;

  ngOnInit(): void { this.loadOrders(); }

  loadOrders(): void {
    this.loading.set(true);
    this.orderService.getMyOrders(this.currentPage, this.pageSize).subscribe({
      next: data => {
        this.pagedData.set(data);
        this.orders.set(data.content);
        this.loading.set(false);
      },
      error: () => {
        this.toastService.error('Failed to load orders');
        this.loading.set(false);
      }
    });
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadOrders();
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
