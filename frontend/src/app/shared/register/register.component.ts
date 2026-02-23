import { Component } from '@angular/core';
import { FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from "@angular/router";
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './register.component.html'
})
export class RegisterComponent {

  loading = false;
  errorMessage: string | null = null;
  successMessage: string | null = null;

  registerForm = this.fb.nonNullable.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]],
    confirmPassword: ['', [Validators.required]]
  });

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (this.registerForm.invalid) {
      return;
    }

    const credentials= this.registerForm.value;

    if (credentials.password !== credentials.confirmPassword) {
      this.errorMessage = 'Les mots de passe ne correspondent pas.';
      return;
    }

    this.errorMessage = null;
    this.successMessage = null;
    this.loading = true;

    const rawCredentials = this.registerForm.getRawValue();

    this.authService.register(rawCredentials).subscribe({
      next: (res) => {
        this.authService.saveToken(res.accesToken);
        this.successMessage = 'Enregistrement réussie, bienvenue!'; 
        this.router.navigate(['/dashboard']);
      },
      error: (err) => {
        this.errorMessage = ('Email ou mot de passe invalide');
        console.error('Erreur de connexion', err);
        this.loading = false;
      }
    });
  }
}
