import { Injectable, PLATFORM_ID, Inject } from '@angular/core';
import { isPlatformBrowser } from '@angular/common';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root'
})
export class Token {
  private readonly ACCESS_TOKEN = 'access_token';
  private readonly REFRESH_TOKEN = 'refresh_token';
  private readonly USER_ROLE = 'user_role';
  private readonly USER_ID = 'user_id';

  private jwtHelper = new JwtHelperService();

  constructor(@Inject(PLATFORM_ID) private platformId: any) {}

  // ==========================
  //       ACCESS TOKEN
  // ==========================
  set accessToken(token: string) {
    console.log('Setting access token:', token);
    if (this.isBrowser()) {
      localStorage.setItem(this.ACCESS_TOKEN, token);
    }
  }

  get accessToken(): string | null {
    if (this.isBrowser()) {
      const token = localStorage.getItem(this.ACCESS_TOKEN);
      console.log('Getting access token:', token);
      return token;
    }
    console.log('Access token requested on server');
    return null;
  }

  // ==========================
  //       REFRESH TOKEN
  // ==========================
  set refreshToken(token: string) {
    console.log('Setting refresh token:', token);
    if (this.isBrowser()) {
      localStorage.setItem(this.REFRESH_TOKEN, token);
    }
  }

  get refreshToken(): string | null {
    if (this.isBrowser()) {
      const token = localStorage.getItem(this.REFRESH_TOKEN);
      console.log('Getting refresh token:', token);
      return token;
    }
    console.log('Refresh token requested on server');
    return null;
  }

  // ==========================
  //       SAVE ALL TOKENS
  // ==========================
  saveAuthResponse(response: any) {
    console.log('Saving auth response:', response);
    if (this.isBrowser()) {
      this.accessToken = response.accessToken;
      this.refreshToken = response.refreshToken;
      localStorage.setItem(this.USER_ROLE, response.role);
      localStorage.setItem(this.USER_ID, response.userId.toString());
      console.log('Tokens saved to localStorage');
    }
  }

  // ==========================
  //     TOKEN VALIDATION
  // ==========================
  isTokenValid(): boolean {
    if (!this.isBrowser()) {
      console.log('Token validation called on server');
      return false;
    }

    const token = this.accessToken;
    if (!token) {
      console.log('No access token found');
      return false;
    }

    const isExpired = this.jwtHelper.isTokenExpired(token);
    console.log('Token expired?', isExpired);

    if (isExpired) {
      this.clear();
      return false;
    }
    return true;
  }

  isTokenNotValid(): boolean {
    return !this.isTokenValid();
  }

  // ==========================
  //     GET USER ROLES
  // ==========================
  get userRoles(): string[] {
    if (!this.isBrowser()) return [];

    const token = this.accessToken;
    if (!token) return [];

    const decoded = this.jwtHelper.decodeToken(token);
    console.log('Decoded token roles:', decoded?.roles);
    return decoded?.roles?.map((r: any) => r.authority) || [];
  }

  // ==========================
  //     GET USER ID
  // ==========================
  get userId(): number | null {
    if (this.isBrowser()) {
      const id = localStorage.getItem(this.USER_ID);
      console.log('Getting userId:', id);
      return id ? parseInt(id, 10) : null;
    }
    console.log('UserId requested on server');
    return null;
  }

  // ==========================
  //     LOGGED IN CHECK
  // ==========================
  isLoggedIn(): boolean {
    const valid = this.isTokenValid();
    console.log('Is logged in?', valid);
    return valid;
  }

  // ==========================
  //     CLEAR TOKENS
  // ==========================
  clear(): void {
    console.log('Clearing tokens');
    if (this.isBrowser()) {
      localStorage.removeItem(this.ACCESS_TOKEN);
      localStorage.removeItem(this.REFRESH_TOKEN);
      localStorage.removeItem(this.USER_ROLE);
      localStorage.removeItem(this.USER_ID);
    }
  }

  // ==========================
  //     BROWSER CHECK
  // ==========================
  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }
}
