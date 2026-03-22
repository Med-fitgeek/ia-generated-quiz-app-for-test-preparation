import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LucideAngularModule } from 'lucide-angular';

@Component({
  selector: 'app-quiz-grid',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './quiz-grid.component.html',
  styleUrl: './quiz-grid.component.scss'
})
export class QuizGridComponent {

  @Input() quizzes: any[] = [];
  @Input() totalCount: number = 0;
  @Output() startSession = new EventEmitter<number>();
  @Output() deleteQuiz = new EventEmitter<number>();
  @Output() seeAll = new EventEmitter<void>();

  TrashIcon = 'Trash';

  onStartSession(quizId: number): void { this.startSession.emit(quizId); }
  onDeleteQuiz(quizId: number): void   { this.deleteQuiz.emit(quizId); }
  onSeeAll(): void                      { this.seeAll.emit(); }
}