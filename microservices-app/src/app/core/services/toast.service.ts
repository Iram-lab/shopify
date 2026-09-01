import { Injectable, signal } from '@angular/core';
import { Toast } from '../models/models';

@Injectable({ providedIn: 'root' })
export class ToastService {
  toasts = signal<Toast[]>([]);

  show(message: string, type: Toast['type'] = 'info'): void {
    const id = Math.random().toString(36).substring(2);
    this.toasts.update(t => [...t, { id, message, type }]);
    setTimeout(() => this.remove(id), 4000);
  }

  success(message: string): void { this.show(message, 'success'); }
  error(message: string): void   { this.show(message, 'error'); }
  info(message: string): void    { this.show(message, 'info'); }
  warning(message: string): void { this.show(message, 'warning'); }

  remove(id: string): void {
    this.toasts.update(t => t.filter(toast => toast.id !== id));
  }
}
