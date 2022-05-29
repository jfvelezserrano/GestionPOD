import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class TeacherService {

  public csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');

  public httpOptionsCookiesCSRF = {
    headers: new HttpHeaders({ 'X-XSRF-TOKEN': this.csrfToken}),
    withCredentials: true
  };


  public httpOptionsCookies = {
    withCredentials: true
  };

  constructor(
    private http: HttpClient
  ) { }

  getTeachersByPOD(id:any){
    return this.http.get(environment.urlApi + "/pods/" + id + "/teachers", this.httpOptionsCookiesCSRF);
  }
}
