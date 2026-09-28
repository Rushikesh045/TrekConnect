import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface TrekReview {
  id?: string;
  trekId: string;
  userId?: string;
  userName: string;
  rating: number;
  comment: string;
  createdAt?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.monolithApiUrl}/reviews`;

  submitReview(review: { trekId: string; rating: number; comment: string; userName?: string }): Observable<TrekReview> {
    console.log(`[ReviewService] Submitting review for Trek: ${review.trekId}`);
    return this.http.post<TrekReview>(this.apiUrl, review);
  }

  getTrekReviews(trekId: string): Observable<TrekReview[]> {
    console.log(`[ReviewService] Fetching reviews for Trek: ${trekId}`);
    return this.http.get<TrekReview[]>(`${this.apiUrl}/trek/${trekId}`);
  }
}
