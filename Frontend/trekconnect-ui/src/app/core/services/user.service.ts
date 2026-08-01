import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environments/environment';

export interface UserProfileResponse {
  userId: string;
  name: string;
  phone?: string;
  profilePicUrl?: string;
  bio?: string;
  role: string;
  createdAt?: string;
}

export interface UpdateProfileRequest {
  name: string;
  phone?: string;
  profilePicUrl?: string;
  bio?: string;
}

/**
 * Service handling user profile HTTP requests to monolith backend core (:8080).
 */
@Injectable({
  providedIn: 'root'
})
export class UserService {
  private http = inject(HttpClient);
  private apiUrl = `${environment.monolithApiUrl}/users`;

  getProfile(): Observable<UserProfileResponse> {
    return this.http.get<UserProfileResponse>(`${this.apiUrl}/me`);
  }

  updateProfile(request: UpdateProfileRequest): Observable<UserProfileResponse> {
    return this.http.put<UserProfileResponse>(`${this.apiUrl}/me`, request);
  }
}
