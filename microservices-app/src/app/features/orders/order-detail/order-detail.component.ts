import { Component, inject, Input, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { MatStepperModule } from '@angular/material/stepper';
import { OrderService } from '../../../core/services/order.service';
import { ToastService } from '../../../core/services/toast.service';
import { Order } from '../../../core/models/models';
import { SkeletonComponent } from '../../../shared/components/skeleton/skeleton.component';

@Component({
  selector: 'app-order-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, MatButtonModule, MatIconModule,
            MatDividerModule, MatStepperModule, SkeletonComponent],
  template: `
    <div class="max-w-3xl mx-auto">
      <div class="flex items-center gap-3 mb-6">
        <a routerLink="/orders" mat-icon-button>
          <mat-icon>arrow_back</mat-icon>
        </a>
        <h1 class="text-2xl font-bold text-gray-900 dark:text-white">Order Details</h1>
      </div>

      @if (loading()) {
        <app-skeleton type="product-detail" />
      } @else if (order(); as o) {
        <div class="space-y-4">

          <!-- Status Card -->
          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-700 p-6">
            <div class="flex flex-wrap items-center justify-between gap-4 mb-4">
              <div>
                <p class="text-sm text-gray-500 dark:text-gray-400">Order Number</p>
                <p class="text-xl font-bold text-gray-900 dark:text-white">{{ o.orderNumber }}</p>
                <p class="text-xs text-gray-400 mt-1">Placed on {{ o.createdAt | date:'longDate' }}</p>
              </div>
              <span [class]="getStatusClass(o.status)"
                    class="px-4 py-2 rounded-full text-sm font-bold">
                {{ o.status }}
              </span>
            </div>

            <!-- Progress Steps -->
            <div class="flex items-center justify-between mt-6 relative">
              <div class="absolute top-4 left-0 right-0 h-0.5 bg-gray-200 dark:bg-gray-600 z-0"></div>
              @for (step of statusSteps; track step.label) {
                <div class="flex flex-col items-center z-10 flex-1">
                  <div [class]="isStepComplete(o.status, step.status)
                    ? 'bg-blue-600 text-white'
                    : 'bg-gray-200 dark:bg-gray-600 text-gray-400'"
                       class="w-8 h-8 rounded-full flex items-center justify-center">
                    <mat-icon class="text-sm">{{ step.icon }}</mat-icon>
                  </div>
                  <p class="text-xs mt-1 text-gray-500 dark:text-gray-400 text-center">{{ step.label }}</p>
                </div>
              }
            </div>
          </div>

          <!-- Items -->
          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-700 p-6">
            <h2 class="font-bold text-gray-900 dark:text-white mb-4">Items Ordered</h2>
            <div class="space-y-3">
              @for (item of o.items; track item.productId) {
                <div class="flex justify-between items-center py-2">
                  <div>
                    <p class="font-medium text-gray-900 dark:text-white text-sm">{{ item.productName }}</p>
                    <p class="text-xs text-gray-500">\${{ item.unitPrice | number:'1.2-2' }} × {{ item.quantity }}</p>
                  </div>
                  <p class="font-bold text-gray-900 dark:text-white">\${{ item.subtotal | number:'1.2-2' }}</p>
                </div>
                <mat-divider />
              }
            </div>
            <div class="flex justify-between font-bold text-lg mt-4 text-gray-900 dark:text-white">
              <span>Total</span>
              <span class="text-blue-600">\${{ o.totalAmount | number:'1.2-2' }}</span>
            </div>
          </div>

          <!-- Shipping & Payment -->
          <div class="grid grid-cols-1 sm:grid-cols-2 gap-4">
            <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-700 p-5">
              <h3 class="font-bold text-gray-900 dark:text-white mb-2 flex items-center gap-2">
                <mat-icon class="text-blue-600 text-sm">local_shipping</mat-icon> Shipping Address
              </h3>
              <p class="text-sm text-gray-600 dark:text-gray-400">{{ o.shippingAddress }}</p>
            </div>
            <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-700 p-5">
              <h3 class="font-bold text-gray-900 dark:text-white mb-2 flex items-center gap-2">
                <mat-icon class="text-blue-600 text-sm">payment</mat-icon> Payment
              </h3>
              <p class="text-sm text-gray-600 dark:text-gray-400">
                ID: {{ o.paymentId || 'N/A' }}
              </p>
            </div>
          </div>
        </div>
      }
    </div>
  `
})
export class OrderDetailComponent implements OnInit {
  @Input() orderNumber!: string;

  private orderService = inject(OrderService);
  private toastService = inject(ToastService);

  order = signal<Order | null>(null);
  loading = signal(true);

  statusSteps = [
    { label: 'Pending',   status: 'PENDING',   icon: 'hourglass_empty' },
    { label: 'Confirmed', status: 'CONFIRMED',  icon: 'check_circle' },
    { label: 'Shipped',   status: 'SHIPPED',    icon: 'local_shipping' },
    { label: 'Delivered', status: 'DELIVERED',  icon: 'home' },
  ];

  statusOrder = ['PENDING', 'CONFIRMED', 'SHIPPED', 'DELIVERED'];

  ngOnInit(): void {
    this.orderService.getOrderByNumber(this.orderNumber).subscribe({
      next: o => { this.order.set(o); this.loading.set(false); },
      error: () => { this.toastService.error('Failed to load order'); this.loading.set(false); }
    });
  }

  isStepComplete(currentStatus: string, stepStatus: string): boolean {
    return this.statusOrder.indexOf(currentStatus) >= this.statusOrder.indexOf(stepStatus);
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
