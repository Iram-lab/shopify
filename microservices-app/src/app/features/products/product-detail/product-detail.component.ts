import { Component, inject, OnInit, signal, Input } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Store } from '@ngrx/store';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { ProductService } from '../../../core/services/product.service';
import { ToastService } from '../../../core/services/toast.service';
import { AuthService } from '../../../core/services/auth.service';
import { CartActions } from '../../../store/cart/cart.store';
import { Product } from '../../../core/models/models';
import { SkeletonComponent } from '../../../shared/components/skeleton/skeleton.component';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, MatButtonModule,
            MatIconModule, MatChipsModule, MatDividerModule, SkeletonComponent],
  template: `
    <div class="max-w-5xl mx-auto">

      <!-- Breadcrumb -->
      <nav class="flex items-center gap-2 text-sm text-gray-500 mb-6">
        <a routerLink="/" class="hover:text-blue-600">Home</a>
        <mat-icon class="text-sm">chevron_right</mat-icon>
        <span class="text-gray-900 dark:text-white">{{ product()?.name }}</span>
      </nav>

      @if (loading()) {
        <app-skeleton type="product-detail" />
      } @else if (product(); as p) {
        <div class="grid grid-cols-1 md:grid-cols-2 gap-8">

          <!-- Image -->
          <div class="bg-white dark:bg-gray-800 rounded-2xl overflow-hidden shadow-sm border border-gray-100 dark:border-gray-700">
            <img [src]="p.imageUrl || 'https://placehold.co/600x500?text=No+Image'"
                 [alt]="p.name"
                 class="w-full h-96 object-cover"
                 (error)="onImgError($event)" />
          </div>

          <!-- Details -->
          <div class="space-y-4">
            <div>
              <span class="inline-block bg-blue-100 text-blue-700 text-xs font-semibold px-3 py-1 rounded-full mb-2">
                {{ p.category?.name }}
              </span>
              <h1 class="text-2xl font-bold text-gray-900 dark:text-white">{{ p.name }}</h1>
              <p class="text-gray-500 dark:text-gray-400 text-sm mt-1">Brand: {{ p.brand }}</p>
            </div>

            <div class="text-3xl font-bold text-blue-600">\${{ p.price | number:'1.2-2' }}</div>

            <mat-divider />

            <p class="text-gray-600 dark:text-gray-300 text-sm leading-relaxed">{{ p.description }}</p>

            <mat-divider />

            <!-- Quantity selector -->
            <div class="flex items-center gap-4">
              <span class="text-sm font-medium text-gray-700 dark:text-gray-300">Quantity:</span>
              <div class="flex items-center border border-gray-300 dark:border-gray-600 rounded-lg overflow-hidden">
                <button mat-icon-button (click)="decrementQty()" [disabled]="quantity <= 1">
                  <mat-icon>remove</mat-icon>
                </button>
                <span class="px-4 py-2 font-semibold text-gray-900 dark:text-white min-w-[3rem] text-center">
                  {{ quantity }}
                </span>
                <button mat-icon-button (click)="incrementQty()">
                  <mat-icon>add</mat-icon>
                </button>
              </div>
            </div>

            <!-- Actions -->
            <div class="flex gap-3">
              <button mat-flat-button color="primary" class="flex-1 h-12 text-base"
                      (click)="addToCart(p)">
                <mat-icon class="mr-2">add_shopping_cart</mat-icon>
                Add to Cart
              </button>
              <a routerLink="/cart" mat-stroked-button color="primary" class="h-12 px-6">
                <mat-icon>shopping_cart</mat-icon>
              </a>
            </div>

            <!-- Meta -->
            <div class="bg-gray-50 dark:bg-gray-700 rounded-xl p-4 space-y-2 text-sm">
              <div class="flex items-center gap-2 text-green-600">
                <mat-icon class="text-sm">local_shipping</mat-icon>
                <span>Free shipping on orders over \$50</span>
              </div>
              <div class="flex items-center gap-2 text-blue-600">
                <mat-icon class="text-sm">verified_user</mat-icon>
                <span>Secure checkout guaranteed</span>
              </div>
            </div>
          </div>
        </div>
      } @else {
        <div class="text-center py-20">
          <mat-icon class="text-6xl text-gray-300">error_outline</mat-icon>
          <p class="text-gray-500 mt-4">Product not found</p>
          <a routerLink="/" mat-flat-button color="primary" class="mt-4">Back to Products</a>
        </div>
      }
    </div>
  `
})
export class ProductDetailComponent implements OnInit {
  @Input() id!: string;

  private productService = inject(ProductService);
  private toastService = inject(ToastService);
  private authService = inject(AuthService);
  private store = inject(Store);

  product = signal<Product | null>(null);
  loading = signal(true);
  quantity = 1;

  ngOnInit(): void {
    this.productService.getProductById(+this.id).subscribe({
      next: p => { this.product.set(p); this.loading.set(false); },
      error: () => { this.toastService.error('Failed to load product'); this.loading.set(false); }
    });
  }

  incrementQty(): void { this.quantity++; }
  decrementQty(): void { if (this.quantity > 1) this.quantity--; }

  addToCart(product: Product): void {
    if (!this.authService.isLoggedIn()) {
      this.toastService.warning('Please login to add items to cart');
      return;
    }
    this.store.dispatch(CartActions.addItem({ request: { productId: product.id, quantity: this.quantity } }));
  }

  onImgError(event: Event): void {
    (event.target as HTMLImageElement).src = 'https://placehold.co/600x500?text=No+Image';
  }
}
