import { Routes } from '@angular/router';
import { HomeComponent } from './shared/home/home.component';
import { authGuard } from './core/guards/auth.guards';
import { DashboardHomeComponent } from './shared/dashboard-home/dashboard-home.component';
import { AccountComponent } from './shared/account/account.component';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () => import('./shared/home/home.component').then(m => m.HomeComponent)
  },
  {
    path: 'register',
    loadComponent: () => import('./shared/register/register.component').then(m => m.RegisterComponent)
  },
  {
    path: 'login',
    loadComponent: () => import('./shared/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'source',
    loadComponent: () => import('./features/knowledge-upload/knowledge-upload.component').then(m => m.KnowledgeUploadComponent),
    canActivate: [authGuard]
  },
  {
    path: 'quiz-generation/:id',
    loadComponent: () => import('./features/quiz-generate/quiz-generate.component').then(m => m.QuizGenerateComponent),
    canActivate: [authGuard]
  },
  {
    path: 'quiz-player/:id',
    loadComponent: () => import('./features/quiz-player/quiz-player.component').then(m => m.QuizPlayerComponent),
    canActivate: [authGuard]
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./shared/dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard],
    children: [
      {
        path: '',
        component: DashboardHomeComponent
      },
      {
        path: 'quizzes',
        component: DashboardHomeComponent
      },
      {
        path: 'account',
        component: AccountComponent
      }
    ]
  },
  {
    path: 'quiz-review/:sessionId',
    loadComponent: () => import('./features/quiz-review/quiz-review.component').then(m => m.QuizReviewComponent),
    canActivate: [authGuard]
  }
];

