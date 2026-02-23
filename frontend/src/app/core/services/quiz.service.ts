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

  constructor(private http: HttpClient) {}

  generateQuiz(req: QuizGenerationRequest): Observable<GeneratedQuizDto> {
    return this.http.post<GeneratedQuizDto>(`${this.apiUrl}/quiz-generation/generate`, req);
  }

  getQuizById(id : number) : Observable<GeneratedQuizDto> {
    return this.http.get<GeneratedQuizDto>(`${this.apiUrl}/quiz-generation/quiz/${id}`);
  }

}
