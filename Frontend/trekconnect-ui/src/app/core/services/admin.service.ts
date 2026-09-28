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

export interface RegisteredUserResponse {
  userId: string;
  name: string;
  email: string;
  phone?: string;
  role: string;
  profilePicUrl?: string;
  bio?: string;
  createdAt?: string;
}

export interface AdminUserCreateRequest {
  name: string;
  email: string;
  role?: string;
  password?: string;
}

export interface AdminUserCreateResponse {
  userId: string;
  name: string;
  email: string;
  role: string;
  generatedPassword?: string;
  emailSent: boolean;
  message: string;
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

  getAllRegisteredUsers(): Observable<RegisteredUserResponse[]> {
    return this.http.get<RegisteredUserResponse[]>(`${this.apiUrl}/users`);
  }

  createAdminUser(data: AdminUserCreateRequest): Observable<AdminUserCreateResponse> {
    return this.http.post<AdminUserCreateResponse>(`${environment.authApiUrl}/admin/create-user`, data);
  }
}
