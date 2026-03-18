import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface Review {
  initials: string;
  name: string;
  role: string;
  text: string;
  gradient: string;
  stars: number;
}

@Component({
  selector: 'app-review',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './review.component.html',
  styleUrl: './review.component.scss'
})
export class ReviewComponent {

  reviews: Review[] = [
    {
      initials: 'ML',
      name: 'Marie L.',
      role: 'Étudiante en Licence',
      gradient: 'linear-gradient(135deg, #4e6ccd, #4ECDC4)',
      stars: 5,
      text: 'J\'ai généré un quiz de 25 questions à partir de mon cours en quelques secondes. Le rapport m\'a montré exactement où je devais réviser. Je n\'utilise plus rien d\'autre.'
    },
    {
      initials: 'TK',
      name: 'Thomas K.',
      role: 'Formateur indépendant',
      gradient: 'linear-gradient(135deg, #f97316, #ef4444)',
      stars: 5,
      text: 'Je crée désormais mes évaluations en 1/10 du temps. Les questions sont pertinentes, bien réparties et mes apprenants progressent visiblement plus vite.'
    },
    {
      initials: 'SN',
      name: 'Sophie N.',
      role: 'Responsable pédagogique',
      gradient: 'linear-gradient(135deg, #8b5cf6, #ec4899)',
      stars: 5,
      text: 'Le rapport par thème change tout. On voit immédiatement les lacunes des apprenants et on peut adapter le programme en conséquence.'
    },
    {
      initials: 'AR',
      name: 'Antoine R.',
      role: 'Étudiant en Master',
      gradient: 'linear-gradient(135deg, #22c55e, #16a34a)',
      stars: 5,
      text: 'J\'ai utilisé QuizGen pour préparer mes partiels. Le mode répétition espacée est une révélation — mes résultats ont augmenté de façon significative.'
    },
    {
      initials: 'CL',
      name: 'Clara L.',
      role: 'Professeure de lycée',
      gradient: 'linear-gradient(135deg, #0ea5e9, #6366f1)',
      stars: 5,
      text: 'Mes élèves adorent le format interactif. La génération automatique me fait gagner des heures de préparation chaque semaine.'
    },
    {
      initials: 'MD',
      name: 'Marc D.',
      role: 'Directeur de formation',
      gradient: 'linear-gradient(135deg, #f59e0b, #ef4444)',
      stars: 5,
      text: 'On a intégré QuizGen dans notre LMS. L\'onboarding de nos nouveaux collaborateurs est bien plus efficace. Un outil vraiment bien pensé.'
    },
  ];

  get starsArray(): number[] {
    return [1, 2, 3, 4, 5];
  }

}