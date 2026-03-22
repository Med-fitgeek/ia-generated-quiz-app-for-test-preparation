import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss'
})
export class RegisterComponent {

  loading = false;
  showPassword = false;
  showConfirm  = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  registerForm = this.fb.nonNullable.group({
    username:        ['', [Validators.required, Validators.minLength(3)]],
    email:           ['', [Validators.required, Validators.email]],
    password:        ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required]]
  });

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  // ── Password strength ────────────────────────────────

  get passwordStrength(): number {
    const pwd = this.registerForm.get('password')?.value ?? '';
    let score = 0;
    if (pwd.length >= 6)                        score++;
    if (pwd.length >= 10)                       score++;
    if (/[A-Z]/.test(pwd) && /\d/.test(pwd))   score++;
    return score;
  }

  get strengthLabel(): string {
    return ['', 'Faible', 'Moyen', 'Fort'][this.passwordStrength] ?? '';
  }

  // ── Submit ───────────────────────────────────────────

  onSubmit(): void {
    if (this.registerForm.invalid) {
      this.registerForm.markAllAsTouched();
      return;
    }

    const { password, confirmPassword } = this.registerForm.getRawValue();

    if (password !== confirmPassword) {
      this.errorMessage = 'Les mots de passe ne correspondent pas.';
      return;
    }

    this.errorMessage = null;
    this.successMessage = null;
    this.loading = true;

    this.authService.register(this.registerForm.getRawValue()).subscribe({
      next: () => {
        this.successMessage = 'Compte créé avec succès, bienvenue !';
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = err?.error?.message || 'Une erreur est survenue. Réessayez.';
        console.error(err);
        this.loading = false;
      }
    });
  }
}