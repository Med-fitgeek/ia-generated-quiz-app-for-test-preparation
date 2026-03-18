import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { UserService } from '../../core/services/user.service';
import { QuizService } from '../../core/services/quiz.service';
import { SessionService } from '../../core/services/session.service';
import { AuthService } from '../../core/services/auth.service';

export interface AvatarOption {
  id: string;
  emoji: string;
  gradient: string;
}

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './account.component.html',
  styleUrl: './account.component.scss'
})
export class AccountComponent implements OnInit {

  // ── User data ────────────────────────────────────
  username = '';
  email = '';
  selectedAvatarId = 'a1';

  // ── Stats ────────────────────────────────────────
  totalQuizzes = 0;
  totalSessions = 0;
  averageScore = 0;
  memberSince = '';

  // ── Password form ────────────────────────────────
  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  passwordError = '';
  passwordSuccess = false;

  // ── UI state ─────────────────────────────────────
  showAvatarPicker = false;
  profileSaved = false;
  showDeleteModal = false;
  deleteConfirmText = '';

  // ── Avatar gallery ───────────────────────────────
  avatars: AvatarOption[] = [
    { id: 'a1',  emoji: '🦊', gradient: 'linear-gradient(135deg, #f97316, #ef4444)' },
    { id: 'a2',  emoji: '🐺', gradient: 'linear-gradient(135deg, #6366f1, #8b5cf6)' },
    { id: 'a3',  emoji: '🦁', gradient: 'linear-gradient(135deg, #f59e0b, #f97316)' },
    { id: 'a4',  emoji: '🐸', gradient: 'linear-gradient(135deg, #22c55e, #16a34a)' },
    { id: 'a5',  emoji: '🐙', gradient: 'linear-gradient(135deg, #ec4899, #a21caf)' },
    { id: 'a6',  emoji: '🦋', gradient: 'linear-gradient(135deg, #4e6ccd, #4ECDC4)' },
    { id: 'a7',  emoji: '🐻', gradient: 'linear-gradient(135deg, #92400e, #d97706)' },
    { id: 'a8',  emoji: '🦄', gradient: 'linear-gradient(135deg, #f472b6, #818cf8)' },
    { id: 'a9',  emoji: '🐬', gradient: 'linear-gradient(135deg, #0ea5e9, #06b6d4)' },
    { id: 'a10', emoji: '🦉', gradient: 'linear-gradient(135deg, #84cc16, #15803d)' },
    { id: 'a11', emoji: '🐉', gradient: 'linear-gradient(135deg, #dc2626, #7c3aed)' },
    { id: 'a12', emoji: '🤖', gradient: 'linear-gradient(135deg, #374151, #6366f1)' },
  ];

  constructor(
    private userService: UserService,
    private quizService: QuizService,
    private sessionService: SessionService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.loadStats();
  }

  // ── Loaders ──────────────────────────────────────

  private loadUserData(): void {
    this.userService.getCurrentUser().subscribe(user => {
      this.username = user.username;
      this.email = user.email;
      this.memberSince = user.createdAt
        ? new Date(user.createdAt).toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })
        : '';
    });
  }

  private loadStats(): void {
    this.quizService.getAllQuizzes().subscribe(page => {
      this.totalQuizzes = page.totalElements;
    });

    this.sessionService.getAllSessions().subscribe(page => {
      this.totalSessions = page.totalElements;
      if (page.content.length > 0) {
        const total = page.content.reduce((sum, s) => sum + (s.rate ?? 0), 0);
        this.averageScore = Math.round(total / page.content.length);
      }
    });
  }

  // ── Avatar ───────────────────────────────────────

  get currentAvatar(): AvatarOption {
    return this.avatars.find(a => a.id === this.selectedAvatarId) ?? this.avatars[0];
  }

  selectAvatar(id: string): void {
    this.selectedAvatarId = id;
    this.showAvatarPicker = false;
  }

  // ── Profile save ─────────────────────────────────

  saveProfile(): void {
    this.userService.updateUser({
      username: this.username,
      email: this.email,
    }).subscribe({
      next: () => {
        this.profileSaved = true;
        setTimeout(() => this.profileSaved = false, 2500);
      }
    });
  }

  // ── Password ─────────────────────────────────────

  savePassword(): void {
    
  }

  // ── Delete account ───────────────────────────────

  confirmDelete(): void {
    
  }

}