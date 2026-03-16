import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './about.component.html',
  styleUrl: './about.component.scss'
})
export class AboutComponent {

  values = [
    {
      title: "Apprentissage efficace",
      text: "Nous croyons que la pratique active est la meilleure façon d'apprendre et de retenir l'information."
    },
    {
      title: "IA utile",
      text: "L'intelligence artificielle doit être un outil pour amplifier l'apprentissage humain."
    },
    {
      title: "Simplicité",
      text: "Une interface simple et rapide pour se concentrer uniquement sur l'apprentissage."
    }
  ]

}