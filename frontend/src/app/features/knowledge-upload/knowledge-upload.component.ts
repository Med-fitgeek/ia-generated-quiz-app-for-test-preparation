import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { KnowledgeService } from '../../core/services/knowledge.service';
import { StructuredTextDto } from '../../core/models/structured-text-dto.models'; 

type Mode = 'file' | 'text';

@Component({
  standalone: true,
  selector: 'app-knowledge-upload',
  imports: [CommonModule, FormsModule],
  templateUrl: './knowledge-upload.component.html',
})
export class KnowledgeUploadComponent {
  mode: Mode = 'file';

  // File mode
  selectedFile: File | null = null;

  // Text mode
  textDto: StructuredTextDto = {
    subject: '',
    keyConcepts: '',
    objectives: '',
    additionalNotes: '',
  };

  // UI state
  loading = false;
  error: string | null = null;

  constructor(
    private knowledgeService: KnowledgeService,
    private router: Router
  ) {}

  setMode(mode: Mode) {
    this.mode = mode;
    this.error = null;
  }

  onFileSelected(event: Event) {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  submit() {
    this.error = null;

    if (this.mode === 'file') {
      if (!this.selectedFile) {
        this.error = 'Please select a file.';
        return;
      }

      this.loading = true;
      this.knowledgeService.uploadFile(this.selectedFile).subscribe({
        next: (res) => {
          this.loading = false;
          this.router.navigate(['/quiz-generation', res.sourceId]);
        },
        error: (err) => {
          this.loading = false;
          this.error = err?.error?.message || 'Upload failed.';
        },
      });
    } else {
      if (!this.textDto.subject || !this.textDto.objectives) {
        this.error = 'Subject and objectives are required.';
        return;
      }

      this.loading = true;
      this.knowledgeService.uploadStructuredText(this.textDto).subscribe({
        next: (res) => {
          console.log('RESPONSE:', res);
          this.loading = false;
          this.router.navigate(['/quiz-generation', res.sourceId]);
        },
        error: (err) => {
          this.loading = false;
          this.error = err?.error?.message || 'Upload failed.';
        },
      });
    }
  }
}
