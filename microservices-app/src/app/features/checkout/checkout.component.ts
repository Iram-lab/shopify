import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { Store } from '@ngrx/store';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatDividerModule } from '@angular/material/divider';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { OrderService } from '../../core/services/order.service';
import { ToastService } from '../../core/services/toast.service';
import { AuthService } from '../../core/services/auth.service';
import { CartActions, selectCart } from '../../store/cart/cart.store';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule, MatButtonModule,
            MatIconModule, MatFormFieldModule, MatInputModule,
            MatDividerModule, MatProgressSpinnerModule],
  template: `
    <div class="max-w-4xl mx-auto px-4 py-6">

      <!-- Header -->
      <h1 class="text-2xl font-bold text-gray-900 dark:text-white mb-6 flex items-center gap-2">
        <mat-icon>payment</mat-icon> Checkout
      </h1>

      <!-- Steps indicator -->
      <div class="flex items-center gap-2 mb-8 text-sm">
        <span class="flex items-center gap-1 text-gray-400">
          <mat-icon class="text-base">shopping_cart</mat-icon> Cart
        </span>
        <mat-icon class="text-gray-300 text-base">chevron_right</mat-icon>
        <span class="flex items-center gap-1 font-semibold text-blue-600">
          <mat-icon class="text-base">local_shipping</mat-icon> Shipping
        </span>
        <mat-icon class="text-gray-300 text-base">chevron_right</mat-icon>
        <span class="flex items-center gap-1 text-gray-400">
          <mat-icon class="text-base">payment</mat-icon> Payment
        </span>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-3 gap-6">

        <!-- Shipping Form -->
        <div class="lg:col-span-2">
          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-700 p-6">
            <h2 class="text-lg font-bold text-gray-900 dark:text-white mb-5 flex items-center gap-2">
              <mat-icon class="text-blue-600">local_shipping</mat-icon> Shipping Details
            </h2>

            <form [formGroup]="form" class="space-y-4" novalidate>

              <div class="grid grid-cols-2 gap-4">
                <mat-form-field appearance="outline">
                  <mat-label>First Name</mat-label>
                  <input matInput formControlName="firstName" placeholder="John" />
                  @if (f['firstName'].touched && f['firstName'].hasError('required')) {
                    <mat-error>Required</mat-error>
                  } @else if (f['firstName'].touched && f['firstName'].hasError('pattern')) {
                    <mat-error>Letters only</mat-error>
                  }
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>Last Name</mat-label>
                  <input matInput formControlName="lastName" placeholder="Doe" />
                  @if (f['lastName'].touched && f['lastName'].hasError('required')) {
                    <mat-error>Required</mat-error>
                  } @else if (f['lastName'].touched && f['lastName'].hasError('pattern')) {
                    <mat-error>Letters only</mat-error>
                  }
                </mat-form-field>
              </div>

              <mat-form-field appearance="outline" class="w-full">
                <mat-label>Street Address</mat-label>
                <mat-icon matPrefix class="mr-2 text-gray-400">home</mat-icon>
                <input matInput formControlName="street" placeholder="123 Main Street" />
                @if (f['street'].touched && f['street'].hasError('required')) {
                  <mat-error>Required</mat-error>
                } @else if (f['street'].touched && f['street'].hasError('minlength')) {
                  <mat-error>Enter a valid address</mat-error>
                }
              </mat-form-field>

              <div class="grid grid-cols-3 gap-4">
                <mat-form-field appearance="outline">
                  <mat-label>City</mat-label>
                  <input matInput formControlName="city" placeholder="Mumbai" />
                  @if (f['city'].touched && f['city'].hasError('required')) {
                    <mat-error>Required</mat-error>
                  }
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>State</mat-label>
                  <input matInput formControlName="state" placeholder="Maharashtra" />
                  @if (f['state'].touched && f['state'].hasError('required')) {
                    <mat-error>Required</mat-error>
                  }
                </mat-form-field>

                <mat-form-field appearance="outline">
                  <mat-label>PIN Code</mat-label>
                  <input matInput formControlName="zip" placeholder="400001" maxlength="6" />
                  @if (f['zip'].touched && f['zip'].hasError('required')) {
                    <mat-error>Required</mat-error>
                  } @else if (f['zip'].touched && f['zip'].hasError('pattern')) {
                    <mat-error>6-digit PIN</mat-error>
                  }
                </mat-form-field>
              </div>

              <mat-form-field appearance="outline" class="w-full">
                <mat-label>Phone Number</mat-label>
                <mat-icon matPrefix class="mr-2 text-gray-400">phone</mat-icon>
                <input matInput formControlName="phone" placeholder="+91 98765 43210" />
                @if (f['phone'].touched && f['phone'].hasError('required')) {
                  <mat-error>Required</mat-error>
                } @else if (f['phone'].touched && f['phone'].hasError('pattern')) {
                  <mat-error>Enter valid phone number</mat-error>
                }
              </mat-form-field>

            </form>

            <!-- Razorpay badge -->
            <mat-divider class="my-5" />
            <div class="flex items-center gap-3 bg-gradient-to-r from-blue-50 to-indigo-50
                        dark:from-blue-900/20 dark:to-indigo-900/20
                        border border-blue-200 dark:border-blue-700 rounded-xl p-4">
              <mat-icon class="text-blue-600 text-3xl">lock</mat-icon>
              <div>
                <p class="font-semibold text-blue-800 dark:text-blue-300 text-sm">Secure Payment via Razorpay</p>
                <p class="text-xs text-blue-600 dark:text-blue-400 mt-0.5">
                  UPI · Cards · Net Banking · Wallets — all supported
                </p>
              </div>
            </div>
          </div>
        </div>

        <!-- Order Summary + Pay Button -->
        <div class="lg:col-span-1">
          <div class="bg-white dark:bg-gray-800 rounded-2xl shadow-sm border border-gray-100 dark:border-gray-700 p-6 sticky top-24">
            <h2 class="text-lg font-bold text-gray-900 dark:text-white mb-4">Order Summary</h2>
            <mat-divider class="mb-4" />

            @if (cart$ | async; as cart) {
              <div class="space-y-2 mb-4 max-h-52 overflow-y-auto pr-1">
                @for (item of cart.items; track item.id) {
                  <div class="flex justify-between text-sm">
                    <span class="text-gray-600 dark:text-gray-400 truncate flex-1 mr-2">
                      {{ item.productName }} × {{ item.quantity }}
                    </span>
                    <span class="font-medium text-gray-900 dark:text-white flex-shrink-0">
                      ₹{{ item.subtotal | number:'1.2-2' }}
                    </span>
                  </div>
                }
              </div>

              <mat-divider class="mb-3" />

              <div class="flex justify-between text-sm text-gray-500 dark:text-gray-400 mb-1">
                <span>Subtotal</span>
                <span>₹{{ cart.totalPrice | number:'1.2-2' }}</span>
              </div>
              <div class="flex justify-between text-sm text-gray-500 dark:text-gray-400 mb-4">
                <span>Shipping</span>
                <span class="text-green-600 font-medium">FREE</span>
              </div>

              <mat-divider class="mb-4" />

              <div class="flex justify-between font-bold text-xl text-gray-900 dark:text-white mb-6">
                <span>Total</span>
                <span class="text-blue-600">₹{{ cart.totalPrice | number:'1.2-2' }}</span>
              </div>
            }

            <!-- PAY NOW button -->
            <button mat-flat-button color="primary"
                    class="w-full h-14 text-base font-bold rounded-xl"
                    [disabled]="placing()"
                    (click)="placeOrder()">
              @if (placing()) {
                <mat-progress-spinner diameter="22" mode="indeterminate"
                  class="inline-block mr-2"></mat-progress-spinner>
                Processing...
              } @else {
                Pay Now with Razorpay
              }
            </button>

            <a routerLink="/cart" mat-stroked-button class="w-full h-10 mt-3 text-sm">
              Back to Cart
            </a>
          </div>
        </div>

      </div>
    </div>
  `
})
export class CheckoutComponent implements OnInit {
  private fb = inject(FormBuilder);
  private orderService = inject(OrderService);
  private toastService = inject(ToastService);
  private authService = inject(AuthService);
  private store = inject(Store);
  private router = inject(Router);

