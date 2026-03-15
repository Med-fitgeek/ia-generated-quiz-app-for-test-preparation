import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { QuizSummary } from '../../core/models/quiz-summary.model';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { SessionResponseDto } from '../../core/models/session-response-dto.model';
import { SessionService } from '../../core/services/session.service';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, SidebarComponent, RouterOutlet],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})


export class DashboardComponent implements OnInit {

  quizzes: QuizSummary[] = [];
  sessions: SessionResponseDto[] = [];
  userName = '';


  constructor(
    private router: Router,
    private sessionService: SessionService,
    private userService: UserService
  ) {}

  ngOnInit(): void {

    this.sessionService.getAllSessions()
      .subscribe(res => {
        this.sessions = res.content;
      });

    this.userService.getCurrentUser().subscribe(user => {
      this.userName = user.username;
    });
  }

  newQuiz(): void {
    this.router.navigate(['/source']);
  }

  openQuiz(quizId: number): void {
    this.router.navigate(['/quiz-preview', quizId]);
  }

  goToReview(sessionId: number) : void {
    this.router.navigate(['/quiz-review', sessionId])
  }
}

