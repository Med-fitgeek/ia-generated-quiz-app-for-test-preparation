// core/services/quiz.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { QuizGenerationRequest } from '../models/quiz-generation.model';
import { GeneratedQuizDto } from '../models/generated-quiz-dto';

@Injectable({ providedIn: 'root' })
export class QuizService {
  private baseUrl = 'http://localhost:8080/api/quiz-orchestration';

  constructor(private http: HttpClient) {}

  generateQuiz(req: QuizGenerationRequest): Observable<GeneratedQuizDto> {
    return this.http.post<GeneratedQuizDto>(`${this.baseUrl}/generate`, req);
  }
}
