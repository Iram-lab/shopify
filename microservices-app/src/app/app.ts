import { Component, inject, OnInit } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Store } from '@ngrx/store';
import { NavbarComponent } from './shared/components/navbar/navbar.component';
import { ToastComponent } from './shared/components/toast/toast.component';
import { CartActions } from './store/cart/cart.store';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, NavbarComponent, ToastComponent],
  templateUrl: './app.html',
})
export class App implements OnInit {
  private store = inject(Store);
  private authService = inject(AuthService);

  ngOnInit(): void {
    if (this.authService.isLoggedIn()) {
      this.store.dispatch(CartActions.loadCart());
    }
  }
}
