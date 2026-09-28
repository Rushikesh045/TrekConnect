export type Role = 'USER' | 'ORGANIZER' | 'ADMIN';

export interface UserInfo {
  id: string;
  email: string;
  role: Role;
  isEmailVerified: boolean;
  isActive: boolean;
}

export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  expiresInSeconds: number;
  user: UserInfo;
}

export interface LoginRequest {
  email: string;
  password: string;
  deviceInfo?: string;
  role?: Role;
}

export interface RegisterRequest {
  email: string;
  password: string;
  name: string;
  role?: Role;
}

export interface MessageResponse {
  message: string;
  success: boolean;
}

export interface ApiErrorResponse {
  status: number;
  error: string;
  message: string;
  path?: string;
  details?: string[];
}
