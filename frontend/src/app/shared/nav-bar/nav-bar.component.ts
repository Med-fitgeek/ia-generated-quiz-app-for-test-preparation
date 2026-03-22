import { CommonModule } from '@angular/common';
import { Component, HostListener, OnInit } from '@angular/core';
import { RouterLink } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { UserService } from '../../core/services/user.service';
import { AvatarService } from '../../core/services/avatar.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './nav-bar.component.html',
  styleUrl: './nav-bar.component.scss'
})
export class NavBarComponent implements OnInit {

  isAuthenticated$: Observable<boolean>;
  isMenuOpen = false;
  dropdownOpen = false;

  userName = '';
  userEmail = '';
  userAvatarId = 'a1';

  constructor(
    private authService: AuthService,
    private userService: UserService,
    readonly avatarService: AvatarService
  ) {
    this.isAuthenticated$ = this.authService.isAuthenticated$;
  }

  ngOnInit(): void {
    this.isAuthenticated$.subscribe(auth => {
      if (auth) {
        this.userService.getCurrentUser().subscribe(user => {
          this.userName     = user.username;
          this.userEmail    = user.email;
          this.userAvatarId = user.avatarId ?? 'a1';
        });
      }
    });
  }

  get userAvatar() {
    return this.avatarService.getById(this.userAvatarId);
  }

  toggleMenu(): void {
    this.isMenuOpen = !this.isMenuOpen;
    if (this.isMenuOpen) this.dropdownOpen = false;
  }

  closeMenu(): void { this.isMenuOpen = false; }

  toggleDropdown(): void { this.dropdownOpen = !this.dropdownOpen; }

  closeDropdown(): void { this.dropdownOpen = false; }

  @HostListener('document:click', ['$event'])
  onDocumentClick(event: MouseEvent): void {
    if (!(event.target as HTMLElement).closest('.avatar-menu')) {
      this.dropdownOpen = false;
    }
  }

  logout(): void {
    this.authService.logout();
    this.closeMenu();
    this.closeDropdown();
  }
}