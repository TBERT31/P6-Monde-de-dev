import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Session } from '../interfaces/session.interface';
import { jwtDecode } from 'jwt-decode';


@Injectable({
  providedIn: 'root'
})
export class SessionService {
  private readonly SESSION_STORAGE_KEY = 'userSession';
  private isLoggedSubject = new BehaviorSubject<boolean>(this.isLoggedIn());
  public session: Session | undefined;

  constructor() {
    this.loadSession();
  }

  public $isLogged(): Observable<boolean> {
    return this.isLoggedSubject.asObservable();
  }

  public logIn(user: Session): void {
    this.session = user;
    this.saveSession();
    this.isLoggedSubject.next(true);
  }

  public logOut(): void {
    this.session = undefined;
    this.clearSession();
    this.isLoggedSubject.next(false);
  }

  public getSession(): Session | undefined {
    return this.session;
  }

  public getToken(): string | null {
    const session = localStorage.getItem(this.SESSION_STORAGE_KEY);
    if (session) {
      const parsedSession = JSON.parse(session);
      return parsedSession.token || null;
    }
    return null;
  }

  private saveSession(): void {
    if (this.session) {
      localStorage.setItem(this.SESSION_STORAGE_KEY, JSON.stringify(this.session));
    }
  }

  private loadSession(): void {
    const sessionData = localStorage.getItem(this.SESSION_STORAGE_KEY);
    if (sessionData) {
      try {
        const session = JSON.parse(sessionData) as Session;
        if (this.isValidSession(session)) {
          this.session = session;
          this.isLoggedSubject.next(true);
        } else {
          this.clearSession();
        }
      } catch (error) {
        console.error('Error parsing session data:', error);
        this.clearSession();
      }
    }
  }

  private clearSession(): void {
    localStorage.removeItem(this.SESSION_STORAGE_KEY);
    this.session = undefined;
    this.isLoggedSubject.next(false);
  }

  public isLoggedIn(): boolean {
    const token = this.getToken();
    return !!token && !this.isTokenExpired(token);
  }

  private isValidSession(session: Session): boolean {
    return !!(session.token && session.id && session.email && session.username) && !this.isTokenExpired(session.token);
  }

  private isTokenExpired(token: string): boolean {
    try {
      const decodedToken: any = jwtDecode(token);
      const expirationDate = new Date(0);
      expirationDate.setUTCSeconds(decodedToken.exp);

      return expirationDate < new Date();
    } catch (error) {
      console.error('Error decoding token:', error);
      return true;
    }
  }
}
