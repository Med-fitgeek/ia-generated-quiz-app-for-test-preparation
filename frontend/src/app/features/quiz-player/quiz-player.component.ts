import { Component, Input, NgModule, OnInit } from '@angular/core';
import { GeneratedQuizDto } from '../../core/models/generated-quiz-dto';
import { GeneratedQuestion } from '../../core/models/generated-question.model';
import { CommonModule, NgStyle } from '@angular/common';
import { QuizRuntimeService } from '../../core/services/quiz-runtime.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-quiz-player',
  standalone: true,
  imports: [CommonModule, NgStyle],
  templateUrl: './quiz-player.component.html',
  styleUrl: './quiz-player.component.scss'
})

export class QuizPlayerComponent implements OnInit {

  quiz!: GeneratedQuizDto;

  constructor(
    private quizRuntime: QuizRuntimeService,
    private router: Router
  ) {}

  currentIndex = 0;
  selectedAnswers: (number | null)[] = [];
  showResult = false;

  ngOnInit(): void {
    this.quiz = this.quizRuntime.loadQuiz() ??  { generatedQuestions: [] } as GeneratedQuizDto;
    if (!this.quiz || !this.quiz.generatedQuestions?.length) {
      console.log('Quiz vide ou non fourni');
    }
    this.selectedAnswers = new Array(this.quiz.generatedQuestions.length).fill(null);
  }

  get currentQuestion(): GeneratedQuestion {
    return this.quiz.generatedQuestions[this.currentIndex];
  }

  selectAnswer(choiceIndex: number): void {
    this.selectedAnswers[this.currentIndex] = choiceIndex;
  }

  next(): void {
    if (this.currentIndex < this.quiz.generatedQuestions.length - 1) {
      this.currentIndex++;
    }
  }

  previous(): void {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }

  finish(): void {
    this.showResult = true;

    // Ici tu enverras au backend :
    // quizSessionId + selectedAnswers
    console.log('Réponses utilisateur:', this.selectedAnswers);
  }

  isSelected(choiceIndex: number): boolean {
    return this.selectedAnswers[this.currentIndex] === choiceIndex;
  }

  isLastQuestion(): boolean {
    return this.currentIndex === this.quiz.generatedQuestions.length - 1;
  }
}
