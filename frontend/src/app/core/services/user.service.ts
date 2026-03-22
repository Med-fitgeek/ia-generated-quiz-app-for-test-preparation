import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user-dto.model';
import { UpdatePasswordRequest } from '../models/update-password-request.model';
import { UpdateProfileRequest } from '../models/update-profil-request.model';

@Injectable({ providedIn: 'root' })

export class UserService {

  private apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  getCurrentUser(): Observable<User> {

    return this.http.get<User>(`${this.apiUrl}/users/me`);

  }

  updateUser(data: Partial<User>): Observable<User> {

    return this.http.put<User>(`${this.apiUrl}/users/me`, data);

  }

   updateProfile(data: UpdateProfileRequest): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/users/me`, data);
  }
 
  updatePassword(data: UpdatePasswordRequest): Observable<void> {
    return this.http.patch<void>(`${this.apiUrl}/users/me/password`, data);
  }
 
  deleteAccount(): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/users/me`);
  }

}