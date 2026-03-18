import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

import { QuizService } from '../../core/services/quiz.service';
import { SessionService } from '../../core/services/session.service';

import { SessionResponseDto } from '../../core/models/session-response-dto.model';
import { Page } from '../../core/models/page.model';
import { UserService } from '../../core/services/user.service';
import { DashboardHeaderComponent } from "../dashboard-header/dashboard-header.component";
import { StatsGridComponent } from "../stats-grid/stats-grid.component";
import { SessionsCardComponent } from "../sessions-card/sessions-card.component";
import { QuizGridComponent } from "../quiz-grid/quiz-grid.component";


@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule, DashboardHeaderComponent, StatsGridComponent, SessionsCardComponent, QuizGridComponent],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.scss'
})
export class DashboardHomeComponent {

  totalQuizzes = 0;
  totalSessions = 0;
  averageScore = 0;
  quizzes: any[] = [];
  sessions: SessionResponseDto[] = [];
  userName = '';


  constructor(
    private quizService: QuizService,
    private sessionService: SessionService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  //------------------------------------------------------
  // Dashboard data
  //------------------------------------------------------

  private loadDashboard(): void {

    this.userService.getCurrentUser().subscribe(user => {
      this.userName = user.username;
    });

    this.quizService.getAllQuizzes().subscribe({
      next: (page) => {

        const quizzes = page.content;

        this.totalQuizzes = page.totalElements;

        this.quizzes = quizzes.map(q => ({
          quizId: q.id,
          title: q.title,
          generatedAt: q.generatedAt,
          sessionsCount: q.numberOfSessions
        }));

      }
      
    });

    this.sessionService.getAllSessions().subscribe(
      (sessions: Page<SessionResponseDto>) => {

        this.sessions = sessions.content;
        this.totalSessions = sessions.content.length;

        if (sessions.content.length > 0) {
          const totalScore =
            sessions.content.reduce((sum, s) => sum + (s.rate ?? 0), 0);

          this.averageScore =
            Math.round(totalScore / sessions.content.length);
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

  goToReview(sessionId: number) : void {
    this.router.navigate(['/quiz-review', sessionId])
  }

  newQuiz(): void {
    this.router.navigate(['/source']);
  }

  openQuiz(quizId: number): void {
    this.router.navigate(['/quiz-preview', quizId]);
  }

}