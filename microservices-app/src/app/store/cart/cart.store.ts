import { createAction, createReducer, createSelector, createFeatureSelector, on, props } from '@ngrx/store';
import { inject } from '@angular/core';
import { Actions, createEffect, ofType } from '@ngrx/effects';
import { catchError, exhaustMap, map, of, tap } from 'rxjs';
import { CartService } from '../../core/services/cart.service';
import { ToastService } from '../../core/services/toast.service';
import { AddToCartRequest, Cart } from '../../core/models/models';

// --- State ---
export interface CartState {
  cart: Cart | null;
  loading: boolean;
  error: string | null;
}

const initialState: CartState = { cart: null, loading: false, error: null };

// --- Actions ---
export const CartActions = {
  loadCart: createAction('[Cart] Load Cart'),
  loadCartSuccess: createAction('[Cart] Load Cart Success', props<{ cart: Cart }>()),
  loadCartFailure: createAction('[Cart] Load Cart Failure', props<{ error: string }>()),

  addItem: createAction('[Cart] Add Item', props<{ request: AddToCartRequest }>()),
  addItemSuccess: createAction('[Cart] Add Item Success', props<{ cart: Cart }>()),
  addItemFailure: createAction('[Cart] Add Item Failure', props<{ error: string }>()),

  removeItem: createAction('[Cart] Remove Item', props<{ productId: number }>()),
  removeItemSuccess: createAction('[Cart] Remove Item Success', props<{ cart: Cart }>()),

  updateItem: createAction('[Cart] Update Item', props<{ productId: number; quantity: number }>()),
  updateItemSuccess: createAction('[Cart] Update Item Success', props<{ cart: Cart }>()),

  clearCart: createAction('[Cart] Clear Cart'),
  clearCartSuccess: createAction('[Cart] Clear Cart Success'),
};

// --- Reducer ---
export const cartReducer = createReducer(
  initialState,
  on(CartActions.loadCart, CartActions.addItem, state => ({ ...state, loading: true })),
  on(CartActions.loadCartSuccess, CartActions.addItemSuccess,
     CartActions.removeItemSuccess, CartActions.updateItemSuccess,
    (state, { cart }) => ({ ...state, cart, loading: false })),
  on(CartActions.loadCartFailure, CartActions.addItemFailure,
    (state, { error }) => ({ ...state, error, loading: false })),
  on(CartActions.clearCartSuccess, state => ({ ...state, cart: null })),
);

// --- Selectors ---
export const selectCartState = createFeatureSelector<CartState>('cart');
export const selectCart = createSelector(selectCartState, s => s.cart);
export const selectCartItems = createSelector(selectCart, c => c?.items ?? []);
export const selectCartTotal = createSelector(selectCart, c => c?.totalPrice ?? 0);
export const selectCartItemCount = createSelector(selectCart, c => c?.totalItems ?? 0);
export const selectCartLoading = createSelector(selectCartState, s => s.loading);

// --- Effects ---
export class CartEffects {
  private actions$ = inject(Actions);
  private cartService = inject(CartService);
  private toastService = inject(ToastService);

  loadCart$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CartActions.loadCart),
      exhaustMap(() =>
        this.cartService.getCart().pipe(
          map(cart => CartActions.loadCartSuccess({ cart })),
          catchError(err => of(CartActions.loadCartFailure({ error: err.message })))
        )
      )
    )
  );

  addItem$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CartActions.addItem),
      exhaustMap(({ request }) =>
        this.cartService.addItem(request).pipe(
          map(cart => CartActions.addItemSuccess({ cart })),
          tap(() => this.toastService.success('Item added to cart!')),
          catchError(err => {
            this.toastService.error(err.error?.detail || 'Failed to add item');
            return of(CartActions.addItemFailure({ error: err.message }));
          })
        )
      )
    )
  );

  removeItem$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CartActions.removeItem),
      exhaustMap(({ productId }) =>
        this.cartService.removeItem(productId).pipe(
          map(cart => CartActions.removeItemSuccess({ cart })),
          tap(() => this.toastService.info('Item removed from cart'))
        )
      )
    )
  );

  updateItem$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CartActions.updateItem),
      exhaustMap(({ productId, quantity }) =>
        this.cartService.updateItem(productId, quantity).pipe(
          map(cart => CartActions.updateItemSuccess({ cart }))
        )
      )
    )
  );

  clearCart$ = createEffect(() =>
    this.actions$.pipe(
      ofType(CartActions.clearCart),
      exhaustMap(() =>
        this.cartService.clearCart().pipe(
          map(() => CartActions.clearCartSuccess()),
          catchError(() => of(CartActions.clearCartSuccess()))
        )
      )
    )
  );
}
