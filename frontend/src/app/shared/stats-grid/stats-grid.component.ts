import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stats-grid',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stats-grid.component.html',
  styleUrl: './stats-grid.component.scss'
})
export class StatsGridComponent {

  @Input() totalQuizzes: number = 0;
  @Input() totalSessions: number = 0;
  @Input() averageScore: number = 0;

}