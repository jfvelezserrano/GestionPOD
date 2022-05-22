import { Injectable } from '@angular/core';
import {HttpEvent, HttpHandler, HttpRequest, HttpErrorResponse, HttpInterceptor} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { NavigationExtras, Router } from '@angular/router';

@Injectable()

export class ErrorIntercept implements HttpInterceptor {
  public status:any | undefined;
  public errorMessage:any | undefined;

  constructor(
    private router:Router
  ) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<unknown>> {
      return next.handle(request).pipe(
              retry(1),
              catchError((error: HttpErrorResponse) => {

                if (error.error instanceof ErrorEvent) {
                    this.errorMessage = error.error.message;
                } else {
                    this.errorMessage = error.error;
                    this.status = error.status;
                }

                const navigationExtras: NavigationExtras = {
                  state: {status: this.status, message: this.errorMessage}
                };

                this.router.navigate(['error'], navigationExtras);

                return throwError(() => new Error());
              })
          )
  }
}

