import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Order, PagedResponse, PlaceOrderRequest } from '../models/models';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class OrderService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/api/orders`;
  private paymentUrl = `${environment.apiUrl}/api/payments`;

  placeOrder(request: PlaceOrderRequest): Observable<Order> {
    return this.http.post<Order>(this.baseUrl, request);
  }

  confirmOrder(orderNumber: string): Observable<Order> {
    return this.http.post<Order>(`${this.baseUrl}/${orderNumber}/confirm`, {});
  }

  verifyPayment(body: {
    orderNumber: string;
    razorpayOrderId: string;
    razorpayPaymentId: string;
    razorpaySignature: string;
  }): Observable<{ paymentId: string; status: string; message: string }> {
    return this.http.post<any>(`${this.paymentUrl}/verify`, body);
  }

  getMyOrders(page = 0, size = 10): Observable<PagedResponse<Order>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<Order>>(`${this.baseUrl}/my-orders`, { params });
  }

  getOrderByNumber(orderNumber: string): Observable<Order> {
    return this.http.get<Order>(`${this.baseUrl}/${orderNumber}`);
  }

  getAllOrders(page = 0, size = 10): Observable<PagedResponse<Order>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<PagedResponse<Order>>(this.baseUrl, { params });
  }
}
