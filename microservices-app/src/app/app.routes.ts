import { Routes } from '@angular/router';
import { authGuard, adminGuard, guestGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./features/products/product-list/product-list.component')
      .then(m => m.ProductListComponent),
  },
  {
    path: 'products/:id',
    loadComponent: () => import('./features/products/product-detail/product-detail.component')
      .then(m => m.ProductDetailComponent),
  },
  {
    path: 'auth',
    canActivate: [guestGuard],
    children: [
      {
        path: 'login',
        loadComponent: () => import('./features/auth/login/login.component')
          .then(m => m.LoginComponent),
      },
      {
        path: 'register',
        loadComponent: () => import('./features/auth/register/register.component')
          .then(m => m.RegisterComponent),
      },
    ],
  },
  {
    path: 'cart',
    canActivate: [authGuard],
    loadComponent: () => import('./features/cart/cart.component')
      .then(m => m.CartComponent),
  },
  {
    path: 'checkout',
    canActivate: [authGuard],
    loadComponent: () => import('./features/checkout/checkout.component')
      .then(m => m.CheckoutComponent),
  },
  {
    path: 'orders',
    canActivate: [authGuard],
    children: [
      {
        path: '',
        loadComponent: () => import('./features/orders/order-list/order-list.component')
          .then(m => m.OrderListComponent),
      },
      {
        path: ':orderNumber',
        loadComponent: () => import('./features/orders/order-detail/order-detail.component')
          .then(m => m.OrderDetailComponent),
      },
    ],
  },
  {
    path: 'profile',
    canActivate: [authGuard],
    loadComponent: () => import('./features/profile/profile.component')
      .then(m => m.ProfileComponent),
  },
  {
    path: 'admin',
    canActivate: [authGuard, adminGuard],
    loadComponent: () => import('./features/admin/admin-dashboard/admin-dashboard.component')
      .then(m => m.AdminDashboardComponent),
  },
  { path: '**', redirectTo: '' },
];
