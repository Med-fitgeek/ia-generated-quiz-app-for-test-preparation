import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { GeneratedQuizDto } from '../../core/models/generated-quiz-dto';
import { GeneratedQuestion } from '../../core/models/generated-question.model';

@Component({
  standalone: true,
  selector: 'app-quiz-preview',
  imports: [CommonModule],
  templateUrl: './quiz-preview.component.html',
})
export class QuizPreviewComponent implements OnInit {
  quiz: GeneratedQuizDto | null = null;
  error: string | null = null;

  constructor(public router: Router) {}

  ngOnInit(): void {
    const nav = this.router.getCurrentNavigation();
    const stateQuiz = nav?.extras?.state?.['quiz'] as GeneratedQuizDto | undefined;

    if (!stateQuiz || !stateQuiz.generatedQuestions || stateQuiz.generatedQuestions.length === 0) {
      this.error = 'No quiz data to display. Please generate a quiz first.';
      return;
    }

    this.quiz = stateQuiz;
  }

  trackByIndex(index: number): number {
    return index;
  }
}
