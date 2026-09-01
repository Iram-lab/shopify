import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Category, PagedResponse, Product, ProductSearchParams, ProductSummary } from '../models/models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ProductService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/api/products`;
  private categoryUrl = `${environment.apiUrl}/api/categories`;

  getProducts(page = 0, size = 12, sortBy = 'createdAt'): Observable<PagedResponse<ProductSummary>> {
    const params = new HttpParams()
      .set('page', page).set('size', size).set('sortBy', sortBy);
    return this.http.get<PagedResponse<ProductSummary>>(this.baseUrl, { params });
  }

  searchProducts(searchParams: ProductSearchParams): Observable<PagedResponse<ProductSummary>> {
    let params = new HttpParams()
      .set('page', searchParams.page ?? 0)
      .set('size', searchParams.size ?? 12);
    if (searchParams.keyword) params = params.set('keyword', searchParams.keyword);
    if (searchParams.categoryId) params = params.set('categoryId', searchParams.categoryId);
    if (searchParams.minPrice != null) params = params.set('minPrice', searchParams.minPrice);
    if (searchParams.maxPrice != null) params = params.set('maxPrice', searchParams.maxPrice);
    return this.http.get<PagedResponse<ProductSummary>>(`${this.baseUrl}/search`, { params });
  }

  getProductsByCategory(categoryId: number, page = 0, size = 12): Observable<PagedResponse<ProductSummary>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<ProductSummary>>(
      `${this.baseUrl}/category/${categoryId}`, { params });
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.baseUrl}/${id}`);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(this.categoryUrl);
  }
}
