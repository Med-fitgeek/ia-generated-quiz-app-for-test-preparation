import { ApplicationConfig, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';

import { provideHttpClient, withInterceptors } from '@angular/common/http';

import { routes } from './app.routes';
import { LucideAngularModule, FileText, Brain, Zap, BookOpen, LucideCheckCheck, Clock, Shield, ChartLine, Trash,  } from 'lucide-angular';

import { authInterceptor } from './core/interceptors/auth.interceptor';
import { errorInterceptor } from './core/interceptors/error.interceptor';
import { CheckCircle, CheckCircle2 } from 'lucide';

export const appConfig: ApplicationConfig = {
  providers: [

    provideRouter(routes),

    provideHttpClient(
      withInterceptors([
        authInterceptor,
        errorInterceptor
      ])
    ),
    importProvidersFrom(
      LucideAngularModule.pick({
        FileText,
        Brain,
        Zap,
        BookOpen,
        LucideCheckCheck,
        Clock,
        Shield,
        ChartLine, 
        Trash
      })
    )
    

  ]
};