import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterOutlet } from '@angular/router';
import { QuizSummary } from '../../core/models/quiz-summary.model';
import { SidebarComponent } from '../sidebar/sidebar.component';
import { SessionResponseDto } from '../../core/models/session-response-dto.model';
import { SessionService } from '../../core/services/session.service';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, SidebarComponent, RouterOutlet],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.scss'
})


export class DashboardComponent  {}

