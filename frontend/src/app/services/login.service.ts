import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { LocalStorageService } from './localstorage.service';
import { TeacherRoles } from '../models/teacher-roles.model';

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

  access(email:string) {
      return this.http.post(environment.urlApi + "/access", email, this.httpOptionsCookiesCSRF)
      .pipe(catchError(error => this.handleError(error)));
  }

  verify(code:number): Observable<TeacherRoles> {
      return this.http.get<TeacherRoles>(environment.urlApi + "/verify/" + code, this.httpOptionsCookies)
      .pipe(catchError(error => this.handleError(error))
      ) as Observable<TeacherRoles>;
  }

  getTeacherLoggedDDBB(): Observable<TeacherRoles> {
    return this.http.get<TeacherRoles>(environment.urlApi + "/teacherLogged", this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<TeacherRoles>;
  }

  logout() {
    this.localStorageService.removeLocalStorage("teacher");
    return this.http.get(environment.urlApi + "/logout", this.httpOptionsCookies)
    .pipe(catchError(error => this.handleError(error)));
  }

  getTeacherLogged() {
    try{
      let teacher = this.localStorageService.getInLocalStorage("teacher");

      if (teacher && teacher != undefined && teacher != null) {
          return teacher;
      } else {
          return null;
      }
    }catch(e){
      return null;
    }
  }

  handleError(error: Error) {
    return throwError(() => { return error.message; });
	}
}