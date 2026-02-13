import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { QuizSummary } from '../../core/models/quiz-summary.model';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})


export class DashboardComponent implements OnInit {
  quizzes: QuizSummary[] = [];

  constructor(private router: Router) {}

  ngOnInit(): void {
    // TODO: remplacer par appel API
    this.quizzes = [
      {
        quizId: 1,
        title: 'Java Basics',
        sessionsCount: 3,
        lastPlayedAt: '2026-02-10',
      },
      {
        quizId: 2,
        title: 'Spring Security',
        sessionsCount: 1,
        lastPlayedAt: '2026-02-08',
      },
    ];
  }

  newQuiz(): void {
    this.router.navigate(['/knowledge']);
  }

  openQuiz(quizId: number): void {
    this.router.navigate(['/quiz-preview', quizId]);
  }
}

