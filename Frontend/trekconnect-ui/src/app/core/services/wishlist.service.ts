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
}
