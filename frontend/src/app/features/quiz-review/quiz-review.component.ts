import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { SessionService } from '../../core/services/session.service';
import { QuizReviewDto } from '../../core/models/quiz-review-dto.model';
import { QuizReportDto } from '../../core/models/quiz-report-dto.model';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-quiz-review',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './quiz-review.component.html',
  styleUrls: ['./quiz-review.component.scss']
})
export class QuizReviewComponent implements OnInit {

  review!: QuizReviewDto;
  report!: QuizReportDto;

  loading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private sessionService: SessionService
  ) {}

  ngOnInit(): void {

    const sessionId = Number(
      this.route.snapshot.paramMap.get('sessionId')
    );

    this.sessionService.getSessionResult(sessionId)
      .subscribe({
        next: res => {
          this.review = res;
          this.loading = false;
        },
        error: () => this.error = "Unable to load quiz review"
      });

    this.sessionService.getSessionReport(sessionId)
      .subscribe({
        next: res => {
          this.report = res;
        },
        error: () => this.error = "Unable to load session report"
      });
  }

}