  cart$ = this.store.select(selectCart);
  placing = signal(false);

  form = this.fb.group({
    firstName: ['', [Validators.required, Validators.pattern(/^[a-zA-Z]+$/)]],
    lastName:  ['', [Validators.required, Validators.pattern(/^[a-zA-Z]+$/)]],
    street:    ['', [Validators.required, Validators.minLength(5)]],
    city:      ['', [Validators.required, Validators.pattern(/^[a-zA-Z\s]+$/)]],
    state:     ['', [Validators.required, Validators.pattern(/^[a-zA-Z\s]+$/)]],
    zip:       ['', [Validators.required, Validators.pattern(/^\d{5,6}$/)]],
    phone:     ['', [Validators.required, Validators.pattern(/^[+]?[\d\s\-()]{7,15}$/)]],
  });

  get f() { return this.form.controls; }

  ngOnInit(): void {
    this.store.dispatch(CartActions.loadCart());
  }

  placeOrder(): void {
    this.form.markAllAsTouched();
    if (this.form.invalid) {
      this.toastService.error('Please fill in all shipping details.');
      return;
    }

    const { firstName, lastName, street, city, state, zip, phone } = this.form.value;
    const shippingAddress = `${firstName} ${lastName}, ${street}, ${city}, ${state} ${zip}, Phone: ${phone}`;

    this.placing.set(true);
    this.orderService.placeOrder({ shippingAddress }).subscribe({
      next: order => {
        this.placing.set(false);
        console.log('Order response from backend:', order);
        this.openRazorpay(order);
      },
      error: err => {
        this.placing.set(false);
        this.toastService.error(err.error?.detail || err.error?.message || 'Failed to place order. Please try again.');
      }
    });
  }

