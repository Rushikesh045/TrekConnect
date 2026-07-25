import { Injectable, signal } from '@angular/core';

export interface ToastMessage {
  id: string;
  type: 'success' | 'error' | 'info' | 'warning';
  title: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  public toasts = signal<ToastMessage[]>([]);

  showSuccess(message: string, title = 'Success'): void {
    this.addToast('success', title, message);
  }

  showError(message: string, title = 'Error'): void {
    this.addToast('error', title, message);
  }

  showInfo(message: string, title = 'Notice'): void {
    this.addToast('info', title, message);
  }

  showWarning(message: string, title = 'Warning'): void {
    this.addToast('warning', title, message);
  }

  remove(id: string): void {
    this.toasts.update(current => current.filter(t => t.id !== id));
  }

  private addToast(type: ToastMessage['type'], title: string, message: string): void {
    const id = Math.random().toString(36).substring(2, 9);
    const toast: ToastMessage = { id, type, title, message };
    this.toasts.update(current => [...current, toast]);

    // Auto dismiss after 5 seconds
    setTimeout(() => {
      this.remove(id);
    }, 5000);
  }
}
