import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

import { QuizService } from '../../core/services/quiz.service';
import { SessionService } from '../../core/services/session.service';
import { UserService } from '../../core/services/user.service';

import { SessionResponseDto } from '../../core/models/session-response-dto.model';
import { Page } from '../../core/models/page.model';

import { DashboardHeaderComponent } from '../dashboard-header/dashboard-header.component';
import { StatsGridComponent } from '../stats-grid/stats-grid.component';
import { SessionsCardComponent } from '../sessions-card/sessions-card.component';
import { QuizGridComponent } from '../quiz-grid/quiz-grid.component';

@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule, DashboardHeaderComponent, StatsGridComponent, SessionsCardComponent, QuizGridComponent],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.scss'
})
export class DashboardHomeComponent implements OnInit {

  totalQuizzes = 0;
  totalSessions = 0;
  averageScore = 0;

  recentQuizzes: any[] = [];
  recentSessions: SessionResponseDto[] = [];

  userName = '';
  avatarId = 'a1';

  constructor(
    private quizService: QuizService,
    private sessionService: SessionService,
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadDashboard();
  }

  private loadDashboard(): void {

    this.userService.getCurrentUser().subscribe(user => {
      this.userName = user.username;
      this.avatarId = user.avatarId ?? 'a1';
    });

    this.quizService.getAllQuizzes().subscribe({
      next: (page) => {
        this.totalQuizzes = page.totalElements;
        this.recentQuizzes = page.content.slice(0, 6).map(q => ({
          quizId: q.id,
          title: q.title,
          generatedAt: q.generatedAt,
          sessionsCount: q.numberOfSessions
        }));
      }
    });

    this.sessionService.getAllSessions().subscribe((sessions: Page<SessionResponseDto>) => {
      this.totalSessions = sessions.totalElements ?? sessions.content.length;
      this.recentSessions = sessions.content.slice(0, 5);

      if (sessions.content.length > 0) {
        const totalScore = sessions.content.reduce((sum, s) => sum + (s.rate ?? 0), 0);
        this.averageScore = Math.round(totalScore / sessions.content.length);
      }
    });
  }

  // ── Navigation ──────────────────────────────────────

  goToSessions(): void { this.router.navigate(['/dashboard/sessions']); }
  goToQuizzes():  void { this.router.navigate(['/dashboard/quizzes']); }
  newQuiz():      void { this.router.navigate(['/source']); }
  goToReview(sessionId: number): void { this.router.navigate(['/quiz-review', sessionId]); }

  startSession(quizId: number): void {
    this.sessionService.createSession(quizId).subscribe({
      next: (session) => this.router.navigate(['/quiz-player', session.id])
    });
  }

  deleteQuiz(quizId: number): void {
    this.quizService.deleteQuiz(quizId).subscribe({
      next: () => this.loadDashboard()
    });
  }
}