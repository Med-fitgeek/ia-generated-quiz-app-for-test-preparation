import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-dashboard-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './dashboard-header.component.html',
  styleUrl: './dashboard-header.component.scss'
})
export class DashboardHeaderComponent {

  @Input() userName: string = '';
  @Output() newQuiz = new EventEmitter<void>();

  onNewQuiz(): void {
    this.newQuiz.emit();
  }

}