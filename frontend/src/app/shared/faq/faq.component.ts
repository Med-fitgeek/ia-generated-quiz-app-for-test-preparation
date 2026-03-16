import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

interface FaqItem {
  question: string
  answer: string
  open?: boolean
}

@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './faq.component.html',
  styleUrl: './faq.component.scss'
})
export class FaqComponent {

  faqs: FaqItem[] = [

    {
      question: "Comment fonctionne la génération de quiz ?",
      answer:
        "Notre IA analyse vos documents ou vos connaissances et génère automatiquement des questions pertinentes pour vous aider à réviser efficacement."
    },

    {
      question: "Puis-je utiliser mes propres documents ?",
      answer:
        "Oui. Vous pouvez importer des documents ou du texte et l'application génère des quiz personnalisés basés sur votre contenu."
    },

    {
      question: "Les quiz sont-ils adaptés à mon niveau ?",
      answer:
        "Oui. L'algorithme ajuste la difficulté en fonction de vos performances pour maximiser votre apprentissage."
    },

    {
      question: "Puis-je revoir mes sessions précédentes ?",
      answer:
        "Toutes vos sessions sont sauvegardées dans le dashboard afin que vous puissiez analyser vos résultats et progresser."
    },

    {
      question: "L'application est-elle gratuite ?",
      answer:
        "Une version gratuite est disponible. Des fonctionnalités avancées pourront être ajoutées dans une future version premium."
    }

  ]

  toggle(index: number) {
    this.faqs[index].open = !this.faqs[index].open
  }

}