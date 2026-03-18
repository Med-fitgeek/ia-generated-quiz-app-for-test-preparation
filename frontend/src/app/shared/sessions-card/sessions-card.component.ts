import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

import { SessionResponseDto } from '../../core/models/session-response-dto.model';

@Component({
  selector: 'app-sessions-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sessions-card.component.html',
  styleUrl: './sessions-card.component.scss'
})
export class SessionsCardComponent {

  @Input() sessions: SessionResponseDto[] = [];
  @Output() goToReview = new EventEmitter<number>();

  onReview(sessionId: number): void {
    this.goToReview.emit(sessionId);
  }

}