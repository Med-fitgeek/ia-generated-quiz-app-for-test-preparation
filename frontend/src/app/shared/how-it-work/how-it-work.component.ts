import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { LucideAngularModule } from "lucide-angular";

@Component({
  selector: 'app-how-it-work',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './how-it-work.component.html',
  styleUrls: ['./how-it-work.component.scss']
})
export class HowItWorkComponent {

    steps = [
    {
      icon: 'file-text',
      title: 'Importez votre contenu',
      description:
        'Déposez vos documents de cours ou collez directement votre texte',
      step: 1
    },
    {
      icon: 'brain',
      title: "L'IA génère votre contenu",
      description:
        'Notre intelligence artificielle analyse votre contenu et crée quiz, flashcards ou fiches',
      step: 2
    },
    {
      icon: 'LucideCheckCheck',
      title: 'Testez vos connaissances',
      description:
        'Répondez aux questions et suivez votre progression',
      step: 3
    },
    {
      icon: 'chart-line',
      title: 'Analysez vos résultats',
      description:
        'Consultez votre rapport détaillé et identifiez vos points à améliorer',
      step: 4
    }
  ];
}
