import { Component, HostListener, OnInit } from '@angular/core';
import { GeneratedQuizDto } from '../../core/models/generated-quiz-dto';
import { GeneratedQuestion } from '../../core/models/generated-question.model';
import { CommonModule } from '@angular/common';
import { QuizRuntimeService } from '../../core/services/quiz-runtime.service';
import { ActivatedRoute, Router } from '@angular/router';
import { SessionService } from '../../core/services/session.service';
import { SessionResponseDto } from '../../core/models/session-response-dto.model';
import { SubmitAnswerRequestDto } from '../../core/models/submit-answer-request-dto.models';
import { QuizService } from '../../core/services/quiz.service';

type QuizState = 'READY' | 'IN_PROGRESS' | 'COMPLETED';

@Component({
  selector: 'app-quiz-player',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quiz-player.component.html',
  styleUrl: './quiz-player.component.scss'
})
export class QuizPlayerComponent implements OnInit {

  quiz!: GeneratedQuizDto;
  quizId!: number;

  state: QuizState = 'READY';
  error: string | null = null;
  sessionId!: number;
  questionIndex = 0;
  selectedAnswers: (number | null)[] = [];
  loading = true;
  rate = 0;

  readonly choiceLetters = ['A', 'B', 'C', 'D', 'E'];

  constructor(
    private sessionService: SessionService,
    private quizRuntime: QuizRuntimeService,
    private quizService: QuizService,
    private router: Router,
    private route: ActivatedRoute,
  ) {}

  @HostListener('window:beforeunload', ['$event'])
  unload($event: any) {
    if (this.state === 'IN_PROGRESS') {
      $event.returnValue = true;
    }
  }

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');

    if (!idParam) {
      this.error = 'Missing sourceId in route.';
      return;
    }

    this.quizId = Number(idParam);
    const cachedQuiz = this.quizRuntime.loadQuiz();

    if (cachedQuiz) {
      this.initializeQuiz(cachedQuiz);
      this.loading = false;
      return;
    }

    this.quizService.getQuizById(this.quizId).subscribe({
      next: (res: GeneratedQuizDto) => {
        this.initializeQuiz(res);
        this.loading = false;
      },
      error: (err) => {
        this.error = err?.error?.message || 'No quiz was found please return to quiz generation.';
        this.loading = false;
      }
    });
  }

  private initializeQuiz(quiz: GeneratedQuizDto): void {
    if (!quiz.generatedQuestions?.length) {
      this.error = 'Quiz vide ou non fourni';
      return;
    }
    this.quiz = quiz;
    this.selectedAnswers = new Array(quiz.generatedQuestions.length).fill(null);
    this.state = 'READY';
  }

  startOrResume(): void {
    this.sessionService.createSession(this.quiz.quizId).subscribe({
      next: (res: SessionResponseDto) => {
        this.sessionId = res.id;
        this.state = 'IN_PROGRESS';
      },
      error: (err) => {
        this.error = err?.error?.message || 'No quiz was created please return to quiz generation.';
      },
    });
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
    const request: SubmitAnswerRequestDto = { answers: this.selectedAnswers as number[] };

    this.sessionService.submitSession(this.sessionId, request).subscribe({
      next: () => {
        this.router.navigate(['/quiz-review', this.sessionId]);
      },
      error: (err) => {
        this.error = err?.error?.message || 'Erreur lors du traitement des réponses, réessayez plus tard';
      }
    });
  }

  goToReview(): void {
    this.router.navigate(['/quiz-review', this.sessionId]);
  }

  goHome(): void {
    this.router.navigate(['/dashboard']);
  }

  isSelected(choiceIndex: number): boolean {
    return this.selectedAnswers[this.questionIndex] === choiceIndex;
  }

  isLastQuestion(): boolean {
    return this.questionIndex === this.quiz.generatedQuestions.length - 1;
  }
}