  private openRazorpay(order: any): void {
    console.log('openRazorpay called with:', order);
    console.log('razorpayOrderId:', order?.razorpayOrderId);
    console.log('razorpayKeyId:', order?.razorpayKeyId);

    if (!order.razorpayOrderId) {
      this.toastService.error(`Payment gateway error: razorpayOrderId missing. Order status: ${order.status}`);
      return;
    }

    const options: any = {
      key: order.razorpayKeyId || 'rzp_test_TOpt8j6M6j1CXn',
      amount: Math.round(order.totalAmount * 100),
      currency: 'INR',
      name: 'ShopMicro',
      description: `Order #${order.orderNumber}`,
      order_id: order.razorpayOrderId,
      handler: (response: any) => {
        this.orderService.verifyPayment({
          orderNumber: order.orderNumber,
          razorpayOrderId: response.razorpay_order_id,
          razorpayPaymentId: response.razorpay_payment_id,
          razorpaySignature: response.razorpay_signature,
        }).subscribe({
          next: result => {
            if (result.status === 'SUCCESS') {
              this.orderService.confirmOrder(order.orderNumber).subscribe({
                next: () => {
                  this.store.dispatch(CartActions.clearCart());
                  this.toastService.success(`Payment successful! Order ${order.orderNumber} confirmed.`);
                  this.router.navigate(['/orders', order.orderNumber]);
                },
                error: () => {
                  this.store.dispatch(CartActions.clearCart());
                  this.toastService.success(`Payment successful! Order ${order.orderNumber} confirmed.`);
                  this.router.navigate(['/orders', order.orderNumber]);
                }
              });
            } else {
              this.toastService.error('Payment verification failed. Please contact support.');
            }
          },
          error: () => this.toastService.error('Payment verification error. Please contact support.')
        });
      },
      prefill: {
        name: `${this.form.value.firstName} ${this.form.value.lastName}`,
        email: this.authService.currentUser()?.email || '',
        contact: this.form.value.phone || '',
      },
      notes: {
        address: `${this.form.value.street}, ${this.form.value.city}`,
      },
      theme: { color: '#2563eb' },
      modal: {
        ondismiss: () => {
          this.toastService.warning('Payment cancelled. Your order is saved — you can retry from Orders page.');
        }
      }
    };

    const rzp = new (window as any).Razorpay(options);
    rzp.on('payment.failed', (response: any) => {
      this.toastService.error(`Payment failed: ${response.error.description}`);
    });
    rzp.open();
  }
}
