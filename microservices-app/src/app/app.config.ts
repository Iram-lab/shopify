import { ApplicationConfig, provideBrowserGlobalErrorListeners, isDevMode } from '@angular/core';
import { provideRouter, withComponentInputBinding, withViewTransitions } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { provideAnimationsAsync } from '@angular/platform-browser/animations/async';
import { provideStore } from '@ngrx/store';
import { provideEffects } from '@ngrx/effects';
import { provideStoreDevtools } from '@ngrx/store-devtools';
import { routes } from './app.routes';
import { jwtInterceptor } from './core/interceptors/jwt.interceptor';
import { authReducer } from './store/auth/auth.store';
import { cartReducer } from './store/cart/cart.store';
import { AuthEffects } from './store/auth/auth.store';
import { CartEffects } from './store/cart/cart.store';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes, withComponentInputBinding(), withViewTransitions()),
    provideHttpClient(withInterceptors([jwtInterceptor])),
    provideAnimationsAsync(),
    provideStore({
      auth: authReducer,
      cart: cartReducer,
    }),
    provideEffects([AuthEffects, CartEffects]),
    provideStoreDevtools({
      maxAge: 25,
      logOnly: !isDevMode(),
      connectInZone: true,
    }),
  ],
};
