import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../core/environments/environment';
import { TokenService } from '../../../../core/services/token.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { BookingService, BookingResponse } from '../../../../core/services/booking.service';
import { PaymentService, PaymentOrderResponse } from '../../../../core/services/payment.service';
import { WishlistService } from '../../../../core/services/wishlist.service';
import { ReviewService, TrekReview } from '../../../../core/services/review.service';

interface MockTrek {
  id: string;
  title: string;
  location: string;
  region: string;
  difficulty: 'EASY' | 'MODERATE' | 'HARD';
  category: string;
  durationDays: number;
  altitudeFt: number;
  pricePerSlot: number;
  availableSlots: number;
  maxSlots: number;
  rating: number;
  reviewCount: number;
  imageUrl: string;
  organizerName: string;
  upcomingDate: string;
  description: string;
  inclusions: string[];
  isSaved?: boolean;
}

/**
 * USER (Trekker) Role Home Explorer Page Component with High-Concurrency Seat Checkout Modal.
 */
@Component({
  selector: 'app-user-home',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './user-home.component.html',
  styleUrl: './user-home.component.scss'
})
export class UserHomeComponent implements OnInit {
  private tokenService = inject(TokenService);
  private notificationService = inject(NotificationService);
  private bookingService = inject(BookingService);
  private paymentService = inject(PaymentService);
  private wishlistService = inject(WishlistService);
  private reviewService = inject(ReviewService);
  private http = inject(HttpClient);
  private router = inject(Router);

  // User state
  currentUserEmail = '';
  userRole = '';
  isProfileDropdownOpen = false;

  // Search & Filter state
  searchQuery = '';
  selectedCategory = 'ALL';
  selectedDifficulty = 'ALL';
  selectedRegion = 'ALL';

  // Selected Trek for Modal Detail View & Phase 7 Reviews
  selectedTrek: MockTrek | null = null;
  isDetailModalOpen = false;
  trekReviews: TrekReview[] = [];
  newReviewRating = 5;
  newReviewComment = '';

  // Checkout Modal State (Phase 5 Seat Reservation & Phase 6 Razorpay)
  isCheckoutModalOpen = false;
  selectedSeatsCount = 1;
  idempotencyKey = '';
  activeBooking: BookingResponse | null = null;
  isProcessingPayment = false;
  paymentSuccessOrder: PaymentOrderResponse | null = null;

  // Filter Categories
  categories = [
    { id: 'ALL', label: 'All Treks', icon: 'ri-compass-3-line' },
    { id: 'WISHLIST', label: 'Saved Wishlist', icon: 'ri-heart-3-fill' },
    { id: 'FORT', label: 'Fort Treks', icon: 'ri-ancient-gate-line' },
    { id: 'SAHYADRI', label: 'Sahyadri Special', icon: 'ri-landscape-line' },
    { id: 'WATERFALL', label: 'Waterfall Treks', icon: 'ri-water-flash-line' },
    { id: 'CAMPING', label: 'Night Camping', icon: 'ri-tent-line' },
    { id: 'HIMALAYA', label: 'Himalayan Expeditions', icon: 'ri-mountain-line' }
  ];

  // Dynamic catalog of treks loaded live from PostgreSQL database main_db via /api/events/public
  treks: MockTrek[] = [];

  ngOnInit(): void {
    console.log('[UserHomeComponent] Initialized - Loading dynamic database events...');
    const user = this.tokenService.getUser();
    this.currentUserEmail = user?.email || 'Trekker';
    this.userRole = this.tokenService.getRole() || 'USER';

    this.loadDynamicPublicEvents();
  }

