import { Component, inject, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { Store } from '@ngrx/store';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatDividerModule } from '@angular/material/divider';
import { CartActions, selectCart, selectCartLoading } from '../../store/cart/cart.store';
import { SkeletonComponent } from '../../shared/components/skeleton/skeleton.component';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterLink, MatButtonModule, MatIconModule, MatDividerModule, SkeletonComponent],
  template: `
    <div class="max-w-4xl mx-auto px-4 py-6">
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white mb-6 flex items-center gap-2">
        <mat-icon>shopping_cart</mat-icon> Shopping Cart
      </h1>

      @if (loading$ | async) {
        <div class="space-y-3">
          @for (i of [1,2,3]; track i) { <app-skeleton type="list-item" /> }
        </div>
      } @else if (!(cart$ | async)?.items?.length) {
        <div class="text-center py-20 bg-white dark:bg-gray-800 rounded-2xl shadow-sm">
          <mat-icon class="text-7xl text-gray-300">shopping_cart</mat-icon>
          <h2 class="text-xl font-semibold text-gray-600 dark:text-gray-300 mt-4">Your cart is empty</h2>
          <p class="text-gray-400 mt-2">Add some products to get started</p>
          <a routerLink="/" mat-flat-button color="primary" class="mt-6">Start Shopping</a>
        </div>
      } @else {
        <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

          <!-- Cart Items List -->
          <div class="lg:col-span-2 space-y-4">
            @for (item of (cart$ | async)?.items; track item.id) {
              <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 p-4 flex gap-4">
                <img [src]="item.imageUrl || 'https://placehold.co/100x100?text=?'"
                     [alt]="item.productName"
                     class="w-24 h-24 object-cover rounded-lg flex-shrink-0"
                     (error)="onImgError($event)" />
                <div class="flex-1 min-w-0">
                  <h3 class="font-semibold text-gray-900 dark:text-white text-sm line-clamp-2">
                    {{ item.productName }}
                  </h3>
                  <p class="text-blue-600 font-bold mt-1 text-base">₹{{ item.unitPrice | number:'1.2-2' }}</p>

                  <div class="flex items-center justify-between mt-3">
                    <!-- Quantity controls -->
                    <div class="flex items-center border border-gray-200 dark:border-gray-600 rounded-lg overflow-hidden">
                      <button mat-icon-button class="h-8 w-8"
                              (click)="updateQty(item.productId, item.quantity - 1)"
                              [disabled]="item.quantity <= 1">
                        <mat-icon class="text-sm">remove</mat-icon>
                      </button>
                      <span class="px-4 text-sm font-semibold min-w-[2rem] text-center">{{ item.quantity }}</span>
                      <button mat-icon-button class="h-8 w-8"
                              (click)="updateQty(item.productId, item.quantity + 1)">
                        <mat-icon class="text-sm">add</mat-icon>
                      </button>
                    </div>
                      <p> Newly adde p </p>
                       <p> Newly adde p </p>
                    <div class="flex items-center gap-3">
                      <span class="font-bold text-gray-900 dark:text-white text-base">
                        ₹{{ item.subtotal | number:'1.2-2' }}
                      </span>
                      <button mat-icon-button color="warn" (click)="removeItem(item.productId)"
                              title="Remove item">
                        <mat-icon>delete_outline</mat-icon>
                      </button>
                    </div>
                  </div>
                </div>
              </div>
            }
          </div>

          <!-- Order Summary + Proceed to Pay -->
          <div class="lg:col-span-1">
            <div class="bg-white dark:bg-gray-800 rounded-xl shadow-sm border border-gray-100 dark:border-gray-700 p-6 sticky top-24">
              <h2 class="text-lg font-bold text-gray-900 dark:text-white mb-4">Order Summary</h2>
              <mat-divider class="mb-4" />

              <div class="space-y-3 text-sm mb-4">
                @for (item of (cart$ | async)?.items; track item.id) {
                  <div class="flex justify-between text-gray-600 dark:text-gray-400">
                    <span class="truncate flex-1 mr-2">{{ item.productName }} × {{ item.quantity }}</span>
                    <span class="font-medium text-gray-900 dark:text-white flex-shrink-0">
                      ₹{{ item.subtotal | number:'1.2-2' }}
                    </span>
                  </div>
                }
              </div>

              <mat-divider class="mb-4" />

              <div class="flex justify-between text-gray-500 dark:text-gray-400 text-sm mb-2">
                <span>Items ({{ (cart$ | async)?.totalItems }})</span>
                <span>₹{{ (cart$ | async)?.totalPrice | number:'1.2-2' }}</span>
              </div>
              <div class="flex justify-between text-gray-500 dark:text-gray-400 text-sm mb-4">
                <span>Shipping</span>
                <span class="text-green-600 font-medium">FREE</span>
              </div>

              <mat-divider class="mb-4" />

              <div class="flex justify-between font-bold text-xl text-gray-900 dark:text-white mb-6">
                <span>Total</span>
                <span class="text-blue-600">₹{{ (cart$ | async)?.totalPrice | number:'1.2-2' }}</span>
              </div>

              <!-- PROCEED TO PAY button -->
              <button mat-flat-button color="primary"
                      class="w-full h-14 text-base font-bold rounded-xl"
                      (click)="proceedToPay()">
                Proceed to Pay
              </button>

              <a routerLink="/" mat-stroked-button class="w-full h-10 mt-3 text-sm">
                Continue Shopping
              </a>
            </div>
          </div>

        </div>
      }
    </div>
  `
})
export class CartComponent implements OnInit {
  private store = inject(Store);
  private router = inject(Router);

  cart$ = this.store.select(selectCart);
  loading$ = this.store.select(selectCartLoading);

  ngOnInit(): void {
    this.store.dispatch(CartActions.loadCart());
  }

  updateQty(productId: number, quantity: number): void {
    if (quantity < 1) return;
    this.store.dispatch(CartActions.updateItem({ productId, quantity }));
  }

  removeItem(productId: number): void {
    this.store.dispatch(CartActions.removeItem({ productId }));
  }

  proceedToPay(): void {
    this.router.navigate(['/checkout']);
  }

  onImgError(event: Event): void {
    (event.target as HTMLImageElement).src = 'https://placehold.co/100x100?text=?';
  }
}
