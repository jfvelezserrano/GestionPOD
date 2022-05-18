import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable } from 'rxjs';
import { Teacher } from '../models/teacher';
import { LoginRequest } from '../models/login-request';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})

export class LoginService {
  public teacher:any;
  private _roleTeacherLogged: any;
  public csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');

  public httpOptionsCookiesCSRF = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json', 'X-XSRF-TOKEN': this.csrfToken }),
    withCredentials: true
  };

  public httpOptionsCookies = {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
    withCredentials: true
  };

  constructor(
      private _http: HttpClient
  ) {
  }

  access(email:string): Observable<any> {
      return this._http.post(environment.urlApi + "/access", email, this.httpOptionsCookiesCSRF);
  }

  verify(code:number): Observable<any> {
      return this._http.get(environment.urlApi + "/verify/" + code, this.httpOptionsCookies);
    }

  getTeacherLogged() {
    let teacher = JSON.parse(localStorage.getItem('teacher') || '{}');

    if (teacher && teacher != null && teacher != undefined && teacher != 'undefined') {
        return teacher;
    } else {
        return null;
    }
  }

  logout() {
    return this._http.get(environment.urlApi + "/logout", this.httpOptionsCookiesCSRF);
  }
}