import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { LucideAngularModule} from 'lucide-angular';

@Component({
  selector: 'app-features',
  standalone: true,
  imports: [CommonModule, LucideAngularModule],
  templateUrl: './features.component.html',
  styleUrl: './features.component.scss'
})
export class FeaturesSectionComponent {

  features = [
    {
      icon: 'brain',
      title: 'IA Avancée',
      description: 'Questions pertinentes générées automatiquement à partir de vos documents',
      color: 'blue'
    },
    {
      icon: 'zap',
      title: 'Rapide & Efficace',
      description: 'Créez du contenu pédagogique en quelques secondes pour tester vos connaissances',
      color: 'purple'
    },
    {
      icon: 'book-open',
      title: 'Apprentissage Adaptatif',
      description: "Du contenu qui s'adapte à votre niveau et à vos besoins",
      color: 'green'
    },
    {
      icon: 'LucideCheckCheck',
      title: 'Validation Instantanée',
      description: 'Obtenez un retour immédiat sur vos réponses',
      color: 'orange'
    },
    {
      icon: 'clock',
      title: 'Gain de Temps',
      description: 'Plus besoin de créer manuellement votre contenu de révision',
      color: 'pink'
    },
    {
      icon: 'shield',
      title: 'Sécurisé',
      description: 'Vos données sont chiffrées et protégées en permanence',
      color: 'indigo'
    }
  ];

}