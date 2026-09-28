import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface OrganizerApplicationRequest {
  organizationName: string;
  contactPhone?: string;
  cityLocation?: string;
  licenseNumber?: string;
  verificationDocsUrl?: string;
}

export interface OrganizerApplicationResponse {
  id: string;
  userId: string;
  organizationName: string;
  contactPhone?: string;
  cityLocation?: string;
  licenseNumber?: string;
  verificationStatus: string;
  verificationDocsUrl?: string;
  rejectionReason?: string;
  verifiedAt?: string;
}

/**
 * Service handling organizer onboarding HTTP requests to monolith backend core (:8080).
 */
@Injectable({
  providedIn: 'root'
})
export class OrganizerService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.monolithApiUrl}/organizers`;

  applyForOrganizer(request: OrganizerApplicationRequest): Observable<OrganizerApplicationResponse> {
    return this.http.post<OrganizerApplicationResponse>(`${this.apiUrl}/apply`, request);
  }

  getMyApplicationStatus(): Observable<OrganizerApplicationResponse> {
    return this.http.get<OrganizerApplicationResponse>(`${this.apiUrl}/my-status`);
  }
}
