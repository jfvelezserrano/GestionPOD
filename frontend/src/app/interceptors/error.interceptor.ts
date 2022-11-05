import { Injectable } from '@angular/core';
import {HttpEvent, HttpHandler, HttpRequest, HttpErrorResponse, HttpInterceptor} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { NavigationExtras, Router } from '@angular/router';
import { LoginService } from '../services/login.service';

@Injectable()

export class ErrorInterceptor implements HttpInterceptor {

  constructor(
    private router:Router,
    private loginService: LoginService
  ) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
      return next.handle(request).pipe(
              retry(1),
              catchError((error: HttpErrorResponse) => {

                let status;
                let errorMessage = "";

                if (error.status == 403 || error.status == 401) {
                  this.loginService.logout().subscribe(
                    response => {
                      this.router.navigate(['']);
                    }
                  );
                }
              
                if (error.error instanceof ErrorEvent) {
                    errorMessage = error.error.message;
                } else {
                    errorMessage = error.status + ";" + error.error.message;
                    status = error.status;
                }

                if(!error.error || error.error.message == " "){
                  const navigationExtras: NavigationExtras = {
                    state: {status: status, message: errorMessage}
                  };
                
                  this.router.navigate(['error'], navigationExtras);
                }

                return throwError(() => new Error(errorMessage));
              })
    )
  }
}

