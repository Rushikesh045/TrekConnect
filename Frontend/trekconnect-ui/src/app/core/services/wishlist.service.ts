import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface WishlistItem {
  id: string;
  userId: string;
  trekId: string;
  createdAt: string;
}

export interface WishlistFeedEvent {
  id: string;
  trekId: string;
  trekName?: string;
  organizerId: string;
  organizerName?: string;
  organizerPhone?: string;
  title: string;
  eventDate: string;
  cost: number;
  totalSeats: number;
  availableSeats: number;
  status: string;
}

export interface WishlistFeedResponse {
  wishlistId: string;
  trekId: string;
  savedAt: string;
  trekName: string;
  region: string;
  category: string;
  difficulty: string;
  imageUrl: string;
  description: string;
  altitudeMeters: number;
  durationDays: number;
  distanceKm: number;
  bestSeason: string;
  availableEvents: WishlistFeedEvent[];
}

@Injectable({
  providedIn: 'root'
})
export class WishlistService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.monolithApiUrl}/wishlist`;

  toggleWishlist(trekId: string): Observable<{ trekId: string; isSaved: boolean }> {
    console.log(`[WishlistService] Toggling wishlist status for Trek: ${trekId}`);
    return this.http.post<{ trekId: string; isSaved: boolean }>(`${this.apiUrl}/toggle`, { trekId });
  }

  getMyWishlist(): Observable<WishlistItem[]> {
    console.log(`[WishlistService] Fetching trekker wishlist`);
    return this.http.get<WishlistItem[]>(`${this.apiUrl}/my-wishlist`);
  }

  getWishlistFeed(): Observable<WishlistFeedResponse[]> {
    console.log(`[WishlistService] Fetching enriched wishlist feed with events`);
    return this.http.get<WishlistFeedResponse[]>(`${this.apiUrl}/feed`);
  }
}
