// core/services/quiz.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { QuizGenerationRequest } from '../models/quiz-generation.model';
import { GeneratedQuizDto } from '../models/generated-quiz-dto';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class QuizService {
  private readonly apiUrl = environment.apiUrl;
  private readonly serviceUrl = `${this.apiUrl}/quizzes`

  constructor(private http: HttpClient) {}

  generateQuiz(req: QuizGenerationRequest): Observable<GeneratedQuizDto> {
    return this.http.post<GeneratedQuizDto>(`${this.serviceUrl}/generate`, req);
  }

  getQuizById(id : number) : Observable<GeneratedQuizDto> {
    return this.http.get<GeneratedQuizDto>(`${this.serviceUrl}/${id}`);
  }

  getAllQuizzes(): Observable<GeneratedQuizDto[]> {
    return this.http.get<GeneratedQuizDto[]>(`${this.serviceUrl}`);
  }
  deleteQuiz(quizId: number): Observable<void> {
    return this.http.delete<void>(`${this.serviceUrl}/${quizId}`);
  }

}
