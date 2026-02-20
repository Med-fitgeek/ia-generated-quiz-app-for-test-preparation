import { Component, OnInit } from '@angular/core';
import { GeneratedQuizDto } from '../../core/models/generated-quiz-dto';
import { GeneratedQuestion } from '../../core/models/generated-question.model';
import { CommonModule, NgStyle } from '@angular/common';
import { QuizRuntimeService } from '../../core/services/quiz-runtime.service';
import { Router } from '@angular/router';
import { SessionService } from '../../core/services/session.service';
import { SessionRequestDto } from '../../core/models/session-request-dto.model';
import { SessionResponseDto } from '../../core/models/session-response-dto.model';
import { ResultResponseDto } from '../../core/models/result-response-dto.model';
import { SubmitAnswerRequestDto } from '../../core/models/submit-answer-request-dto.models';
import { SubmitSessionWrapperDto } from '../../core/models/submit-session-wrapper-dto';

type QuizState = 'READY' | 'IN_PROGRESS' | 'COMPLETED';

@Component({
  selector: 'app-quiz-player',
  standalone: true,
  imports: [CommonModule, NgStyle],
  templateUrl: './quiz-player.component.html',
  styleUrl: './quiz-player.component.scss'
})
export class QuizPlayerComponent implements OnInit {

  quiz!: GeneratedQuizDto;

  state: QuizState = 'READY';
  error: string | null = null;
  sessionId!: number;
  questionIndex = 0;
  selectedAnswers: (number)[] = [];

  rate = 0;

  constructor(
    private sessionService: SessionService, 
    private quizRuntime: QuizRuntimeService,
    private router: Router
  ) {}

  ngOnInit(): void {
  this.quiz = this.quizRuntime.loadQuiz() ?? {
    quizId: 0,
    generatedQuestions: []
  };

  if (!this.quiz.generatedQuestions.length) {
    this.error = 'Quiz vide ou non fourni';
    return;
  }

  this.selectedAnswers = new Array(this.quiz.generatedQuestions.length).fill(null);
  this.state = 'READY';
}

  
  createQuiz(): void {

    const payload: SessionRequestDto = {
    quizId: this.quiz.quizId,
    sessionStatus: 'CREATED',
    duration: 1
    };

    this.sessionService.createSession(payload).subscribe({
        next: (res: SessionResponseDto) => {
          this.sessionId =res.id
          this.startSession(this.sessionId);
        },
        error: (err) => {
          this.error = err?.error?.message || 'No quiz was created please return to quiz generation.';
        },
      });
  }

  startSession(id: number): void {
    this.sessionService.startSession(id).subscribe({
      error : (err) => {
        this.error = err?.error?.message || 'Quiz start failed'
      }
    });

    this.state = 'IN_PROGRESS';
  }

  get currentQuestion(): GeneratedQuestion {
    return this.quiz.generatedQuestions[this.questionIndex];
  }

  selectAnswer(choiceIndex: number): void {
    this.selectedAnswers[this.questionIndex] = choiceIndex;
  }

  next(): void {
    if (this.questionIndex < this.quiz.generatedQuestions.length - 1) {
      this.questionIndex++;
    }
  }

  previous(): void {
    if (this.questionIndex > 0) {
      this.questionIndex--;
    }
  }

  
  submitAnswers(): void {

    const answers: SubmitAnswerRequestDto = {
      answers: this.selectedAnswers
    };

    const payload: SubmitSessionWrapperDto = {
      sessionId: this.sessionId,
      sessionRequestDto: answers
    };

    this.sessionService.submitSession(payload).subscribe({
      next: (res: ResultResponseDto) => {
        this.rate = res.rate;
        this.state = 'COMPLETED';
      },
      error : (err) => {
        this.error = err?.error?.message || 'Erreur lors du traitement des réponses, réessayez plus tard'
      }
    });

  }

  isSelected(choiceIndex: number): boolean {
    return this.selectedAnswers[this.questionIndex] === choiceIndex;
  }

  isLastQuestion(): boolean {
    return this.questionIndex === this.quiz.generatedQuestions.length - 1;
  }
}
