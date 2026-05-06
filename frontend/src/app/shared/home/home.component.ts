import { Component } from '@angular/core';
import { HeroComponent } from '../hero/hero.component';
import { HowItWorkComponent } from '../how-it-work/how-it-work.component';
import { ImpactComponent } from '../impact/impact.component';
import { DemoComponent } from '../demo/demo.component';
import { ReviewComponent } from '../review/review.component';
import { FeaturesSectionComponent } from "../features/features.component";
import { PricingComponent } from "../pricing/pricing.component";

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [HeroComponent, HowItWorkComponent, ImpactComponent, DemoComponent, ReviewComponent, FeaturesSectionComponent, PricingComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

}
