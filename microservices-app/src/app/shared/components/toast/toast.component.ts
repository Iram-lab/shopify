import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { ToastService } from '../../../core/services/toast.service';
import { trigger, transition, style, animate } from '@angular/animations';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule, MatIconModule],
  animations: [
    trigger('slideIn', [
      transition(':enter', [
        style({ transform: 'translateX(100%)', opacity: 0 }),
        animate('300ms ease-out', style({ transform: 'translateX(0)', opacity: 1 }))
      ]),
      transition(':leave', [
        animate('200ms ease-in', style({ transform: 'translateX(100%)', opacity: 0 }))
      ])
    ])
  ],
  template: `
    <div class="fixed top-4 right-4 z-[9999] flex flex-col gap-2 max-w-sm w-full">
      @for (toast of toastService.toasts(); track toast.id) {
        <div @slideIn
             [class]="getToastClass(toast.type)"
             class="flex items-center gap-3 px-4 py-3 rounded-lg shadow-lg cursor-pointer"
             (click)="toastService.remove(toast.id)">
          <mat-icon class="text-lg flex-shrink-0">{{ getIcon(toast.type) }}</mat-icon>
          <span class="text-sm font-medium flex-1">{{ toast.message }}</span>
          <mat-icon class="text-sm opacity-70">close</mat-icon>
        </div>
      }
    </div>
  `
})
export class ToastComponent {
  toastService = inject(ToastService);

  getToastClass(type: string): string {
    const classes: Record<string, string> = {
      success: 'bg-green-500 text-white',
      error:   'bg-red-500 text-white',
      warning: 'bg-yellow-500 text-white',
      info:    'bg-blue-500 text-white',
    };
    return classes[type] ?? classes['info'];
  }

  getIcon(type: string): string {
    const icons: Record<string, string> = {
      success: 'check_circle',
      error:   'error',
      warning: 'warning',
      info:    'info',
    };
    return icons[type] ?? 'info';
  }
}
