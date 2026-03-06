import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { SessionResponseDto } from "../models/session-response-dto.model";
import { Observable } from "rxjs";
import { ResultResponseDto } from "../models/result-response-dto.model";
import { environment } from "../../../environments/environment";
import { SubmitAnswerRequestDto } from "../models/submit-answer-request-dto.models";

@Injectable({ providedIn: 'root' })
export class SessionService {

  private readonly apiUrl = environment.apiUrl;
  private readonly serviceUrl = `${this.apiUrl}/sessions`

  constructor(private http: HttpClient) {}

  createSession(quizId: number): Observable<SessionResponseDto> {
    return this.http.post<SessionResponseDto>(`${this.serviceUrl}/create`, quizId);
  }

  submitSession( sessionId: number,
  request: SubmitAnswerRequestDto): Observable<ResultResponseDto> {
    return this.http.post<ResultResponseDto>(`${this.serviceUrl}/${sessionId}/submit`, request);
  }

  getSessionById(sessionId: number): Observable<SessionResponseDto> {
    return this.http.get<SessionResponseDto>(`${this.serviceUrl}/${sessionId}`);
  }

  getAllSessions(): Observable<SessionResponseDto[]> {
    return this.http.get<SessionResponseDto[]>(`${this.serviceUrl}`);
  }

  deleteSession(sessionId: number): Observable<void> {
    return this.http.delete<void>(`${this.serviceUrl}/${sessionId}`);
  }

}
