import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-skeleton',
  standalone: true,
  imports: [CommonModule],
  template: `
    @if (type === 'product-card') {
      <div class="bg-white dark:bg-gray-800 rounded-xl overflow-hidden shadow">
        <div class="skeleton h-52 w-full"></div>
        <div class="p-4 space-y-3">
          <div class="skeleton h-4 w-3/4"></div>
          <div class="skeleton h-3 w-1/2"></div>
          <div class="skeleton h-6 w-1/3"></div>
          <div class="skeleton h-9 w-full rounded-lg"></div>
        </div>
      </div>
    }
    @if (type === 'product-detail') {
      <div class="grid grid-cols-1 md:grid-cols-2 gap-8">
        <div class="skeleton h-96 rounded-xl"></div>
        <div class="space-y-4">
          <div class="skeleton h-8 w-3/4"></div>
          <div class="skeleton h-4 w-1/2"></div>
          <div class="skeleton h-10 w-1/3"></div>
          <div class="skeleton h-24 w-full"></div>
          <div class="skeleton h-12 w-full rounded-lg"></div>
        </div>
      </div>
    }
    @if (type === 'list-item') {
      <div class="flex gap-4 p-4 bg-white dark:bg-gray-800 rounded-xl shadow">
        <div class="skeleton h-20 w-20 rounded-lg flex-shrink-0"></div>
        <div class="flex-1 space-y-2">
          <div class="skeleton h-4 w-3/4"></div>
          <div class="skeleton h-3 w-1/2"></div>
          <div class="skeleton h-5 w-1/4"></div>
        </div>
      </div>
    }
  `
})
export class SkeletonComponent {
  @Input() type: 'product-card' | 'product-detail' | 'list-item' = 'product-card';
}
