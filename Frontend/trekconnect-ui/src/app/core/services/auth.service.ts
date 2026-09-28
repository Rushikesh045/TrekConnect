import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap, catchError, throwError } from 'rxjs';
import { environment } from '../environments/environment';
import { AuthResponse, LoginRequest, RegisterRequest, MessageResponse, UserInfo } from '../models/auth.model';
import { TokenService } from './token.service';

/**
 * Service managing Authentication HTTP REST API interactions.
 * 
 * WHY THIS SERVICE WAS CREATED:
 * Serves as the central API client for identity management.
 * Sends registration, login, refresh token, logout, and profile identity requests
 * to the Auth Microservice (http://localhost:8080/auth) and updates token storage signals.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private http = inject(HttpClient);
  private tokenService = inject(TokenService);
  private baseUrl = environment.authApiUrl;

  /**
   * Authenticates user credentials via POST /auth/login.
   * 
   * WHY THIS METHOD WAS CREATED:
   * Sends user email, password, and device info to Auth Service.
   * Upon successful response, automatically persists access/refresh tokens and user info in TokenService.
   * 
   * @param credentials LoginRequest containing email, password, and deviceInfo.
   * @return Observable<AuthResponse> returning tokens and user details.
   */
  login(credentials: LoginRequest): Observable<AuthResponse> {
    logDebug('Sending login request to Auth Service...');
    return this.http.post<AuthResponse>(`${this.baseUrl}/login`, credentials).pipe(
      tap(res => {
        // Step 1: Save access token and refresh token into local storage & signals
        this.tokenService.saveTokens(res.accessToken, res.refreshToken);
        // Step 2: Save decoded user info into signal state
        this.tokenService.saveUser(res.user);
      })
    );
  }

  /**
   * Registers a new user account via POST /auth/register.
   * 
   * WHY THIS METHOD WAS CREATED:
   * Creates new user credentials on Auth Service and broadcasts user.registered event.
   * 
   * @param data RegisterRequest containing name, email, password, and role.
   * @return Observable<AuthResponse>
   */
  register(data: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.baseUrl}/register`, data).pipe(
      tap(res => {
        // Step 1: Persist tokens and user state upon registration
        this.tokenService.saveTokens(res.accessToken, res.refreshToken);
        this.tokenService.saveUser(res.user);
      })
    );
  }

  /**
   * Rotates refresh tokens via POST /auth/refresh.
   * 
   * WHY THIS METHOD WAS CREATED:
   * Obtains a fresh access token using the stored refresh token when an HTTP 401 Unauthorized occurs.
   */
  refreshToken(): Observable<AuthResponse> {
    const refreshToken = this.tokenService.getRefreshToken();
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    return this.http.post<AuthResponse>(`${this.baseUrl}/refresh`, { refreshToken }).pipe(
      tap(res => {
        // Step 1: Update stored tokens with newly rotated pair
        this.tokenService.saveTokens(res.accessToken, res.refreshToken);
        this.tokenService.saveUser(res.user);
      })
    );
  }

  /**
   * Logs out the user session via POST /auth/logout.
   * 
   * WHY THIS METHOD WAS CREATED:
   * Informs backend to revoke refresh token & blacklist access token JTI, then clears local storage state.
   */
  logout(): Observable<MessageResponse> {
    const refreshToken = this.tokenService.getRefreshToken();
    return this.http.post<MessageResponse>(`${this.baseUrl}/logout`, { refreshToken }).pipe(
      tap(() => this.tokenService.clear()),
      catchError(err => {
        // Ensure local tokens are cleared even if server logout call fails
        this.tokenService.clear();
        return throwError(() => err);
      })
    );
  }

  /**
   * Fetches current identity profile via GET /auth/me.
   * 
   * WHY THIS METHOD WAS CREATED:
   * Validates stored JWT and updates user signal state.
   */
  getMe(): Observable<UserInfo> {
    return this.http.get<UserInfo>(`${this.baseUrl}/me`).pipe(
      tap(user => this.tokenService.saveUser(user))
    );
  }

  changePassword(currentPassword: string, newPassword: string): Observable<MessageResponse> {
    return this.http.post<MessageResponse>(`${this.baseUrl}/change-password`, { currentPassword, newPassword });
  }
}

function logDebug(msg: string) {
  console.log(`[AuthService] ${msg}`);
}
