import { Component, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService } from '../../../core/services/notification.service';

@Component({
  selector: 'app-toast-notification',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast-notification.component.html',
  styleUrl: './toast-notification.component.scss'
})
export class ToastNotificationComponent {
  notificationService = inject(NotificationService);

  get toasts() {
    return this.notificationService.toasts();
  }

  close(id: string): void {
    this.notificationService.remove(id);
  }
}
