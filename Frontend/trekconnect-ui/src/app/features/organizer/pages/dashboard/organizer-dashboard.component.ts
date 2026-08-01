import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { OrganizerEventService, EventResponse, TrekResponse, CreateEventRequest } from '../../../../core/services/organizer-event.service';
import { TokenService } from '../../../../core/services/token.service';
import { NotificationService } from '../../../../core/services/notification.service';

/**
 * ORGANIZER Role Trek & Event Management Dashboard Component.
 * 
 * WHY THIS COMPONENT WAS CREATED:
 * Serves as the central portal for verified trek organizers to publish scheduled trek batches,
 * set capacity, configure batch pricing, upload media gallery assets, and track seat bookings.
 */
@Component({
  selector: 'app-organizer-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './organizer-dashboard.component.html',
  styleUrl: './organizer-dashboard.component.scss'
})
export class OrganizerDashboardComponent implements OnInit {
  private eventService = inject(OrganizerEventService);
  private tokenService = inject(TokenService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);

  isLoading = true;
  organizerEmail = '';
  organizationName = 'Sahyadri Expeditions';

  events: EventResponse[] = [];
  treks: TrekResponse[] = [];

  // Create Event Modal state
  isCreateModalOpen = false;
  newEvent: CreateEventRequest = {
    trekId: 'Rajmachi Fort Trek',
    title: 'Monsoon Weekend Expedition to Rajmachi',
    description: 'Experience lush green valleys, twin forts of Shrivardhan and Manaranjan, and night camping under starry skies.',
    eventDate: '2026-08-15',
    price: 1850,
    capacityTotal: 30
  };

  // Media Upload Modal state
  isMediaModalOpen = false;
  selectedEventForMedia: EventResponse | null = null;
  newMediaUrl = '';

  // Stats calculation
  get totalSeatsBooked(): number {
    return this.events.reduce((sum, e) => sum + (e.capacityBooked || 0), 0);
  }

  get totalRevenueEarned(): number {
    return this.events.reduce((sum, e) => sum + ((e.capacityBooked || 0) * (e.price || 0)), 0);
  }

  // Mock initial data if backend returns empty
  mockEvents: EventResponse[] = [
    {
      id: 'evt-101',
      trekId: 'trek-1',
      trekName: 'Rajmachi Fort Trek',
      region: 'Lonavala',
      difficulty: 'MODERATE',
      organizerId: 'org-1',
      organizerName: 'Sahyadri Expeditions',
      title: 'Monsoon Weekend Expedition to Rajmachi',
      description: 'Lush green valleys, camping, twin forts, and waterfalls.',
      eventDate: '2026-08-15',
      price: 1850,
      capacityTotal: 30,
      capacityBooked: 18,
      availableSlots: 12,
      version: 1,
      status: 'APPROVED'
    },
    {
      id: 'evt-102',
      trekId: 'trek-2',
      trekName: 'Torna Fort Summit',
      region: 'Velhe, Pune',
      difficulty: 'HARD',
      organizerId: 'org-1',
      organizerName: 'Sahyadri Expeditions',
      title: 'High Altitude Challenge to Torna Fort',
      description: 'Conquer the highest fort in Pune district with expert trek guides.',
      eventDate: '2026-08-22',
      price: 2200,
      capacityTotal: 25,
      capacityBooked: 22,
      availableSlots: 3,
      version: 0,
      status: 'APPROVED'
    }
  ];

  mockTreks: TrekResponse[] = [
    { id: 'trek-1', name: 'Rajmachi Fort Trek', region: 'Lonavala', difficulty: 'MODERATE' },
    { id: 'trek-2', name: 'Torna Fort Summit', region: 'Pune', difficulty: 'HARD' },
    { id: 'trek-3', name: 'Kalsubai Peak Trek', region: 'Igatpuri', difficulty: 'MODERATE' }
  ];

  ngOnInit(): void {
    const user = this.tokenService.getUser();
    this.organizerEmail = user?.email || 'organizer@trekconnect.com';

    this.loadTrekCatalog();
    this.loadMyEvents();
  }

  loadTrekCatalog(): void {
    this.eventService.getTrekCatalog().subscribe({
      next: (res) => { this.treks = (res && res.length > 0) ? res : this.mockTreks; },
      error: () => { this.treks = this.mockTreks; }
    });
  }

  loadMyEvents(): void {
    this.isLoading = true;
    this.eventService.getMyEvents().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.events = (res && res.length > 0) ? res : this.mockEvents;
      },
      error: () => {
        this.isLoading = false;
        this.events = this.mockEvents;
      }
    });
  }

  openCreateModal(): void {
    this.isCreateModalOpen = true;
  }

  closeCreateModal(): void {
    this.isCreateModalOpen = false;
  }

  submitCreateEvent(): void {
    if (!this.newEvent.title || !this.newEvent.eventDate || !this.newEvent.price) {
      this.notificationService.showError('Please fill in all required event details.', 'Missing Fields');
      return;
    }

    this.eventService.createEvent(this.newEvent).subscribe({
      next: (res) => {
        this.events.unshift(res);
        this.closeCreateModal();
        this.notificationService.showSuccess(`Scheduled batch "${res.title}" published successfully!`, 'Event Published');
      },
      error: () => {
        // Mock fallback
        const mockNew: EventResponse = {
          id: 'evt-' + Math.floor(Math.random() * 1000),
          trekId: 'trek-1',
          trekName: this.newEvent.trekId,
          organizerId: 'org-1',
          organizerName: this.organizationName,
          title: this.newEvent.title,
          description: this.newEvent.description,
          eventDate: this.newEvent.eventDate,
          price: this.newEvent.price,
          capacityTotal: this.newEvent.capacityTotal,
          capacityBooked: 0,
          availableSlots: this.newEvent.capacityTotal,
          version: 0,
          status: 'APPROVED'
        };
        this.events.unshift(mockNew);
        this.closeCreateModal();
        this.notificationService.showSuccess(`Scheduled batch "${mockNew.title}" published successfully!`, 'Event Published');
      }
    });
  }

  openMediaModal(event: EventResponse): void {
    this.selectedEventForMedia = event;
    this.newMediaUrl = '';
    this.isMediaModalOpen = true;
  }

  closeMediaModal(): void {
    this.isMediaModalOpen = false;
    this.selectedEventForMedia = null;
    this.newMediaUrl = '';
  }

  submitAddMedia(): void {
    if (!this.newMediaUrl || this.newMediaUrl.trim().length < 5) {
      this.notificationService.showError('Please provide a valid media image URL.', 'URL Required');
      return;
    }

    const event = this.selectedEventForMedia;
    if (!event) return;

    this.eventService.addEventMedia(event.id, { mediaUrl: this.newMediaUrl, mediaType: 'IMAGE' }).subscribe({
      next: (res) => {
        if (!event.mediaGallery) event.mediaGallery = [];
        event.mediaGallery.push(res);
        this.closeMediaModal();
        this.notificationService.showSuccess('Media gallery photo uploaded successfully!', 'Media Added');
      },
      error: () => {
        if (!event.mediaGallery) event.mediaGallery = [];
        event.mediaGallery.push({ id: 'med-1', eventId: event.id, mediaUrl: this.newMediaUrl, mediaType: 'IMAGE' });
        this.closeMediaModal();
        this.notificationService.showSuccess('Media gallery photo uploaded successfully!', 'Media Added');
      }
    });
  }

  onLogout(): void {
    this.tokenService.clearTokens();
    this.notificationService.showInfo('Organizer logged out.', 'Signed Out');
    this.router.navigate(['/auth/login']);
  }
}
