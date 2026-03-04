import { Component } from '@angular/core';
import { AuthService } from '../../core/services/auth.service';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.scss'
})
export class SidebarComponent {

  isAuthenticated$ = this.authService.isAuthenticated$;
  
    constructor(private authService: AuthService) {}
  
    logout(): void {
      this.authService.logout();
    }
}
