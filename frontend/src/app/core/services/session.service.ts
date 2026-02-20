import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { SessionRequestDto } from "../models/session-request-dto.model";
import { SessionResponseDto } from "../models/session-response-dto.model";
import { Observable } from "rxjs";
import { SubmitAnswerRequestDto } from "../models/submit-answer-request-dto.models";
import { ResultResponseDto } from "../models/result-response-dto.model";
import { SubmitSessionWrapperDto } from "../models/submit-session-wrapper-dto";
import { environment } from "../../../environments/environment.development";

@Injectable({ providedIn: 'root' })
export class SessionService {

  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  createSession(req: SessionRequestDto): Observable<SessionResponseDto> {
    return this.http.post<SessionResponseDto>(`${this.apiUrl}/session/create`, req);
  }

  startSession(id : number): Observable<SessionResponseDto> {
    return this.http.post<SessionResponseDto>(`${this.apiUrl}/session/start`, id);
  }

  submitSession(submitSesionWrapper: SubmitSessionWrapperDto ): Observable<ResultResponseDto> {
    return this.http.post<ResultResponseDto>(`${this.apiUrl}/session/submit`, submitSesionWrapper);
  }

}
