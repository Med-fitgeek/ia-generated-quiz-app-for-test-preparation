import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { UserService } from '../../core/services/user.service';
import { QuizService } from '../../core/services/quiz.service';
import { SessionService } from '../../core/services/session.service';
import { AuthService } from '../../core/services/auth.service';
import { AvatarService, AvatarOption } from '../../core/services/avatar.service';
import { UpdateProfileRequest } from '../../core/models/update-profil-request.model';
import { UpdatePasswordRequest } from '../../core/models/update-password-request.model';

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './account.component.html',
  styleUrl: './account.component.scss'
})
export class AccountComponent implements OnInit {

  username = '';
  email = '';
  selectedAvatarId = 'a1';

  totalQuizzes = 0;
  totalSessions = 0;
  averageScore = 0;
  memberSince = '';

  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  passwordError = '';
  passwordSuccess = false;

  showAvatarPicker = false;
  profileSaved = false;
  showDeleteModal = false;
  deleteConfirmText = '';

  constructor(
    private userService: UserService,
    private quizService: QuizService,
    private sessionService: SessionService,
    private authService: AuthService,
    private router: Router,
    readonly avatarService: AvatarService
  ) {}

  ngOnInit(): void {
    this.loadUserData();
    this.loadStats();
  }

  // ── Loaders ──────────────────────────────────────────

  private loadUserData(): void {
    this.userService.getCurrentUser().subscribe(user => {
      this.username = user.username;
      this.email = user.email;
      this.memberSince = user.createdAt
        ? new Date(user.createdAt).toLocaleDateString('fr-FR', { month: 'long', year: 'numeric' })
        : '';
      this.selectedAvatarId = user.avatarId ?? 'a1';
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

  // ── Avatar ────────────────────────────────────────────

  get currentAvatar(): AvatarOption {
    return this.avatarService.getById(this.selectedAvatarId);
  }

  get avatars(): AvatarOption[] {
    return this.avatarService.avatars;
  }

  selectAvatar(id: string): void {
    this.selectedAvatarId = id;
    this.showAvatarPicker = false;
  }

  // ── Profile save ──────────────────────────────────────

  saveProfile(): void {
    const payload: UpdateProfileRequest = {
      username: this.username,
      email: this.email,
      avatarId: this.selectedAvatarId
    };

    this.userService.updateProfile(payload).subscribe({
      next: () => {
        this.profileSaved = true;
        setTimeout(() => this.profileSaved = false, 2500);
      }
    });
  }

  // ── Password ──────────────────────────────────────────

  savePassword(): void {
    this.passwordError = '';
    this.passwordSuccess = false;

    if (this.newPassword.length < 8) {
      this.passwordError = 'Le mot de passe doit contenir au moins 8 caractères.';
      return;
    }
    if (this.newPassword !== this.confirmPassword) {
      this.passwordError = 'Les mots de passe ne correspondent pas.';
      return;
    }

    const payload: UpdatePasswordRequest = {
      currentPassword: this.currentPassword,
      newPassword: this.newPassword
    };

    this.userService.updatePassword(payload).subscribe({
      next: () => {
        this.passwordSuccess = true;
        this.currentPassword = '';
        this.newPassword = '';
        this.confirmPassword = '';
        setTimeout(() => this.passwordSuccess = false, 3000);
      },
      error: () => {
        this.passwordError = 'Mot de passe actuel incorrect.';
      }
    });
  }

  // ── Delete account ────────────────────────────────────

  confirmDelete(): void {
    if (this.deleteConfirmText !== 'SUPPRIMER') return;

    this.userService.deleteAccount().subscribe({
      next: () => {
        this.authService.logout();
        this.router.navigate(['/']);
      }
    });
  }
}