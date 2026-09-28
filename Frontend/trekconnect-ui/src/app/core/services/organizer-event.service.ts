import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface TrekResponse {
  id: string;
  name: string;
  region?: string;
  history?: string;
  distanceKm?: number;
  difficulty: string;
  altitudeMeters?: number;
  bestSeason?: string;
}

export interface EventMediaResponse {
  id: string;
  eventId: string;
  mediaUrl: string;
  mediaType: string;
  uploadedAt?: string;
}

export interface EventResponse {
  id: string;
  trekId: string;
  trekName: string;
  region?: string;
  difficulty?: string;
  organizerId: string;
  organizerName: string;
  title: string;
  description?: string;
  eventDate: string;
  price: number;
  capacityTotal: number;
  capacityBooked: number;
  availableSlots: number;
  version: number;
  status: string;
  createdAt?: string;
  mediaGallery?: EventMediaResponse[];
}

export interface CreateEventRequest {
  trekId: string;
  title: string;
  description?: string;
  eventDate: string;
  price: number;
  capacityTotal: number;
  imageUrl?: string;
}

export interface UpdateEventRequest {
  title?: string;
  description?: string;
  eventDate?: string;
  price?: number;
  capacityTotal?: number;
  status?: string;
  version: number;
}

export interface AddEventMediaRequest {
  mediaUrl: string;
  mediaType?: string;
}

/**
 * Service handling Organizer Trek Catalog & Event Management HTTP requests to monolith backend core (:8081).
 */
@Injectable({
  providedIn: 'root'
})
export class OrganizerEventService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.monolithApiUrl}/organizer`;

  getTrekCatalog(): Observable<TrekResponse[]> {
    return this.http.get<TrekResponse[]>(`${this.apiUrl}/treks`);
  }

  createTrek(name: string, region: string, difficulty: string): Observable<TrekResponse> {
    return this.http.post<TrekResponse>(`${this.apiUrl}/treks`, { name, region, difficulty });
  }

  getMyEvents(): Observable<EventResponse[]> {
    return this.http.get<EventResponse[]>(`${this.apiUrl}/events/my-events`);
  }

  createEvent(req: CreateEventRequest): Observable<EventResponse> {
    return this.http.post<EventResponse>(`${this.apiUrl}/events`, req);
  }

  updateEvent(eventId: string, req: UpdateEventRequest): Observable<EventResponse> {
    return this.http.put<EventResponse>(`${this.apiUrl}/events/${eventId}`, req);
  }

  addEventMedia(eventId: string, req: AddEventMediaRequest): Observable<EventMediaResponse> {
    return this.http.post<EventMediaResponse>(`${this.apiUrl}/events/${eventId}/media`, req);
  }

  deleteEvent(eventId: string): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/events/${eventId}`);
  }
}
