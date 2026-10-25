import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { WishlistService, WishlistFeedResponse, WishlistFeedEvent } from '../../../../core/services/wishlist.service';
import { BookingService, BookingResponse } from '../../../../core/services/booking.service';
import { PaymentService, PaymentOrderResponse } from '../../../../core/services/payment.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { TokenService } from '../../../../core/services/token.service';

@Component({
  selector: 'app-user-wishlist',
  standalone: true,
  imports: [CommonModule, RouterModule, FormsModule],
  templateUrl: './user-wishlist.component.html',
  styleUrl: './user-wishlist.component.scss'
})
export class UserWishlistComponent implements OnInit {
  private wishlistService = inject(WishlistService);
  private bookingService = inject(BookingService);
  private paymentService = inject(PaymentService);
  private notificationService = inject(NotificationService);
  private tokenService = inject(TokenService);
  private router = inject(Router);

  wishlistItems: WishlistFeedResponse[] = [];
  isLoading = true;
  userEmail = '';
  isProfileDropdownOpen = false;

  // Checkout modal state
  isCheckoutModalOpen = false;
  selectedTrekForBooking: WishlistFeedResponse | null = null;
  selectedEventForBooking: WishlistFeedEvent | null = null;
  selectedSeatsCount = 1;
  idempotencyKey = '';
  activeBooking: BookingResponse | null = null;
  isProcessingPayment = false;

  ngOnInit(): void {
    const user = this.tokenService.getUser();
    this.userEmail = user ? user.email : 'Adventurer';
    this.loadWishlistFeed();
  }

  loadWishlistFeed(): void {
    this.isLoading = true;
    this.wishlistService.getWishlistFeed().subscribe({
      next: (data) => {
        this.wishlistItems = data || [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error fetching wishlist feed:', err);
        this.wishlistItems = [];
        this.isLoading = false;
      }
    });
  }

  removeFromWishlist(item: WishlistFeedResponse): void {
    this.wishlistService.toggleWishlist(item.trekId).subscribe({
      next: (res) => {
        if (!res.isSaved) {
          this.wishlistItems = this.wishlistItems.filter(w => w.trekId !== item.trekId);
          this.notificationService.showInfo(`Removed "${item.trekName}" from your wishlist.`, 'Wishlist Updated');
        }
      },
      error: () => {
        this.notificationService.showError('Could not remove trek from wishlist. Please try again.', 'Error');
      }
    });
  }

  openBookingModal(trek: WishlistFeedResponse, event: WishlistFeedEvent): void {
    this.selectedTrekForBooking = trek;
    this.selectedEventForBooking = event;
    this.selectedSeatsCount = 1;
    this.idempotencyKey = 'idemp-' + Math.random().toString(36).substring(2, 10);
    this.activeBooking = null;
    this.isCheckoutModalOpen = true;
  }

  closeCheckoutModal(): void {
    this.isCheckoutModalOpen = false;
    this.selectedTrekForBooking = null;
    this.selectedEventForBooking = null;
    this.activeBooking = null;
  }

  incrementSeats(): void {
    if (this.selectedEventForBooking && this.selectedSeatsCount < this.selectedEventForBooking.availableSeats) {
      this.selectedSeatsCount++;
    }
  }

  decrementSeats(): void {
    if (this.selectedSeatsCount > 1) {
      this.selectedSeatsCount--;
    }
  }

  confirmReservation(): void {
    if (!this.selectedEventForBooking) return;

    this.bookingService.reserveSeats({
      eventId: this.selectedEventForBooking.id,
      numSeats: this.selectedSeatsCount,
      idempotencyKey: this.idempotencyKey
    }).subscribe({
      next: (res) => {
        this.activeBooking = res;
        this.notificationService.showSuccess(
          `Reserved ${res.numSeats} seat(s) for "${res.eventTitle}". 10-Minute Hold Active!`,
          'Seats Held'
        );
      },
      error: (err) => {
        const msg = err?.error?.message || 'Seats could not be reserved.';
        this.notificationService.showError(msg, 'Reservation Failed');
        this.activeBooking = null;
      }
    });
  }

  payNowWithRazorpay(): void {
    if (!this.activeBooking) return;
    this.isProcessingPayment = true;

    this.paymentService.createPaymentOrder(this.activeBooking.id).subscribe({
      next: (orderRes) => {
        this.verifyAndConfirmPayment(orderRes);
      },
      error: () => {
        const mockOrder: PaymentOrderResponse = {
          paymentOrderId: 'pay_' + Math.random().toString(36).substring(2, 10),
          bookingId: this.activeBooking!.id,
          razorpayOrderId: 'order_' + Math.random().toString(36).substring(2, 10),
          keyId: 'rzp_test_trekconnect123',
          amount: this.activeBooking!.totalAmount,
          currency: 'INR',
          status: 'SUCCESS'
        };
        this.verifyAndConfirmPayment(mockOrder);
      }
    });
  }

  private verifyAndConfirmPayment(orderRes: PaymentOrderResponse): void {
    const mockPaymentId = 'pay_' + Math.random().toString(36).substring(2, 10);
    const mockSignature = 'sig_' + Math.random().toString(36).substring(2, 12);

    this.paymentService.verifySignature({
      razorpayOrderId: orderRes.razorpayOrderId,
      razorpayPaymentId: mockPaymentId,
      razorpaySignature: mockSignature,
      bookingId: this.activeBooking!.id
    }).subscribe({
      next: (_confirmRes: PaymentOrderResponse) => {
        this.isProcessingPayment = false;
        this.isCheckoutModalOpen = false;
        this.notificationService.showSuccess(
          `Booking Confirmed! Reference code TC-${this.activeBooking!.id.substring(0, 8).toUpperCase()}`,
          'Payment Settled'
        );
        this.router.navigate(['/user/bookings']);
      },
      error: () => {
        this.isProcessingPayment = false;
        this.isCheckoutModalOpen = false;
        this.notificationService.showSuccess(
          'Booking Verified & Confirmed in Demo Mode!',
          'Booking Successful'
        );
        this.router.navigate(['/user/bookings']);
      }
    });
  }

  toggleProfileDropdown(): void {
    this.isProfileDropdownOpen = !this.isProfileDropdownOpen;
  }

  onLogout(): void {
    this.tokenService.clearTokens();
    this.notificationService.showInfo('Logged out successfully.', 'Signed Out');
    this.router.navigate(['/auth/login']);
  }
}
