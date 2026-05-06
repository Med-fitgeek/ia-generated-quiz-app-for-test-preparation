import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

interface ContactForm {
  firstName: string;
  lastName:  string;
  email:     string;
  subject:   string;
  message:   string;
}

interface FormErrors {
  firstName?: string;
  lastName?:  string;
  email?:     string;
  subject?:   string;
  message?:   string;
}

@Component({
  selector: 'app-contact',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  templateUrl: './contact.component.html',
  styleUrl: './contact.component.scss'
})
export class ContactComponent {

  loading   = false;
  submitted = false;

  form: ContactForm = {
    firstName: '',
    lastName:  '',
    email:     '',
    subject:   '',
    message:   ''
  };

  errors: FormErrors = {};

  //  Validation 

  private validate(): boolean {
    this.errors = {};

    if (!this.form.firstName.trim())
      this.errors.firstName = 'Le prénom est requis.';

    if (!this.form.lastName.trim())
      this.errors.lastName = 'Le nom est requis.';

    if (!this.form.email.trim()) {
      this.errors.email = 'L\'email est requis.';
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(this.form.email)) {
      this.errors.email = 'Adresse email invalide.';
    }

    if (!this.form.subject)
      this.errors.subject = 'Veuillez sélectionner un sujet.';

    if (!this.form.message.trim()) {
      this.errors.message = 'Le message est requis.';
    } else if (this.form.message.trim().length < 20) {
      this.errors.message = 'Le message doit contenir au moins 20 caractères.';
    }

    return Object.keys(this.errors).length === 0;
  }

  //  Submit 

  onSubmit(): void {
    if (!this.validate()) return;

    this.loading = true;

    // Simule un envoi — remplace par ton appel HTTP réel
    // ex: this.contactService.send(this.form).subscribe(...)
    setTimeout(() => {
      this.loading   = false;
      this.submitted = true;
      this.resetForm();
    }, 1200);
  }

  private resetForm(): void {
    this.form   = { firstName: '', lastName: '', email: '', subject: '', message: '' };
    this.errors = {};
  }
}