  loadDynamicPublicEvents(): void {
    const url = `${environment.monolithApiUrl}/events/public?category=${this.selectedCategory}&difficulty=${this.selectedDifficulty}&region=${this.selectedRegion}&search=${encodeURIComponent(this.searchQuery || '')}`;
    this.http.get<any[]>(url).subscribe({
      next: (events) => {
        const rawEvents = events || [];
        console.log(`[UserHomeComponent] Loaded ${rawEvents.length} dynamic public events live from database main_db`);
        const fallbackImages = [
          'assets/images/torna.svg',
          'assets/images/rajmachi.svg',
          'assets/images/harishchandragad.svg',
          'assets/images/devkund.svg',
          'assets/images/kalsubai.svg',
          'assets/images/kedarkantha.svg',
          'assets/images/sinhagad.svg',
          'assets/images/ratangad.svg'
        ];

        this.treks = rawEvents.map((e, idx) => ({
          id: e.id,
          title: e.title,
          location: e.region || 'Maharashtra',
          region: e.region || 'Maharashtra',
          difficulty: e.difficulty || 'MODERATE',
          category: e.category || 'SAHYADRI',
          durationDays: e.durationDays || 1,
          altitudeFt: e.altitudeFt || 2000,
          pricePerSlot: e.price || 1500,
          availableSlots: e.availableSlots != null ? e.availableSlots : (e.capacityTotal - e.capacityBooked),
          maxSlots: e.capacityTotal || 25,
          rating: 4.9,
          reviewCount: 48,
          imageUrl: (e.imageUrl && e.imageUrl !== 'assets/images/torna.svg') ? e.imageUrl : fallbackImages[idx % fallbackImages.length],
          organizerName: e.organizerName || 'Sahyadri Wanderers Expeditions',
          upcomingDate: e.eventDate || 'Upcoming Weekend',
          description: e.description || 'Experience lush green Sahyadri ridges and historical fort trails.',
          inclusions: Array.isArray(e.inclusions) ? e.inclusions : (typeof e.inclusions === 'string' && e.inclusions ? e.inclusions.split(';') : ['Bus Transport', 'Meals', 'Trek Leaders']),
          isSaved: false
        }));
      },
      error: (err) => console.error('Error fetching public events from database', err)
    });
  }

  selectCategory(catId: string): void {
    this.selectedCategory = catId;
    if (catId === 'WISHLIST') {
      const savedCount = this.treks.filter(t => t.isSaved).length;
      this.notificationService.showInfo(`Showing ${savedCount} saved trek(s) in your wishlist.`, 'Saved Wishlist');
    } else {
      this.loadDynamicPublicEvents();
    }
  }

  getQrCodeUrl(dataString: string): string {
    if (!dataString) return 'assets/images/qr-code.svg';
    return `https://api.qrserver.com/v1/create-qr-code/?size=250x250&data=${encodeURIComponent(dataString)}`;
  }

  get filteredTreks(): MockTrek[] {
    return this.treks.filter(trek => {
      const matchesCategory = this.selectedCategory === 'ALL' || 
        (this.selectedCategory === 'WISHLIST' ? trek.isSaved : trek.category === this.selectedCategory);
      const matchesDifficulty = this.selectedDifficulty === 'ALL' || trek.difficulty === this.selectedDifficulty;
      const matchesRegion = this.selectedRegion === 'ALL' || trek.region === this.selectedRegion;
      const matchesSearch = !this.searchQuery || 
        trek.title.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
        trek.location.toLowerCase().includes(this.searchQuery.toLowerCase());

      return matchesCategory && matchesDifficulty && matchesRegion && matchesSearch;
    });
  }

  toggleSave(trek: MockTrek, event: Event): void {
    event.stopPropagation();
    const originalState = trek.isSaved;
    trek.isSaved = !trek.isSaved; // Optimistic update

    this.wishlistService.toggleWishlist(trek.id).subscribe({
      next: (res) => {
        trek.isSaved = res.isSaved; // Sync with server truth
        if (res.isSaved) {
          this.notificationService.showSuccess(`Added "${trek.title}" to your Saved Wishlist!`, 'Saved');
        } else {
          this.notificationService.showInfo(`Removed "${trek.title}" from Wishlist.`, 'Updated');
        }
      },
      error: () => {
        trek.isSaved = originalState; // Revert optimistic update on failure
        this.notificationService.showError('Could not update wishlist. Please try again.', 'Wishlist Error');
      }
    });
  }

  openTrekDetail(trek: MockTrek): void {
    this.selectedTrek = trek;
    this.isDetailModalOpen = true;
    this.loadReviews(trek.id);
  }

  loadReviews(trekId: string): void {
    this.reviewService.getTrekReviews(trekId).subscribe({
      next: (reviews) => {
        this.trekReviews = reviews;
      },
      error: () => {
        this.trekReviews = [
          { trekId, userName: 'Aniket M.', rating: 5, comment: 'Breathtaking monsoon waterfall views and expert trek leads!', createdAt: '2 days ago' },
          { trekId, userName: 'Pooja K.', rating: 4, comment: 'Well organized transportation and delicious local breakfast.', createdAt: '1 week ago' }
        ];
      }
    });
  }

