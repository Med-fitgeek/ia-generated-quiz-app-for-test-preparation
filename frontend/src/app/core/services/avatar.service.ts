import { Injectable } from '@angular/core';

export interface AvatarOption {
  id: string;
  emoji: string;
  gradient: string;
}

@Injectable({ providedIn: 'root' })
export class AvatarService {

  readonly avatars: AvatarOption[] = [
    { id: 'a1',  emoji: '🦊', gradient: 'linear-gradient(135deg, #f97316, #ef4444)' },
    { id: 'a2',  emoji: '🐺', gradient: 'linear-gradient(135deg, #6366f1, #8b5cf6)' },
    { id: 'a3',  emoji: '🦁', gradient: 'linear-gradient(135deg, #f59e0b, #f97316)' },
    { id: 'a4',  emoji: '🐸', gradient: 'linear-gradient(135deg, #22c55e, #16a34a)' },
    { id: 'a5',  emoji: '🐙', gradient: 'linear-gradient(135deg, #ec4899, #a21caf)' },
    { id: 'a6',  emoji: '🦋', gradient: 'linear-gradient(135deg, #4e6ccd, #4ECDC4)' },
    { id: 'a7',  emoji: '🐻', gradient: 'linear-gradient(135deg, #92400e, #d97706)' },
    { id: 'a8',  emoji: '🦄', gradient: 'linear-gradient(135deg, #f472b6, #818cf8)' },
    { id: 'a9',  emoji: '🐬', gradient: 'linear-gradient(135deg, #0ea5e9, #06b6d4)' },
    { id: 'a10', emoji: '🦉', gradient: 'linear-gradient(135deg, #84cc16, #15803d)' },
    { id: 'a11', emoji: '🐉', gradient: 'linear-gradient(135deg, #dc2626, #7c3aed)' },
    { id: 'a12', emoji: '🤖', gradient: 'linear-gradient(135deg, #374151, #6366f1)' },
  ];

  getById(id: string): AvatarOption {
    return this.avatars.find(a => a.id === id) ?? this.avatars[0];
  }

  getEmoji(id: string): string {
    return this.getById(id).emoji;
  }

  getGradient(id: string): string {
    return this.getById(id).gradient;
  }
}