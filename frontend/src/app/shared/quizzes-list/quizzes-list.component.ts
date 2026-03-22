import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { LucideAngularModule } from 'lucide-angular';
import { QuizService } from '../../core/services/quiz.service';
import { SessionService } from '../../core/services/session.service';

@Component({
  selector: 'app-quizzes-list',
  standalone: true,
  imports: [CommonModule, RouterModule, LucideAngularModule],
  templateUrl: './quizzes-list.component.html',
  styleUrl: './quizzes-list.component.scss'
})
export class QuizzesListComponent implements OnInit {

  quizzes: any[] = [];
  loading = true;

  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  readonly pageSize = 10;

  TrashIcon = 'Trash';

  constructor(
    private quizService: QuizService,
    private sessionService: SessionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    this.loading = true;
    this.quizService.getAllQuizzes(page, this.pageSize).subscribe({
      next: (data) => {
        this.quizzes = data.content.map(q => ({
          quizId: q.id,
          title: q.title,
          generatedAt: q.generatedAt,
          sessionsCount: q.numberOfSessions
        }));
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.currentPage = page;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.loadPage(page);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  newQuiz(): void { this.router.navigate(['/source']); }

  startSession(quizId: number): void {
    this.sessionService.createSession(quizId).subscribe({
      next: (session) => this.router.navigate(['/quiz-player', session.id])
    });
  }

  deleteQuiz(quizId: number): void {
    this.quizService.deleteQuiz(quizId).subscribe({
      next: () => this.loadPage(this.currentPage)
    });
  }

  get pageNumbers(): number[] {
    const range = 2;
    const start = Math.max(0, this.currentPage - range);
    const end   = Math.min(this.totalPages - 1, this.currentPage + range);
    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  }
}