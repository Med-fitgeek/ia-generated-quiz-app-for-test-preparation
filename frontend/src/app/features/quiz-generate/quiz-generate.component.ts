import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { QuizService } from '../../core/services/quiz.service';
import { QuizRuntimeService } from '../../core/services/quiz-runtime.service';
import { QuizGenerationRequest } from '../../core/models/quiz-generation.model';
import { GeneratedQuizDto } from '../../core/models/generated-quiz-dto';
import { Difficulty } from '../../core/models/difficulty.model';

@Component({
  standalone: true,
  selector: 'app-quiz-generate',
  imports: [CommonModule, FormsModule],
  templateUrl: './quiz-generate.component.html',
  styleUrl: './quiz-generate.component.scss'
})
export class QuizGenerateComponent implements OnInit {

  sourceId!: number;

  title = '';
  numberOfQuestions = 5;
  difficulty: Difficulty = 'MODERATE';

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
      this.error = 'Paramètre sourceId manquant dans la route.';
      return;
    }
    this.sourceId = Number(idParam);
    if (Number.isNaN(this.sourceId)) {
      this.error = 'sourceId invalide.';
    }
  }

  increment(): void {
    if (this.numberOfQuestions < 12) this.numberOfQuestions++;
  }

  decrement(): void {
    if (this.numberOfQuestions > 1) this.numberOfQuestions--;
  }

  submit(): void {
    this.error = null;

    if (!this.sourceId) {
      this.error = 'Source de contenu non définie.';
      return;
    }
    if (this.numberOfQuestions < 1 || this.numberOfQuestions > 12) {
      this.error = 'Le nombre de questions doit être compris entre 1 et 12.';
      return;
    }
    if (!this.title.trim()) {
      this.error = 'Veuillez saisir un titre pour le quiz.';
      return;
    }

    const payload: QuizGenerationRequest = {
      sourceId: this.sourceId,
      title: this.title.trim(),
      numberOfQuestions: this.numberOfQuestions,
      difficulty: this.difficulty,
    };

    this.loading = true;

    this.quizService.generateQuiz(payload).subscribe({
      next: (res: GeneratedQuizDto) => {
        this.loading = false;
        this.quizRuntime.saveQuiz(res);
        this.router.navigate(['/quiz-player', res.quizId]);
      },
      error: (err) => {
        this.loading = false;
        this.error = err?.error?.message || 'La génération du quiz a échoué. Réessayez.';
      },
    });
  }
}