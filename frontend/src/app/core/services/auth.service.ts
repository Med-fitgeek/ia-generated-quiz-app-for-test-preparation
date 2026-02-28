import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { tap, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class AuthService {

  private apiUrl = environment.apiUrl;
  private accessToken: string | null = null;

  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();
  private authInitializedSubject = new BehaviorSubject<boolean>(false);
  public authInitialized$ = this.authInitializedSubject.asObservable();

  constructor(private http: HttpClient, private router: Router) {}

  login(credentials: any): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/auth/login`,
      credentials,
      { withCredentials: true }
    ).pipe(
      tap((res: any) => {
        this.accessToken = res.accessToken;
        this.isAuthenticatedSubject.next(true);
      })
    );
  }

  register(data: any): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/auth/register`,
      data,
      { withCredentials: true }
    ).pipe(
      tap((res: any) => {
        this.accessToken = res.accessToken;
        this.isAuthenticatedSubject.next(true);
      })
    );
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/auth/logout`, {}, { withCredentials: true })
      .subscribe();

    this.accessToken = null;
    this.isAuthenticatedSubject.next(false);
    this.router.navigate(['/login']);
  }

  refreshAccessToken(): Observable<any> {
    return this.http.post(
      `${this.apiUrl}/auth/refresh`,
      {},
      { withCredentials: true }
    ).pipe(
      tap((res: any) => {
        this.accessToken = res.accessToken;
        this.isAuthenticatedSubject.next(true);
        this.authInitializedSubject.next(true);
      }),
      catchError(err => {
        this.accessToken = null;
        this.isAuthenticatedSubject.next(false);
        this.router.navigate(['/login']);
        return throwError(() => err);
      })
    );
  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  isLoggedIn(): boolean {
    return !!this.accessToken;
  }
}