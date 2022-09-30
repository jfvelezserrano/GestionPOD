import { Injectable } from '@angular/core';
import {HttpEvent, HttpHandler, HttpRequest, HttpErrorResponse, HttpInterceptor} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { NavigationExtras, Router } from '@angular/router';
import { LoginService } from '../services/login.service';
import { LocalStorageService } from '../services/localstorage.service';
import { TeacherRoles } from '../models/teacher-roles.model';

@Injectable()

export class ErrorInterceptor implements HttpInterceptor {
  public status: number ;
  public errorMessage: string;

  constructor(
    private router:Router,
    private loginService: LoginService,
    private localStorageService: LocalStorageService
  ) { }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
      return next.handle(request).pipe(
              retry(1),
              catchError((error: HttpErrorResponse) => {

                if (error.status === 403) {

                  this.loginService.getTeacherLoggedDDBB().subscribe({
                    next: (data) => {
                      let teacherLocalStorage: any = this.loginService.getTeacherLogged();

                      let isAdmin:boolean = false;

                      teacherLocalStorage.roles.forEach((value:any)=>{
                        if(value=="ADMIN") isAdmin = true;
                      });

                      data.roles.forEach((value:any)=>{
                        if((value=="ADMIN" && !isAdmin) || (value!="ADMIN" && isAdmin)){
                          this.loginService.logout().subscribe(
                            response => {
                              this.router.navigate(['']);
                            }
                          );
                        }
                      });
                    },
                    error: (error) => {
                      console.error(error);
                    }
                  });
                }

                if (error.error instanceof ErrorEvent) {
                    this.errorMessage = error.message;
                } else {
                    this.errorMessage = error.message;
                    this.status = error.status;
                }

                if((error.status != 400) && (error.status != 401) && (error.status != 403) && (error.status != 404)){

                  const navigationExtras: NavigationExtras = {
                    state: {status: this.status, message: this.errorMessage}
                  };
                
                  this.router.navigate(['error'], navigationExtras);
                  
                }

                return throwError(() => new Error(this.status.toString()));
              })
    )
  }
}

