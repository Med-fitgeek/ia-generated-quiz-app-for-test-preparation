import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { QuizSummary } from '../../core/models/quiz-summary.model';
import { SidebarComponent } from '../sidebar/sidebar.component';


@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, SidebarComponent, RouterOutlet],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})


export class DashboardComponent implements OnInit {
  quizzes: QuizSummary[] = [];

  constructor(private router: Router) {}

  ngOnInit(): void {
  }

  newQuiz(): void {
    this.router.navigate(['/source']);
  }

  openQuiz(quizId: number): void {
    this.router.navigate(['/quiz-preview', quizId]);
  }
}

