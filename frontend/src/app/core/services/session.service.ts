import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { SessionResponseDto } from "../models/session-response-dto.model";
import { Observable } from "rxjs";
import { ResultResponseDto } from "../models/result-response-dto.model";
import { environment } from "../../../environments/environment";
import { SubmitAnswerRequestDto } from "../models/submit-answer-request-dto.models";
import { QuizReviewDto } from "../models/quiz-review-dto.model";
import { QuizReportDto } from "../models/quiz-report-dto.model";

@Injectable({ providedIn: 'root' })
export class SessionService {

  private readonly apiUrl = environment.apiUrl;
  private readonly serviceUrl = `${this.apiUrl}/sessions`;

  constructor(private http: HttpClient) {}

  createSession(quizId: number): Observable<SessionResponseDto> {

    return this.http.post<SessionResponseDto>(
      `${this.serviceUrl}`,
      quizId
    );
  }

  submitSession(
    sessionId: number,
    request: SubmitAnswerRequestDto
  ): Observable<ResultResponseDto> {

    return this.http.post<ResultResponseDto>(
      `${this.serviceUrl}/${sessionId}/submit`,
      request
    );
  }

  getSessionById(sessionId: number): Observable<SessionResponseDto> {
    return this.http.get<SessionResponseDto>(
      `${this.serviceUrl}/${sessionId}`
    );
  }

  getAllSessions(): Observable<SessionResponseDto[]> {
    return this.http.get<SessionResponseDto[]>(`${this.serviceUrl}`);
  }

  getSessions() {
  return this.http.get<any>(`${this.apiUrl}`);
  }

  deleteSession(sessionId: number): Observable<void> {
    return this.http.delete<void>(`${this.serviceUrl}/${sessionId}`);
  }

  getSessionResult(sessionId: number) {
    return this.http.get<QuizReviewDto>(`${this.serviceUrl}/${sessionId}/result`);
  }

  getSessionReport(sessionId: number) {
    return this.http.get<QuizReportDto>(`${this.serviceUrl}/${sessionId}/report`);
  }
}