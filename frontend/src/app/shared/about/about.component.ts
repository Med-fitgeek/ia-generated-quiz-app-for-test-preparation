import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './about.component.html',
  styleUrl: './about.component.scss'
})
export class AboutComponent {

  values = [
    {
      icon: 'M12 2L2 7l10 5 10-5-10-5zM2 17l10 5 10-5M2 12l10 5 10-5',
      title: 'Apprentissage actif',
      text: 'La pratique régulière et le test de ses connaissances sont prouvément plus efficaces que la simple relecture passive.'
    },
    {
      icon: 'M9.663 17h4.673M12 3v1m6.364 1.636l-.707.707M21 12h-1M4 12H3m3.343-5.657l-.707-.707m2.828 9.9a5 5 0 117.072 0l-.548.547A3.374 3.374 0 0014 18.469V19a2 2 0 11-4 0v-.531c0-.895-.356-1.754-.988-2.386l-.548-.547z',
      title: 'IA au service de l\'humain',
      text: 'L\'intelligence artificielle doit amplifier vos capacités d\'apprentissage, pas les remplacer. Vous restez aux commandes.'
    },
    {
      icon: 'M13 10V3L4 14h7v7l9-11h-7z',
      title: 'Simplicité radicale',
      text: 'Une interface épurée pour que rien ne vienne distraire l\'essentiel : apprendre vite et bien.'
    },
    {
      icon: 'M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z',
      title: 'Mémorisation durable',
      text: 'Le système de répétition espacée intégré dans QuizGen maximise la rétention à long terme de vos connaissances.'
    }
  ];

}