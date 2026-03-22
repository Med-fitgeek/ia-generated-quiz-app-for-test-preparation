import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { SessionService } from '../../core/services/session.service';
import { SessionResponseDto } from '../../core/models/session-response-dto.model';

@Component({
  selector: 'app-sessions-list',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './sessions-list.component.html',
  styleUrl: './sessions-list.component.scss'
})
export class SessionsListComponent implements OnInit {

  sessions: SessionResponseDto[] = [];
  loading = true;

  currentPage = 0;
  totalPages = 0;
  totalElements = 0;
  readonly pageSize = 10;

  constructor(
    private sessionService: SessionService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadPage(0);
  }

  loadPage(page: number): void {
    this.loading = true;
    this.sessionService.getAllSessions(page, this.pageSize).subscribe({
      next: (data) => {
        this.sessions = data.content;
        this.totalPages = data.totalPages;
        this.totalElements = data.totalElements;
        this.currentPage = page;
        this.loading = false;
      },
      error: () => { this.loading = false; }
    });
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.loadPage(page);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  }

  goToReview(sessionId: number): void {
    this.router.navigate(['/quiz-review', sessionId]);
  }

  get pageNumbers(): number[] {
    const range = 2;
    const start = Math.max(0, this.currentPage - range);
    const end   = Math.min(this.totalPages - 1, this.currentPage + range);
    return Array.from({ length: end - start + 1 }, (_, i) => start + i);
  }
}