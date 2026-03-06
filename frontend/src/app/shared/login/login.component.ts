import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './login.component.html'
})
export class LoginComponent {

  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  loginForm = this.fb.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]]
  });

  constructor(
    private fb: FormBuilder, 
    private authService: AuthService,
    private router: Router
    ) {}

  onSubmit() {
    if (this.loginForm.invalid) {
      return;
    }

    this.errorMessage = null;
    this.successMessage = null;
    this.loading = true;

    const credentials = this.loginForm.getRawValue();


    this.authService.login(credentials).subscribe({
      next: (res) => {
        this.successMessage = 'Connexion réussie.'; 
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = ('Email ou mot de passe invalide');
        console.error(err);
        this.loading = false;
      }
    });
  }
}
