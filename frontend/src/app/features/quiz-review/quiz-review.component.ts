import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, RouterModule } from '@angular/router';
import { SessionService } from '../../core/services/session.service';
import { QuizReviewDto } from '../../core/models/quiz-review-dto.model';
import { QuizReportDto } from '../../core/models/quiz-report-dto.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-quiz-review',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './quiz-review.component.html',
  styleUrls: ['./quiz-review.component.scss']
})
export class QuizReviewComponent implements OnInit {

  review!: QuizReviewDto;
  report!: QuizReportDto;
  currentIndex = 0;

  loading = true;
  error: string | null = null;

  readonly choiceLetters = ['A', 'B', 'C', 'D', 'E'];

  constructor(
    private route: ActivatedRoute,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {
    const sessionId = Number(this.route.snapshot.paramMap.get('sessionId'));

    this.sessionService.getSessionResult(sessionId).subscribe({
      next: res => {
        this.review = res;
        this.loading = false;
      },
      error: () => this.error = 'Unable to load quiz review'
    });

    this.sessionService.getSessionReport(sessionId).subscribe({
      next: res => { this.report = res; },
      error: () => this.error = 'Unable to load session report'
    });
  }

  // ── Navigation ──────────────────────────────────────

  next(): void {
    if (this.currentIndex < this.review.questions.length - 1) {
      this.currentIndex++;
    }
  }

  prev(): void {
    if (this.currentIndex > 0) {
      this.currentIndex--;
    }
  }

  goTo(index: number): void {
    this.currentIndex = index;
  }

  // ── Computed ────────────────────────────────────────

  isCurrentCorrect(): boolean {
    const q = this.review.questions[this.currentIndex];
    return q.userAnswer === q.correctIndex;
  }

  get correctCount(): number {
    return this.review.questions.filter(q => q.userAnswer === q.correctIndex).length;
  }

  get wrongCount(): number {
    return this.review.questions.filter(q => q.userAnswer !== q.correctIndex).length;
  }

}