import { Injectable, signal } from '@angular/core';
import { UserInfo, Role } from '../models/auth.model';

/**
 * Service managing JWT session tokens, user profile signals, and local storage state.
 * 
 * WHY THIS SERVICE WAS CREATED:
 * Encapsulates client-side token persistence (access_token, refresh_token), user profile signals,
 * role extraction, and session clear methods.
 */
@Injectable({
  providedIn: 'root'
})
export class TokenService {

  private readonly ACCESS_TOKEN_KEY = 'tc_access_token';
  private readonly REFRESH_TOKEN_KEY = 'tc_refresh_token';
  private readonly USER_KEY = 'tc_user_info';

  public currentUser = signal<UserInfo | null>(this.getStoredUser());
  public isAuthenticatedSignal = signal<boolean>(!!this.getAccessToken());

  getAccessToken(): string | null {
    return localStorage.getItem(this.ACCESS_TOKEN_KEY);
  }

  getRefreshToken(): string | null {
    return localStorage.getItem(this.REFRESH_TOKEN_KEY);
  }

  saveTokens(accessToken: string, refreshToken: string): void {
    localStorage.setItem(this.ACCESS_TOKEN_KEY, accessToken);
    localStorage.setItem(this.REFRESH_TOKEN_KEY, refreshToken);
    this.isAuthenticatedSignal.set(true);
  }

  saveUser(user: UserInfo): void {
    localStorage.setItem(this.USER_KEY, JSON.stringify(user));
    this.currentUser.set(user);
  }

  getUser(): UserInfo | null {
    return this.currentUser() || this.getStoredUser();
  }

  getRole(): Role | null {
    const user = this.getUser();
    return user ? user.role : null;
  }

  getUserRole(): Role | null {
    return this.getRole();
  }

  isAuthenticated(): boolean {
    return !!this.getAccessToken();
  }

  clear(): void {
    localStorage.removeItem(this.ACCESS_TOKEN_KEY);
    localStorage.removeItem(this.REFRESH_TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
    this.currentUser.set(null);
    this.isAuthenticatedSignal.set(false);
  }

  clearTokens(): void {
    this.clear();
  }

  private getStoredUser(): UserInfo | null {
    const raw = localStorage.getItem(this.USER_KEY);
    if (!raw) return null;
    try {
      return JSON.parse(raw) as UserInfo;
    } catch {
      return null;
    }
  }
}
