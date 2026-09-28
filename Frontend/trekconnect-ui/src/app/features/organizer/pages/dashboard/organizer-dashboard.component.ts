import { Component, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { OrganizerEventService, EventResponse, TrekResponse, CreateEventRequest } from '../../../../core/services/organizer-event.service';
import { LocationAutocompleteService } from '../../../../core/services/location-autocomplete.service';
import { TokenService } from '../../../../core/services/token.service';
import { NotificationService } from '../../../../core/services/notification.service';

/**
 * ORGANIZER Role Dynamic Trek & Event Batch Management Dashboard.
 * 
 * WHY THIS COMPONENT WAS CREATED:
 * Provides organizers with a fully dynamic dashboard to launch scheduled trek batches.
 * Includes real-time location autocomplete suggestions, manual input fields for title, price, capacity,
 * and itinerary description, and live database persistence.
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
  private locationService = inject(LocationAutocompleteService);
  private tokenService = inject(TokenService);
  private notificationService = inject(NotificationService);
  private router = inject(Router);

  isLoading = true;
  organizerEmail = '';
  organizationName = 'Sahyadri Expeditions';

  events: EventResponse[] = [];
  treks: TrekResponse[] = [];

  // Create Event Modal State
  isCreateModalOpen = false;
  newEvent: CreateEventRequest = {
    trekId: '',
    title: '',
    description: '',
    eventDate: new Date().toISOString().split('T')[0],
    price: 1500,
    capacityTotal: 25
  };

  // Location Autocomplete State
  locationQuery = '';
  locationSuggestions: string[] = [];
  isSuggestionsOpen = false;

  // Media Upload Modal State
  isMediaModalOpen = false;
  selectedEventForMedia: EventResponse | null = null;
  newMediaUrl = '';
  mediaSourceType: 'LOCAL' | 'URL' = 'LOCAL';
  uploadedMediaFileName = '';
  uploadedMediaFileSize = '';

  // Cover Image state for Launch Batch form
  coverImageSourceType: 'LOCAL' | 'URL' = 'LOCAL';
  uploadedCoverFileName = '';
  uploadedCoverFileSize = '';

  // Delete Event Confirmation Modal State
  isDeleteConfirmModalOpen = false;
  selectedEventToDelete: EventResponse | null = null;

  setMediaSourceType(type: 'LOCAL' | 'URL'): void {
    this.mediaSourceType = type;
  }

  setCoverImageSourceType(type: 'LOCAL' | 'URL'): void {
    this.coverImageSourceType = type;
  }

  onLocalMediaFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.uploadedMediaFileName = file.name;
    this.uploadedMediaFileSize = (file.size / (1024 * 1024)).toFixed(2) + ' MB';

    const reader = new FileReader();
    reader.onload = () => {
      const dataUrl = reader.result as string;
      this.newMediaUrl = dataUrl;
      this.notificationService.showSuccess(`Media file "${file.name}" loaded successfully!`, 'File Selected');
    };
    reader.readAsDataURL(file);
  }

  removeSelectedMediaFile(): void {
    this.newMediaUrl = '';
    this.uploadedMediaFileName = '';
    this.uploadedMediaFileSize = '';
  }

  onLocalCoverFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (!input.files || input.files.length === 0) return;

    const file = input.files[0];
    this.uploadedCoverFileName = file.name;
    this.uploadedCoverFileSize = (file.size / (1024 * 1024)).toFixed(2) + ' MB';

    const reader = new FileReader();
    reader.onload = () => {
      const dataUrl = reader.result as string;
      this.newEvent.imageUrl = dataUrl;
      this.notificationService.showSuccess(`Cover photo "${file.name}" loaded successfully!`, 'File Selected');
    };
    reader.readAsDataURL(file);
  }

  removeSelectedCoverFile(): void {
    this.newEvent.imageUrl = '';
    this.uploadedCoverFileName = '';
    this.uploadedCoverFileSize = '';
  }

  openDeleteConfirmModal(eventBatch: EventResponse): void {
    this.selectedEventToDelete = eventBatch;
    this.isDeleteConfirmModalOpen = true;
  }

  closeDeleteConfirmModal(): void {
    this.isDeleteConfirmModalOpen = false;
    this.selectedEventToDelete = null;
  }

  confirmDeleteEvent(): void {
    if (!this.selectedEventToDelete) return;

    const targetId = this.selectedEventToDelete.id;
    const targetTitle = this.selectedEventToDelete.title;

    this.eventService.deleteEvent(targetId).subscribe({
      next: () => {
        this.events = this.events.filter(e => e.id !== targetId);
        this.closeDeleteConfirmModal();
        this.notificationService.showSuccess(`Event batch "${targetTitle}" has been deleted.`, 'Event Deleted');
      },
      error: (err) => {
        this.closeDeleteConfirmModal();
        const msg = err?.error?.message || 'Failed to delete event batch. Please try again.';
        this.notificationService.showError(msg, 'Deletion Failed');
      }
    });
  }

  get totalSeatsBooked(): number {
    return this.events.reduce((sum, e) => sum + (e.capacityBooked || 0), 0);
  }

  get totalRevenueEarned(): number {
    return this.events.reduce((sum, e) => sum + ((e.capacityBooked || 0) * (e.price || 0)), 0);
  }

  ngOnInit(): void {
    const user = this.tokenService.getUser();
    this.organizerEmail = user?.email || 'organizer@trekconnect.com';

    this.loadTrekCatalog();
    this.loadMyEvents();
  }

  loadTrekCatalog(): void {
    this.eventService.getTrekCatalog().subscribe({
      next: (res) => {
        this.treks = res;
        if (res.length > 0) {
          this.newEvent.trekId = res[0].id;
        }
      },
      error: (err) => console.error('Error loading trek catalog', err)
    });
  }

  loadMyEvents(): void {
    this.isLoading = true;
    this.eventService.getMyEvents().subscribe({
      next: (res) => {
        this.isLoading = false;
        this.events = res;
      },
      error: (err) => {
        this.isLoading = false;
        console.error('Error loading organizer events', err);
      }
    });
  }

  /**
   * Real-time location search autocomplete input handler.
   */
  onLocationInput(): void {
    if (!this.locationQuery || this.locationQuery.trim().length < 2) {
      this.locationSuggestions = [];
      this.isSuggestionsOpen = false;
      return;
    }

    this.locationService.getSuggestions(this.locationQuery).subscribe({
      next: (suggestions) => {
        this.locationSuggestions = suggestions;
        this.isSuggestionsOpen = suggestions.length > 0;
      },
      error: () => {
        this.locationSuggestions = [
          `${this.locationQuery}, Pune, Maharashtra`,
          `${this.locationQuery}, Raigad, Maharashtra`,
          `${this.locationQuery}, Lonavala, Maharashtra`
        ];
        this.isSuggestionsOpen = true;
      }
    });
  }

  selectLocation(location: string): void {
    this.locationQuery = location;
    this.isSuggestionsOpen = false;
  }

  openCreateModal(): void {
    this.isCreateModalOpen = true;
    if (this.treks.length > 0) {
      this.newEvent.trekId = this.treks[0].id;
    }
  }

  closeCreateModal(): void {
    this.isCreateModalOpen = false;
    this.isSuggestionsOpen = false;
    // Reset form state
    this.locationQuery = '';
    this.uploadedCoverFileName = '';
    this.uploadedCoverFileSize = '';
    this.newEvent = {
      trekId: this.treks.length > 0 ? this.treks[0].id : '',
      title: '',
      description: '',
      eventDate: '',
      price: 0,
      capacityTotal: 0
    };
  }

  submitCreateEvent(): void {
    if (!this.newEvent.title || !this.newEvent.eventDate || this.newEvent.price == null || !this.newEvent.capacityTotal) {
      this.notificationService.showError('Please fill in all required batch fields (Title, Price, Seats, Date).', 'Missing Fields');
      return;
    }

    if (this.newEvent.price < 0) {
      this.notificationService.showError('Price cannot be negative.', 'Invalid Price');
      return;
    }

    if (!this.newEvent.trekId && this.treks.length > 0) {
      this.newEvent.trekId = this.treks[0].id;
    }

    console.log('[OrganizerDashboardComponent] Submitting new dynamic event batch to database:', this.newEvent);
    this.eventService.createEvent(this.newEvent).subscribe({
      next: (res) => {
        this.events.unshift(res);
        this.closeCreateModal();
        this.notificationService.showSuccess(`Scheduled batch "${res.title}" published live to database!`, 'Batch Published');
      },
      error: (err) => {
        console.error('Error creating event batch', err);
        const msg = err?.error?.message || 'Failed to publish batch. Please check your connection and try again.';
        this.notificationService.showError(msg, 'Publish Failed');
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
      error: (err) => {
        const msg = err?.error?.message || 'Failed to upload media. Please try again.';
        this.notificationService.showError(msg, 'Upload Failed');
        this.closeMediaModal();
      }
    });
  }

  onLogout(): void {
    this.tokenService.clearTokens();
    this.notificationService.showInfo('Organizer logged out.', 'Signed Out');
    this.router.navigate(['/auth/login']);
  }
}
