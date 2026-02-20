import { Component } from '@angular/core';
import { HeroComponent } from '../hero/hero.component';
import { ValueComponent } from '../value/value.component';
import { HowItWorkComponent } from '../how-it-work/how-it-work.component';
import { ImpactComponent } from '../impact/impact.component';
import { DemoComponent } from '../demo/demo.component';
import { ReviewComponent } from '../review/review.component';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [HeroComponent, HowItWorkComponent, ValueComponent, ImpactComponent, DemoComponent, ReviewComponent],
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent {

}
