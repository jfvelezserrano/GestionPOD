import { Injectable } from '@angular/core';
import {HttpEvent, HttpHandler, HttpRequest, HttpErrorResponse, HttpInterceptor} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { NavigationExtras, Router } from '@angular/router';
import { LoginService } from '../services/login.service';
import { LocalStorageService } from '../services/localstorage.service';
import { Teacher } from '../models/teacher';

@Injectable()

export class ErrorIntercept implements HttpInterceptor {
  public status:any | undefined;
  public errorMessage:any | undefined;

  constructor(
    private router:Router,
    private loginService: LoginService,
    private localStorageService: LocalStorageService
  ) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<unknown>> {
      return next.handle(request).pipe(
              retry(1),
              catchError((error: HttpErrorResponse) => {

                if (error.status === 403) {
                  let teacher:Teacher|any = this.loginService.getTeacherLogged();

                  teacher.roles.forEach((value:any,index: any)=>{
                    if(value=="ADMIN") teacher.roles.splice(index,1);
                  });

                  this.localStorageService.setInLocalStorage("teacher", teacher);
                }

                if (error.error instanceof ErrorEvent) {
                    this.errorMessage = error.error;
                } else {
                    this.errorMessage = error.error;
                    this.status = error.status;
                }

                const navigationExtras: NavigationExtras = {
                  state: {status: this.status, message: this.errorMessage}
                };

                this.router.navigate(['error'], navigationExtras);

                return throwError(() => new Error(error.error));
              })
    )
  }
}

