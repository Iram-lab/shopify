// Auth Models
export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  email: string;
  role: string;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

// Product Models
export interface Category {
  id: number;
  name: string;
  description: string;
}

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  imageUrl: string;
  brand: string;
  active: boolean;
  category: Category;
  createdAt: string;
}

export interface ProductSummary {
  id: number;
  name: string;
  price: number;
  imageUrl: string;
  brand: string;
  categoryName: string;
}

export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface ProductSearchParams {
  keyword?: string;
  categoryId?: number;
  minPrice?: number;
  maxPrice?: number;
  page?: number;
  size?: number;
}

// Cart Models
export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  unitPrice: number;
  imageUrl: string;
  quantity: number;
  subtotal: number;
}

export interface Cart {
  id: number;
  userEmail: string;
  items: CartItem[];
  totalItems: number;
  totalPrice: number;
  updatedAt: string;
}

export interface AddToCartRequest {
  productId: number;
  quantity: number;
}

// Order Models
export type OrderStatus = 'PENDING' | 'CONFIRMED' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface OrderItem {
  productId: number;
  productName: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export interface Order {
  id: number;
  orderNumber: string;
  userEmail: string;
  status: OrderStatus;
  totalAmount: number;
  shippingAddress: string;
  paymentId: string;
  razorpayOrderId: string;
  razorpayKeyId: string;
  items: OrderItem[];
  createdAt: string;
  updatedAt: string;
}

export interface PlaceOrderRequest {
  shippingAddress: string;
}

// Payment Models
export type PaymentStatus = 'PENDING' | 'SUCCESS' | 'FAILED' | 'REFUNDED';

export interface Payment {
  id: number;
  paymentId: string;
  orderNumber: string;
  userEmail: string;
  amount: number;
  status: PaymentStatus;
  transactionRef: string;
  failureReason: string;
  createdAt: string;
}

// UI Models
export interface Toast {
  id: string;
  message: string;
  type: 'success' | 'error' | 'info' | 'warning';
}

export interface User {
  email: string;
  role: string;
}
