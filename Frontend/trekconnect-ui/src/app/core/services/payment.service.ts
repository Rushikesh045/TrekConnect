import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface PaymentOrderResponse {
  paymentOrderId: string;
  bookingId: string;
  razorpayOrderId: string;
  razorpayPaymentId?: string;
  keyId: string;
  amount: number;
  currency: string;
  status: string;
  createdAt?: string;
}

export interface VerifyPaymentRequest {
  bookingId: string;
  razorpayOrderId: string;
  razorpayPaymentId: string;
  razorpaySignature: string;
}

/**
 * Angular HTTP Service for Phase 6 Razorpay Payment Operations.
 * 
 * WHY THIS SERVICE WAS CREATED:
 * Interacts with /api/payments REST endpoints to create Razorpay Order IDs and verify HMAC payment signatures.
 */
@Injectable({
  providedIn: 'root'
})
export class PaymentService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.monolithApiUrl}/payments`;

  /**
   * Creates a Razorpay Order ID for a reserved booking.
   */
  createOrder(bookingId: string): Observable<PaymentOrderResponse> {
    console.log(`[PaymentService] Creating Razorpay order for Booking: ${bookingId}`);
    return this.http.post<PaymentOrderResponse>(`${this.apiUrl}/razorpay/create-order`, { bookingId });
  }

  createPaymentOrder(bookingId: string): Observable<PaymentOrderResponse> {
    return this.createOrder(bookingId);
  }

  /**
   * Verifies Razorpay HMAC SHA256 payment signature.
   */
  verifySignature(request: VerifyPaymentRequest): Observable<PaymentOrderResponse> {
    console.log(`[PaymentService] Verifying Razorpay signature for Order: ${request.razorpayOrderId}`);
    return this.http.post<PaymentOrderResponse>(`${this.apiUrl}/razorpay/verify-signature`, request);
  }
}
