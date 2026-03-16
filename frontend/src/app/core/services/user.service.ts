import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { User } from '../models/user-dto.model';

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

}