import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { QuizService } from '../../core/services/quiz.service';
import { QuizGenerationRequest } from '../../core/models/quiz-generation.model';
import { Difficulty } from '../../core/models/difficulty.model';
import { GeneratedQuizDto } from '../../core/models/generated-quiz-dto'; 
import { KnowledgeUploadComponent } from '../knowledge-upload/knowledge-upload.component';
import { QuizRuntimeService } from '../../core/services/quiz-runtime.service';

@Component({
  standalone: true,
  selector: 'app-quiz-generate',
  imports: [CommonModule, FormsModule],
  templateUrl: './quiz-generate.component.html',
})
export class QuizGenerateComponent implements OnInit {
  sourceId!: number;

  // Form state
  numberOfQuestions = 10;
  difficulty: Difficulty = 'MEDIUM';

  // UI state
  loading = false;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    public router: Router,
    private quizService: QuizService,
    private quizRuntime: QuizRuntimeService
  ) {}

  ngOnInit(): void {
    const idParam = this.route.snapshot.paramMap.get('id');
    if (!idParam) {
      this.error = 'Missing sourceId in route.';
      return;
    }
    this.sourceId = Number(idParam);
    if (Number.isNaN(this.sourceId)) {
      this.error = 'Invalid sourceId.';
    }
  }

  submit() {
    this.error = null;

    if (!this.sourceId) {
      this.error = 'Knowledge source not defined.';
      return;
    }
    if (this.numberOfQuestions <= 0 || this.numberOfQuestions > 12) {
      this.error = 'Number of questions must be between 1 and 12.';
      return;
    }

    const payload: QuizGenerationRequest = {
      sourceId: this.sourceId,
      numberOfQuestions: this.numberOfQuestions,
      difficulty: this.difficulty,
    };

    this.loading = true;
    this.quizService.generateQuiz(payload).subscribe({
      next: (res: GeneratedQuizDto) => {
        this.loading = false;
        this.quizRuntime.saveQuiz(res);
        this.router.navigate(['/quiz-player']);
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'Quiz generation failed.';
      },
    });
  }
}
