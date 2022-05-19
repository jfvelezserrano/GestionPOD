import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { LocalStorageService } from './localstorage.service';

@Injectable({
  providedIn: 'root'
})

export class LoginService {
  public csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');

  public httpOptionsCookiesCSRF = {
    headers: new HttpHeaders({ 'X-XSRF-TOKEN': this.csrfToken }),
    withCredentials: true
  };

  public httpOptionsCookies = {
    withCredentials: true
  };

  constructor(
      private http: HttpClient,
      private localStorageService: LocalStorageService
  ) {
  }

  access(email:string): Observable<any> {
      return this.http.post(environment.urlApi + "/access", email, this.httpOptionsCookiesCSRF).pipe(			
        catchError(error => this.handleError(error))
      );
  }

  verify(code:number): Observable<any> {
      return this.http.get(environment.urlApi + "/verify/" + code, this.httpOptionsCookies).pipe(			
        catchError(error => this.handleError(error))
      );
    }

  logout() {
    return this.http.get(environment.urlApi + "/logout", this.httpOptionsCookies);
  }

  getTeacherLogged() {
    let teacher = this.localStorageService.getInLocalStorage("teacher");

    if (teacher && teacher != null && teacher != undefined) {
        return teacher;
    } else {
        return null;
    }
  }

  private handleError(error: HttpErrorResponse) {
    if (error.status != 0) {
      console.error(`${error.status}, el error es: `, error.error);
    }
    return throwError(() => new Error('Algo no ha ido bien, pruebe más tarde'));
  }
}