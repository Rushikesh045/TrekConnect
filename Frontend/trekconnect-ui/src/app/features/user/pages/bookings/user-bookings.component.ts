import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { BookingService, BookingResponse } from '../../../../core/services/booking.service';
import { TokenService } from '../../../../core/services/token.service';
import { NotificationService } from '../../../../core/services/notification.service';

/**
 * User Trek Bookings Page Component.
 * 
 * WHY THIS COMPONENT WAS CREATED:
 * Displays active trekker bookings, digital ticket QR codes, seat lock expiration timers,
 * and allows users to cancel reservations before departure.
 */
@Component({
  selector: 'app-user-bookings',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './user-bookings.component.html',
  styleUrl: './user-bookings.component.scss'
})
export class UserBookingsComponent implements OnInit {
  private bookingService = inject(BookingService);
  private tokenService = inject(TokenService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);

  isLoading = true;
  userEmail = '';
  bookings: BookingResponse[] = [];

  // Ticket Pass Modal State
  selectedBookingForPass: BookingResponse | null = null;
  isPassModalOpen = false;

  ngOnInit(): void {
    const user = this.tokenService.getUser();
    this.userEmail = user?.email || 'trekker@trekconnect.com';
    this.loadMyBookings();
  }

  loadMyBookings(): void {
    this.isLoading = true;
    this.bookingService.getMyBookings().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.bookings = res || [];
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error loading my bookings', err);
        this.bookings = [];
      }
    });
  }

  getQrCodeUrl(dataString: string): string {
    if (!dataString) return 'assets/images/qr-code.svg';
    return `https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${encodeURIComponent(dataString)}`;
  }

  openTicketPassModal(booking: BookingResponse): void {
    this.selectedBookingForPass = booking;
    this.isPassModalOpen = true;
  }

  closeTicketPassModal(): void {
    this.isPassModalOpen = false;
    this.selectedBookingForPass = null;
  }

  cancelBooking(booking: BookingResponse): void {
    const isPending = booking.status === 'PENDING_PAYMENT';
    this.bookingService.cancelBooking(booking.id).subscribe({
      next: (res) => {
        if (isPending || res?.status === 'DELETED') {
          // Remove deleted seat-hold reservation completely from the user view
          this.bookings = this.bookings.filter(b => b.id !== booking.id);
          this.notificationService.showInfo('Seat hold cancelled. Your reserved slot has been released.', 'Hold Released');
        } else {
          booking.status = 'CANCELLED';
          this.notificationService.showInfo('Booking cancelled. Reserved seats have been released.', 'Booking Cancelled');
        }
      },
      error: (err) => {
        const msg = err?.error?.message || 'Could not cancel this booking. Please note confirmed bookings can only be cancelled up to 48 hours prior to departure.';
        this.notificationService.showError(msg, 'Cancellation Failed');
      }
    });
  }

  isCancellationAllowed(booking: BookingResponse): boolean {
    if (booking.status === 'PENDING_PAYMENT') return true;
    if (booking.status !== 'CONFIRMED') return false;
    if (!booking.eventDate) return true;

    const departure = new Date(booking.eventDate);
    const now = new Date();
    const diffHours = (departure.getTime() - now.getTime()) / (1000 * 60 * 60);
    return diffHours >= 48;
  }

  getStatusLabel(status: string): string {
    if (status === 'EXPIRED' || status === 'FAILED' || status === 'PAYMENT_FAILED') {
      return 'FAILED';
    }
    if (status === 'PENDING_PAYMENT') {
      return 'PENDING PAYMENT';
    }
    return status;
  }

  downloadDigitalPass(booking: BookingResponse): void {
    this.notificationService.showInfo(`Generating Official Digital Pass for ${booking.eventTitle}...`, 'Pass Download');
    this.bookingService.downloadDigitalPass(booking.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `TrekConnect_Pass_${booking.id.substring(0, Math.min(8, booking.id.length)).toUpperCase()}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        this.notificationService.showSuccess('Official Digital Boarding Pass downloaded!', 'Download Complete');
      },
      error: (err) => {
        console.error('Error downloading pass', err);
        this.notificationService.showError('Could not download digital pass. Please ensure booking is confirmed.', 'Download Failed');
      }
    });
  }

  downloadReceipt(booking: BookingResponse): void {
    this.notificationService.showInfo(`Generating PDF receipt for booking ${booking.id.substring(0, 8).toUpperCase()}...`, 'Receipt Download');
    this.bookingService.downloadReceipt(booking.id).subscribe({
      next: (blob) => {
        const url = window.URL.createObjectURL(blob);
        const a = document.createElement('a');
        a.href = url;
        a.download = `TrekConnect_Receipt_${booking.id}.pdf`;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        window.URL.revokeObjectURL(url);
        this.notificationService.showSuccess('Official PDF Receipt downloaded!', 'Download Complete');
      },
      error: (err) => {
        console.error('Error downloading receipt', err);
        this.notificationService.showError('Could not download receipt. Please verify booking is confirmed.', 'Download Failed');
      }
    });
  }

  onLogout(): void {
    this.tokenService.clearTokens();
    this.notificationService.showInfo('Logged out successfully.', 'Signed Out');
    this.router.navigate(['/auth/login']);
  }
}
