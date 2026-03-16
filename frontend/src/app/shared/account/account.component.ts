import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';

@Component({
  selector: 'app-account',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './account.component.html',
  styleUrl: './account.component.scss'
})
export class AccountComponent implements OnInit {

  loading = true;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  form = this.fb.nonNullable.group({
    username: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]]
  });

  constructor(
    private fb: FormBuilder,
    private userService: UserService
  ) {}

  ngOnInit(): void {

    this.userService.getCurrentUser().subscribe({
      next: (user) => {

        this.form.patchValue({
          username: user.username,
          email: user.email
        });

        this.loading = false;

      },
      error: () => {
        this.errorMessage = "Impossible de charger le profil";
        this.loading = false;
      }
    });

  }

  save() {

    if (this.form.invalid) return;

    this.successMessage = null;
    this.errorMessage = null;

    this.userService.updateUser(this.form.getRawValue())
      .subscribe({

        next: () => {
          this.successMessage = "Profil mis à jour avec succès";
        },

        error: () => {
          this.errorMessage = "Erreur lors de la mise à jour";
        }

      });

  }

}