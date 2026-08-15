import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface BookingResponse {
  id: string;
  eventId: string;
  eventTitle: string;
  trekName: string;
  region?: string;
  eventDate?: string;
  userId: string;
  userName: string;
  numSeats: number;
  totalAmount: number;
  status: string;
  idempotencyKey: string;
  expiresAt?: string;
  createdAt?: string;
}

export interface ReserveSeatRequest {
  eventId: string;
  numSeats: number;
  idempotencyKey: string;
}

/**
 * Service handling Trek Seat Reservations, Idempotent Checkout, and Booking Management HTTP requests.
 */
@Injectable({
  providedIn: 'root'
})
export class BookingService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.monolithApiUrl}/bookings`;

  reserveSeats(req: ReserveSeatRequest): Observable<BookingResponse> {
    const headers = new HttpHeaders({
      'X-Idempotency-Key': req.idempotencyKey
    });
    return this.http.post<BookingResponse>(`${this.apiUrl}/reserve`, req, { headers });
  }

  getMyBookings(): Observable<BookingResponse[]> {
    return this.http.get<BookingResponse[]>(`${this.apiUrl}/my-bookings`);
  }

  cancelBooking(bookingId: string): Observable<BookingResponse> {
    return this.http.put<BookingResponse>(`${this.apiUrl}/${bookingId}/cancel`, {});
  }
}
