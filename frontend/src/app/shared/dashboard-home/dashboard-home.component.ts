import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

import { QuizService } from '../../core/services/quiz.service';
import { SessionService } from '../../core/services/session.service';

import { SessionResponseDto } from '../../core/models/session-response-dto.model';
import { QuizResponseDto } from '../../core/models/quiz-response-dto.model';

@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.scss'
})
export class DashboardHomeComponent {

  totalQuizzes = 0;
  totalSessions = 0;
  averageScore = 0;

  quizzes: any[] = [];

  constructor(
    private quizService: QuizService,
    private sessionService: SessionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  //------------------------------------------------------
  // Dashboard data
  //------------------------------------------------------

  private loadDashboard(): void {

    this.quizService.getAllQuizzes().subscribe({
      next: (quizzes: QuizResponseDto[]) => {

        this.totalQuizzes = quizzes.length;

        this.quizzes = quizzes.map(q => ({
          quizId: q.id,
          title: q.title, // tu n'as pas encore de titre côté backend
          generatedAt: q.generatedAt,
          sessionsCount: q.numberOfSessions
        }));
      }
    });

    this.sessionService.getAllSessions().subscribe({
      next: (sessions: SessionResponseDto[]) => {

        this.totalSessions = sessions.length;

        if (sessions.length > 0) {
          const totalScore =
            sessions.reduce((sum, s) => sum + (s.scorePercentage ?? 0), 0);

          this.averageScore =
            Math.round(totalScore / sessions.length);
        }
      }
    });
  }

  //------------------------------------------------------
  // Actions
  //------------------------------------------------------

  startSession(quizId: number): void {

    this.sessionService.createSession(quizId).subscribe({
      next: (session) => {
        this.router.navigate(['/session', session.id]);
      }
    });

  }

  deleteQuiz(quizId: number): void {

    this.quizService.deleteQuiz(quizId).subscribe({
      next: () => {
        this.loadDashboard();
      }
    });

  }

}