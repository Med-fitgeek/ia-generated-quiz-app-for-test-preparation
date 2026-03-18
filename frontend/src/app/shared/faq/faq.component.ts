import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';

interface FaqItem {
  question: string;
  answer: string;
  open?: boolean;
}

@Component({
  selector: 'app-faq',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './faq.component.html',
  styleUrl: './faq.component.scss'
})
export class FaqComponent {

  faqs: FaqItem[] = [
    {
      question: 'Comment fonctionne la génération de quiz ?',
      answer:
        'Notre IA analyse vos documents ou textes et génère automatiquement des questions pertinentes et variées. Elle identifie les concepts clés, les définitions importantes et les relations entre les idées pour créer des questions qui testent réellement votre compréhension.'
    },
    {
      question: 'Quels types de documents puis-je importer ?',
      answer:
        'Vous pouvez importer des PDF, des fichiers texte ou simplement coller du contenu directement dans l\'interface. QuizGen prend en charge les cours universitaires, les articles, les fiches de révision et tout autre contenu structuré.'
    },
    {
      question: 'Les quiz s\'adaptent-ils à mon niveau ?',
      answer:
        'Oui. L\'algorithme analyse vos performances au fil des sessions et ajuste progressivement la difficulté des questions. Les notions maîtrisées sont moins fréquentes, tandis que les points faibles reviennent plus souvent — c\'est le principe de la répétition espacée.'
    },
    {
      question: 'Puis-je revoir mes sessions précédentes ?',
      answer:
        'Toutes vos sessions sont sauvegardées dans le dashboard avec le détail de chaque réponse. Vous pouvez consulter votre score, identifier les erreurs et relancer une session sur les questions ratées uniquement.'
    },
    {
      question: 'Mes documents sont-ils en sécurité ?',
      answer:
        'Vos fichiers sont utilisés uniquement pour générer vos quiz et ne sont jamais partagés ou utilisés pour entraîner nos modèles. Vous pouvez supprimer vos données à tout moment depuis les paramètres de votre compte.'
    },
    {
      question: 'L\'application est-elle gratuite ?',
      answer:
        'Une version gratuite est disponible sans limite de durée. Des fonctionnalités avancées — comme l\'analyse détaillée des performances ou l\'accès illimité aux documents — seront disponibles dans une future version premium.'
    }
  ];

  toggle(index: number): void {
    this.faqs[index].open = !this.faqs[index].open;
  }

}