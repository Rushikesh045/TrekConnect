import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';
import { OrganizerApplicationResponse } from './organizer.service';

export interface AdminDashboardStatsResponse {
  totalUsers: number;
  totalOrganizers: number;
  pendingApprovals: number;
  totalTreks: number;
}

export interface DisputeResponse {
  id: string;
  paymentId: string;
  requestedByUserId: string;
  requestedByUserName: string;
  reason: string;
  status: string;
  createdAt?: string;
}

export interface RejectOrganizerRequest {
  rejectionReason: string;
}

/**
 * Service handling Admin management and dispute oversight HTTP requests to monolith backend core (:8081).
 */
@Injectable({
  providedIn: 'root'
})
export class AdminService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.monolithApiUrl}/admin`;

  getOrganizerApplications(status?: string): Observable<OrganizerApplicationResponse[]> {
    const url = status ? `${this.apiUrl}/organizers?status=${status}` : `${this.apiUrl}/organizers`;
    return this.http.get<OrganizerApplicationResponse[]>(url);
  }

  verifyOrganizer(applicationId: string): Observable<OrganizerApplicationResponse> {
    return this.http.put<OrganizerApplicationResponse>(`${this.apiUrl}/organizers/${applicationId}/verify`, {});
  }

  rejectOrganizer(applicationId: string, rejectionReason: string): Observable<OrganizerApplicationResponse> {
    return this.http.put<OrganizerApplicationResponse>(`${this.apiUrl}/organizers/${applicationId}/reject`, { rejectionReason });
  }

  getDisputes(status?: string): Observable<DisputeResponse[]> {
    const url = status ? `${this.apiUrl}/disputes?status=${status}` : `${this.apiUrl}/disputes`;
    return this.http.get<DisputeResponse[]>(url);
  }

  resolveDispute(disputeId: string, resolution: string = 'APPROVED'): Observable<DisputeResponse> {
    return this.http.put<DisputeResponse>(`${this.apiUrl}/disputes/${disputeId}/resolve?resolution=${resolution}`, {});
  }

  getDashboardStats(): Observable<AdminDashboardStatsResponse> {
    return this.http.get<AdminDashboardStatsResponse>(`${this.apiUrl}/dashboard/stats`);
  }
}
