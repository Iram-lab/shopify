import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { Store } from '@ngrx/store';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatSelectModule } from '@angular/material/select';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatChipsModule } from '@angular/material/chips';
import { MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { ProductService } from '../../../core/services/product.service';
import { ToastService } from '../../../core/services/toast.service';
import { CartActions } from '../../../store/cart/cart.store';
import { AuthService } from '../../../core/services/auth.service';
import { Category, PagedResponse, ProductSummary } from '../../../core/models/models';
import { SkeletonComponent } from '../../../shared/components/skeleton/skeleton.component';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, MatButtonModule, MatIconModule,
            MatSelectModule, MatFormFieldModule, MatChipsModule,
            MatPaginatorModule, SkeletonComponent],
  template: `
    <div class="space-y-6">

      <!-- Hero Banner -->
      <div class="bg-gradient-to-r from-blue-600 to-blue-800 rounded-2xl p-8 text-white">
        <h1 class="text-3xl font-bold mb-2">Discover Amazing Products</h1>
        <p class="text-blue-100 mb-6">Shop from thousands of quality products</p>
        <div class="relative max-w-xl">
          <mat-icon class="absolute left-3 top-3 text-gray-400">search</mat-icon>
          <input type="text" [(ngModel)]="keyword" (keyup.enter)="onSearch()"
                 placeholder="Search products, brands..."
                 class="w-full pl-10 pr-4 py-3 rounded-xl text-gray-900 text-sm focus:outline-none focus:ring-2 focus:ring-white" />
        </div>
      </div>

      <!-- Filters Row -->
      <div class="flex flex-wrap items-center gap-3">
        <mat-form-field appearance="outline" class="w-48">
          <mat-label>Category</mat-label>
          <mat-select [(ngModel)]="selectedCategory" (ngModelChange)="onCategoryChange()">
            <mat-option [value]="null">All Categories</mat-option>
            @for (cat of categories(); track cat.id) {
              <mat-option [value]="cat.id">{{ cat.name }}</mat-option>
            }
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="outline" class="w-40">
          <mat-label>Sort By</mat-label>
          <mat-select [(ngModel)]="sortBy" (ngModelChange)="loadProducts()">
            <mat-option value="createdAt">Newest</mat-option>
            <mat-option value="price">Price</mat-option>
            <mat-option value="name">Name</mat-option>
          </mat-select>
        </mat-form-field>

        @if (keyword || selectedCategory) {
          <button mat-stroked-button (click)="clearFilters()" class="flex items-center gap-1">
            <mat-icon class="text-sm">clear</mat-icon> Clear Filters
          </button>
        }

        <span class="ml-auto text-sm text-gray-500 dark:text-gray-400">
          {{ pagedData()?.totalElements ?? 0 }} products found
        </span>
      </div>

      <!-- Product Grid -->
      @if (loading()) {
        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
          @for (i of skeletons; track i) {
            <app-skeleton type="product-card" />
          }
        </div>
      } @else if (products().length === 0) {
        <div class="text-center py-20">
          <mat-icon class="text-6xl text-gray-300">search_off</mat-icon>
          <p class="text-gray-500 mt-4 text-lg">No products found</p>
          <button mat-flat-button color="primary" (click)="clearFilters()" class="mt-4">
            Browse All Products
          </button>
        </div>
      } @else {
        <div class="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-4 gap-4">
          @for (product of products(); track product.id) {
            <div class="product-card bg-white dark:bg-gray-800 rounded-xl overflow-hidden shadow-sm border border-gray-100 dark:border-gray-700">
              <a [routerLink]="['/products', product.id]">
                <div class="relative overflow-hidden h-52 bg-gray-100 dark:bg-gray-700">
                  <img [src]="product.imageUrl || 'https://placehold.co/400x300?text=No+Image'"
                       [alt]="product.name"
                       class="w-full h-full object-cover hover:scale-105 transition-transform duration-300"
                       (error)="onImgError($event)" />
                  <span class="absolute top-2 left-2 bg-blue-600 text-white text-xs px-2 py-1 rounded-full">
                    {{ product.categoryName }}
                  </span>
                </div>
              </a>
              <div class="p-4">
                <a [routerLink]="['/products', product.id]">
                  <h3 class="font-semibold text-gray-900 dark:text-white text-sm line-clamp-2 hover:text-blue-600 transition-colors">
                    {{ product.name }}
                  </h3>
                </a>
                <p class="text-xs text-gray-500 dark:text-gray-400 mt-1">{{ product.brand }}</p>
                <div class="flex items-center justify-between mt-3">
                  <span class="text-lg font-bold text-blue-600">\${{ product.price | number:'1.2-2' }}</span>
                </div>
                <button mat-flat-button color="primary" class="w-full mt-3 text-sm"
                        (click)="addToCart(product)">
                  <mat-icon class="text-sm mr-1">add_shopping_cart</mat-icon>
                  Add to Cart
                </button>
              </div>
            </div>
          }
        </div>

        <!-- Pagination -->
        @if ((pagedData()?.totalPages ?? 0) > 1) {
          <mat-paginator
            [length]="pagedData()?.totalElements ?? 0"
            [pageSize]="pageSize"
            [pageIndex]="currentPage"
            [pageSizeOptions]="[12, 24, 48]"
            (page)="onPageChange($event)"
            class="bg-white dark:bg-gray-800 rounded-xl shadow-sm">
          </mat-paginator>
        }
      }
    </div>
  `
})
export class ProductListComponent implements OnInit {
  private productService = inject(ProductService);
  private toastService = inject(ToastService);
  private authService = inject(AuthService);
  private store = inject(Store);
  private route = inject(ActivatedRoute);