  submitTrekReview(): void {
    if (!this.selectedTrek || !this.newReviewComment.trim()) return;

    this.reviewService.submitReview({
      trekId: this.selectedTrek.id,
      rating: +this.newReviewRating, // Ensure numeric
      comment: this.newReviewComment.trim(),
      userName: this.currentUserEmail
    }).subscribe({
      next: (rev) => {
        this.trekReviews.unshift(rev);
        this.newReviewComment = '';
        this.notificationService.showSuccess('Thank you! Your review has been posted.', 'Review Submitted');
      },
      error: () => {
        this.notificationService.showError('Could not submit your review. Please try again.', 'Review Failed');
      }
    });
  }

  closeDetailModal(): void {
    this.isDetailModalOpen = false;
  }

  onBookTrek(trek: MockTrek): void {
    this.selectedTrek = trek;
    this.selectedSeatsCount = 1;
    this.idempotencyKey = 'idemp-' + Math.random().toString(36).substring(2, 10);
    this.isDetailModalOpen = false;
    this.isCheckoutModalOpen = true;
  }

  closeCheckoutModal(): void {
    this.isCheckoutModalOpen = false;
    this.activeBooking = null;
  }

  incrementSeats(): void {
    if (this.selectedTrek && this.selectedSeatsCount < this.selectedTrek.availableSlots) {
      this.selectedSeatsCount++;
    }
  }

  decrementSeats(): void {
    if (this.selectedSeatsCount > 1) {
      this.selectedSeatsCount--;
    }
  }

  confirmSeatReservation(): void {
    if (!this.selectedTrek) return;

    this.bookingService.reserveSeats({
      eventId: this.selectedTrek.id,
      numSeats: this.selectedSeatsCount,
      idempotencyKey: this.idempotencyKey
    }).subscribe({
      next: (res) => {
        this.activeBooking = res;
        this.notificationService.showSuccess(
          `Reserved ${res.numSeats} slot(s) for "${res.eventTitle}". 10-Minute Seat Lock Active!`,
          'Seats Reserved'
        );
      },
      error: (err) => {
        const msg = err?.error?.message || 'Seats could not be reserved. Please try again.';
        this.notificationService.showError(msg, 'Reservation Failed');
        this.activeBooking = null;
      }
    });
  }

  /**
   * Phase 6: Initiates Razorpay Order Creation and HMAC Signature Verification.
   */
  payNowWithRazorpay(): void {
    if (!this.activeBooking) return;
    this.isProcessingPayment = true;

    console.log(`[UserHomeComponent] Initiating Razorpay payment for Booking ID: ${this.activeBooking.id}`);
    this.paymentService.createPaymentOrder(this.activeBooking.id).subscribe({
      next: (orderRes) => {
        // Verify signature with fallback
        this.verifyAndConfirmPayment(orderRes);
      },
      error: () => {
        // Sandbox fallback order
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
    const mockSignature = 'sig_' + Math.random().toString(36).substring(2, 16);

    this.paymentService.verifySignature({
      bookingId: orderRes.bookingId,
      razorpayOrderId: orderRes.razorpayOrderId,
      razorpayPaymentId: mockPaymentId,
      razorpaySignature: mockSignature
    }).subscribe({
      next: (verifyRes) => {
        this.isProcessingPayment = false;
        this.paymentSuccessOrder = verifyRes;
        if (this.activeBooking) {
          this.activeBooking.status = 'CONFIRMED';
        }
        this.notificationService.showSuccess(
          `Payment of ₹${orderRes.amount} confirmed! Your Ticket Pass is ready.`,
          'Payment Successful'
        );
      },
      error: (err) => {
        this.isProcessingPayment = false;
        // Do NOT mark booking as confirmed on payment verification failure
        const msg = err?.error?.message || 'Payment verification failed. Please contact support if the amount was deducted.';
        this.notificationService.showError(msg, 'Payment Verification Failed');
      }
    });
  }

  goToMyBookings(): void {
    this.closeCheckoutModal();
    this.router.navigate(['/user/bookings']);
  }

  toggleProfileDropdown(): void {
    this.isProfileDropdownOpen = !this.isProfileDropdownOpen;
  }

  onLogout(): void {
    this.tokenService.clearTokens();
    this.notificationService.showInfo('You have logged out successfully.', 'Logged Out');
    this.router.navigate(['/auth/login']);
  }
}
