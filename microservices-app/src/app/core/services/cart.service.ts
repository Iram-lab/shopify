import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AddToCartRequest, Cart } from '../models/models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class CartService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/api/cart`;

  getCart(): Observable<Cart> {
    return this.http.get<Cart>(this.baseUrl);
  }

  addItem(request: AddToCartRequest): Observable<Cart> {
    return this.http.post<Cart>(`${this.baseUrl}/items`, request);
  }

  updateItem(productId: number, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.baseUrl}/items/${productId}`, { quantity });
  }

  removeItem(productId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.baseUrl}/items/${productId}`);
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(this.baseUrl);
  }
}
