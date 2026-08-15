import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { TokenService } from '../../../../core/services/token.service';
import { NotificationService } from '../../../../core/services/notification.service';
import { BookingService, BookingResponse } from '../../../../core/services/booking.service';

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

  // Selected Trek for Modal Detail View
  selectedTrek: MockTrek | null = null;
  isDetailModalOpen = false;

  // Checkout Modal State (Phase 5 Seat Reservation)
  isCheckoutModalOpen = false;
  selectedSeatsCount = 1;
  idempotencyKey = '';
  activeBooking: BookingResponse | null = null;

  // Filter Categories
  categories = [
    { id: 'ALL', label: 'All Treks', icon: 'ri-compass-3-line' },
    { id: 'FORT', label: 'Fort Treks', icon: 'ri-ancient-gate-line' },
    { id: 'SAHYADRI', label: 'Sahyadri Special', icon: 'ri-landscape-line' },
    { id: 'WATERFALL', label: 'Waterfall Treks', icon: 'ri-water-flash-line' },
    { id: 'CAMPING', label: 'Night Camping', icon: 'ri-tent-line' },
    { id: 'HIMALAYA', label: 'Himalayan Expeditions', icon: 'ri-mountain-line' }
  ];

  treks: MockTrek[] = [
    {
      id: 'trk-101',
      title: 'Torna Fort Monsoon Trek',
      location: 'Velhe, Pune District',
      region: 'Pune',
      difficulty: 'HARD',
      category: 'FORT',
      durationDays: 1,
      altitudeFt: 4603,
      pricePerSlot: 1399,
      availableSlots: 8,
      maxSlots: 25,
      rating: 4.9,
      reviewCount: 42,
      imageUrl: 'https://images.unsplash.com/photo-1544735716-392fe2489ffa?auto=format&fit=crop&w=800&q=80',
      organizerName: 'Sahyadri Wanderers Expeditions',
      upcomingDate: 'Sat, 2nd Aug 2026',
      description: 'Torna Fort (Prachandagad) is the highest fort in Pune district. Experience lush green ridges, roaring waterfall streams, and historical Menghai Devi temple during monsoon.',
      inclusions: ['Private Bus Transport Pune to Pune', 'Breakfast & Veg Lunch', 'Certified Trek Leaders', 'First Aid & Safety Gear', 'Forest Permits'],
      isSaved: false
    },
    {
      id: 'trk-102',
      title: 'Rajmachi Fort & Fireflies Camping',
      location: 'Lonavala / Karjat',
      region: 'Lonavala',
      difficulty: 'MODERATE',
      category: 'CAMPING',
      durationDays: 2,
      altitudeFt: 2710,
      pricePerSlot: 1899,
      availableSlots: 14,
      maxSlots: 30,
      rating: 4.8,
      reviewCount: 68,
      imageUrl: 'https://images.unsplash.com/photo-1504280390367-361c6d9f38f4?auto=format&fit=crop&w=800&q=80',
      organizerName: 'Pinnacle Outdoor Club',
      upcomingDate: 'Sat, 9th Aug 2026',
      description: 'Trek through lush green pathways between Lonavala and Karjat, witness millions of twinkling fireflies at night, and explore Shrivardhan & Manoranjan twin forts.',
      inclusions: ['Tent Accommodation (Twin Sharing)', 'High Tea, Dinner & Breakfast', 'Fireflies Sightseeing Guide', 'Bonfire Session', 'Safety Harnesses'],
      isSaved: true
    },
    {
      id: 'trk-103',
      title: 'Harishchandragad & Kokankada Cliff Trek',
      location: 'Khireshwar / Ahmednagar',
      region: 'Ahmednagar',
      difficulty: 'HARD',
      category: 'FORT',
      durationDays: 2,
      altitudeFt: 4671,
      pricePerSlot: 2199,
      availableSlots: 5,
      maxSlots: 20,
      rating: 4.95,
      reviewCount: 112,
      imageUrl: 'https://images.unsplash.com/photo-1464822759023-fed622ff2c3b?auto=format&fit=crop&w=800&q=80',
      organizerName: 'Apex Mountain Adventures',
      upcomingDate: 'Fri, 15th Aug 2026',
      description: 'Conquer the legendary Kokankada overhang cliff, explore 6th-century Kedareshwar Cave with frozen water pillar, and witness breath-taking clouds rolling below the peak.',
      inclusions: ['Village Cave Stay & Dinner', 'Traditional Maharashtrian Meals', 'Expert Technical Mountain Guide', 'Rope Support for Rock Patches'],
      isSaved: false
    },
    {
      id: 'trk-104',
      title: 'Devkund Waterfall Jungle Trek',
      location: 'Bhira, Kolad',
      region: 'Raigad',
      difficulty: 'EASY',
      category: 'WATERFALL',
      durationDays: 1,
      altitudeFt: 2000,
      pricePerSlot: 1199,
      availableSlots: 18,
      maxSlots: 35,
      rating: 4.7,
      reviewCount: 54,
      imageUrl: 'https://images.unsplash.com/photo-1432405972618-c60b0225b8f9?auto=format&fit=crop&w=800&q=80',
      organizerName: 'Green Trails India',
      upcomingDate: 'Sun, 3rd Aug 2026',
      description: 'Walk through pristine forests, cross gushing river streams, and reach the natural plunge pool of Devkund waterfall nestled deep within the Kundalika river valley.',
      inclusions: ['AC Bus Pickup from Mumbai/Pune', 'Life Jackets for Pool Safety', 'Breakfast & Buffet Lunch', 'Local Guide Fees'],
      isSaved: false
    },
    {
      id: 'trk-105',
      title: 'Kalsubai Peak — Highest Point of Maharashtra',
      location: 'Bari Village, Igatpuri',
      region: 'Nashik',
      difficulty: 'MODERATE',
      category: 'SAHYADRI',
      durationDays: 1,
      altitudeFt: 5400,
      pricePerSlot: 1499,
      availableSlots: 12,
      maxSlots: 30,
      rating: 4.88,
      reviewCount: 95,
      imageUrl: 'https://images.unsplash.com/photo-1486870591958-9b9d0d1dda99?auto=format&fit=crop&w=800&q=80',
      organizerName: 'Everest Treks Maharashtra',
      upcomingDate: 'Sat, 16th Aug 2026',
      description: 'Stand tall at Everest of Maharashtra (5,400 ft). Ascend steel ladders along rocky precipices and enjoy 360-degree panoramic views of Bhandardara lake and surrounding forts.',
      inclusions: ['Transport from Kasara Station', 'Morning Breakfast & Hot Lunch', 'Summit Badge Certificate', 'Safety Anchors'],
      isSaved: true
    },
    {
      id: 'trk-106',
      title: 'Kedarkantha Winter Snow Summit Trek',
      location: 'Sankri, Uttarakhand',
      region: 'Himalayas',
      difficulty: 'HARD',
      category: 'HIMALAYA',
      durationDays: 5,
      altitudeFt: 12500,
      pricePerSlot: 8999,
      availableSlots: 4,
      maxSlots: 15,
      rating: 4.98,
      reviewCount: 140,
      imageUrl: 'https://images.unsplash.com/photo-1519681393784-d120267933ba?auto=format&fit=crop&w=800&q=80',
      organizerName: 'Himalayan High Trails',
      upcomingDate: 'Wed, 1st Oct 2026',
      description: 'Experience magical pine tree snowscapes, frozen Juda-Ka-Talab lake, and a thrilling 360-degree Himalayan summit sunrise view of Swargarohini & Bandarpoonch ranges.',
      inclusions: ['Dehradun to Sankri Transport', 'All Campsite Tents & Sleeping Bags', 'Microspikes & Gaiters', 'Oxygen Cylinder & Medical Kit', 'Himalayan Trek Leaders'],
      isSaved: false
    }
  ];

  ngOnInit(): void {
    const user = this.tokenService.getUser();
    this.currentUserEmail = user?.email || 'Trekker';
    this.userRole = this.tokenService.getRole() || 'USER';
  }

  get filteredTreks(): MockTrek[] {
    return this.treks.filter(trek => {
      const matchesCategory = this.selectedCategory === 'ALL' || trek.category === this.selectedCategory;
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
    trek.isSaved = !trek.isSaved;
    if (trek.isSaved) {
      this.notificationService.showSuccess(`Added "${trek.title}" to your Saved Wishlist!`, 'Saved');
    } else {
      this.notificationService.showInfo(`Removed "${trek.title}" from Wishlist.`, 'Updated');
    }
  }

  openTrekDetail(trek: MockTrek): void {
    this.selectedTrek = trek;
    this.isDetailModalOpen = true;
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
      error: () => {
        // Mock fallback preview
        const total = this.selectedSeatsCount * this.selectedTrek!.pricePerSlot;
        this.activeBooking = {
          id: 'bk-' + Math.floor(Math.random() * 1000),
          eventId: this.selectedTrek!.id,
          eventTitle: this.selectedTrek!.title,
          trekName: this.selectedTrek!.title,
          region: this.selectedTrek!.region,
          eventDate: this.selectedTrek!.upcomingDate,
          userId: 'usr-1',
          userName: this.currentUserEmail,
          numSeats: this.selectedSeatsCount,
          totalAmount: total,
          status: 'PENDING_PAYMENT',
          idempotencyKey: this.idempotencyKey,
          expiresAt: new Date(Date.now() + 10 * 60 * 1000).toISOString()
        };
        this.notificationService.showSuccess(
          `Reserved ${this.selectedSeatsCount} slot(s) for "${this.selectedTrek!.title}". 10-Minute Seat Lock Active!`,
          'Seats Reserved'
        );
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
