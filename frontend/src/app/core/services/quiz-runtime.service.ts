// core/services/quiz-runtime.service.ts
import { Injectable } from '@angular/core';
import { GeneratedQuizDto } from '../models/generated-quiz-dto';

const QUIZ_KEY = 'current_quiz';

@Injectable({ providedIn: 'root' })
export class QuizRuntimeService {

  saveQuiz(quiz: GeneratedQuizDto): void {
    sessionStorage.setItem(QUIZ_KEY, JSON.stringify(quiz));
  }

  loadQuiz(): GeneratedQuizDto | null {
    const raw = sessionStorage.getItem(QUIZ_KEY);
    return raw ? JSON.parse(raw) as GeneratedQuizDto : null;
  }

  clear(): void {
    sessionStorage.removeItem(QUIZ_KEY);
  }
}
