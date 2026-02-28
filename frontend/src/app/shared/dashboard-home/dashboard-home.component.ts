import { Component } from '@angular/core';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard-home',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-home.component.html',
  styleUrl: './dashboard-home.component.scss'
})
export class DashboardHomeComponent {

  totalQuizzes!: number;
  totalSessions!: number;
  averageScore!: number;

  quizzes: any;
  constructor(private router: Router){}

  ngOnInit(): void {

    this.totalQuizzes = 10;
    this.totalSessions  = 10;
    this.averageScore = 10

    this.quizzes = [
      {
        quizId: 1,
        title: 'Java Fundamentals',
        sessionsCount: 5,
        lastPlayedAt: '2026-02-20',
      },
      {
        quizId: 2,
        title: 'Spring Boot & REST APIs',
        sessionsCount: 3,
        lastPlayedAt: '2026-02-18',
      },
      {
        quizId: 3,
        title: 'Angular Components & Lifecycle',
        sessionsCount: 7,
        lastPlayedAt: '2026-02-21',
      },
      {
        quizId: 4,
        title: 'Data Structures & Algorithms',
        sessionsCount: 2,
        lastPlayedAt: '2026-02-15',
      },
      {
        quizId: 5,
        title: 'Clean Code & SOLID Principles',
        sessionsCount: 4,
        lastPlayedAt: '2026-02-17',
      }
    ];
  }

  startSession(quizId: number): void {
  }

  openQuiz(quizId: number): void {
  }
}
