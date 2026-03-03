// core/services/knowledge.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { KnowledgeNormalizedResponse } from '../models/knowledge-normalized-reponse.model';
import { StructuredTextDto } from '../models/structured-text-dto.models';
import { environment } from '../../../environments/environment.development';

@Injectable({ providedIn: 'root' })
export class KnowledgeService {
  private readonly apiUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  uploadFile(file: File): Observable<KnowledgeNormalizedResponse> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<KnowledgeNormalizedResponse>(`${this.apiUrl}/sources/upload`, formData);
  }

  uploadStructuredText(dto: StructuredTextDto): Observable<KnowledgeNormalizedResponse> {
    const formData = new FormData();
    
    formData.append('strucuturedTextDto.subject', dto.subject);
    formData.append('strucuturedTextDto.keyConcepts', dto.keyConcepts || '');
    formData.append('strucuturedTextDto.objectives', dto.objectives);
    formData.append('strucuturedTextDto.additionalNotes', dto.additionalNotes || '');

    return this.http.post<KnowledgeNormalizedResponse>(`${this.apiUrl}/sources/upload`, formData);
  }
}
