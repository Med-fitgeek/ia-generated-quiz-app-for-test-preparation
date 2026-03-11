import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {

  return next(req).pipe(

    catchError((error: HttpErrorResponse) => {

      let message = "An unexpected error occurred";

      if (error.status === 0) {
        message = "Unable to reach server";
      }

      if (error.status === 400) {
        message = error.error || "Invalid request";
      }

      if (error.status === 401) {
        message = "Unauthorized. Please login again.";
      }

      if (error.status === 403) {
        message = "Access denied";
      }

      if (error.status === 404) {
        message = "Resource not found";
      }

      if (error.status === 429) {
        message = "Too many requests. Please wait before retrying.";
      }

      if (error.status >= 500) {
        message = "Server error. Please try again later.";
      }

      alert(message);

      return throwError(() => error);

    })

  );

};