  products = signal<ProductSummary[]>([]);
  categories = signal<Category[]>([]);
  pagedData = signal<PagedResponse<ProductSummary> | null>(null);
  loading = signal(true);

  keyword = '';
  selectedCategory: number | null = null;
  sortBy = 'createdAt';
  currentPage = 0;
  pageSize = 12;
  skeletons = Array(8).fill(0);

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['keyword']) this.keyword = params['keyword'];
    });
    this.loadCategories();
    this.loadProducts();
  }

  loadProducts(): void {
    this.loading.set(true);
    const obs = (this.keyword || this.selectedCategory)
      ? this.productService.searchProducts({
          keyword: this.keyword || undefined,
          categoryId: this.selectedCategory ?? undefined,
          page: this.currentPage,
          size: this.pageSize,
        })
      : this.productService.getProducts(this.currentPage, this.pageSize, this.sortBy);

    obs.subscribe({
      next: data => {
        this.pagedData.set(data);
        this.products.set(data.content);
        this.loading.set(false);
      },
      error: () => {
        this.toastService.error('Failed to load products');
        this.loading.set(false);
      }
    });
  }

  loadCategories(): void {
    this.productService.getCategories().subscribe(cats => this.categories.set(cats));
  }

  onSearch(): void { this.currentPage = 0; this.loadProducts(); }
  onCategoryChange(): void { this.currentPage = 0; this.loadProducts(); }

  clearFilters(): void {
    this.keyword = '';
    this.selectedCategory = null;
    this.currentPage = 0;
    this.loadProducts();
  }

  onPageChange(event: PageEvent): void {
    this.currentPage = event.pageIndex;
    this.pageSize = event.pageSize;
    this.loadProducts();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }

  addToCart(product: ProductSummary): void {
    if (!this.authService.isLoggedIn()) {
      this.toastService.warning('Please login to add items to cart');
      return;
    }
    this.store.dispatch(CartActions.addItem({ request: { productId: product.id, quantity: 1 } }));
  }

  onImgError(event: Event): void {
    (event.target as HTMLImageElement).src = 'https://placehold.co/400x300?text=No+Image';
  }
}
