import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, of, throwError } from 'rxjs';
import { catchError, tap, switchMap } from 'rxjs/operators';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly apiUrl = environment.apiUrl;

  // Access token en mémoire
  private accessToken: string | null = null;

  // Observable pour savoir si l’utilisateur est connecté
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  /** LOGIN */
  login(credentials: { email: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/login`, credentials).pipe(
      tap((res: any) => {
        this.accessToken = res.accessToken;
        // Ici, le refresh token peut être stocké dans un cookie HttpOnly via le backend
        this.isAuthenticatedSubject.next(true);
      })
    );
  }

  /** REGISTER */
  register(data: { username: string; email: string; password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/register`, data).pipe(
      tap((res: any) => {
        this.accessToken = res.accessToken;
        this.isAuthenticatedSubject.next(true);
      })
    );
  }

  /** LOGOUT */
  logout(): void {
    this.accessToken = null;
    // Idéalement demander au backend de révoquer le refresh token
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  /** GETTER access token */
  getAccessToken(): string | null {
    return this.accessToken;
  }

  /** REFRESH access token */
  refreshAccessToken(): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth/refresh`, { refreshToken: this.getRefreshTokenFromCookie() }).pipe(
      tap((res: any) => {
        this.accessToken = res.accessToken;
        this.isAuthenticatedSubject.next(true);
      }),
      catchError(err => {
        this.logout();
        return throwError(() => err);
      })
    );
  }

  /** Ici, tu récupères le refresh token depuis un cookie HttpOnly via backend */
  private getRefreshTokenFromCookie(): string {
    // Si tu veux gérer côté front, mais idéalement c’est HttpOnly
    return '';
  }

  /** VÉRIFIER SI CONNECTÉ */
  isLoggedIn(): boolean {
    return !!this.accessToken;
  }
}