import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { AvatarService } from '../../core/services/avatar.service';

@Component({
  selector: 'app-dashboard-header',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './dashboard-header.component.html',
  styleUrl: './dashboard-header.component.scss'
})
export class DashboardHeaderComponent {

  @Input() userName: string = '';
  @Input() avatarId: string = 'a1';
  @Output() newQuiz = new EventEmitter<void>();

  constructor(private avatarService: AvatarService) {}

  get avatar() {
    return this.avatarService.getById(this.avatarId);
  }

  onNewQuiz(): void {
    this.newQuiz.emit();
  }
}