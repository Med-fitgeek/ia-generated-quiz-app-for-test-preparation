import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss'
})
export class LoginComponent {

  loading = false;
  showPassword = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  loginForm = this.fb.nonNullable.group({
    email:    ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.errorMessage = null;
    this.successMessage = null;
    this.loading = true;

    this.authService.login(this.loginForm.getRawValue()).subscribe({
      next: () => {
        this.successMessage = 'Connexion réussie.';
        this.authService['authInitializedSubject'].next(true);
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = 'Email ou mot de passe invalide.';
        console.error(err);
        this.loading = false;
      }
    });
  }
}