import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CourseService {

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

  createPOD(pod:FormData) {
    return this.http.post(environment.urlApi + "/pods", pod, this.httpOptionsCookiesCSRF);
  }

  getPODs(){
    return this.http.get(environment.urlApi + "/pods", this.httpOptionsCookiesCSRF);
  }

  deleteTeacherInPod(idPod:any, idTeacher:any){
    return this.http.put(environment.urlApi + "/pods/" + idPod + "/teachers/" + idTeacher, this.httpOptionsCookiesCSRF);
  }

  deleteSubjectInPod(idPod:any, idSubject:any){
    return this.http.put(environment.urlApi + "/pods/" + idPod + "/subjects/" + idSubject, this.httpOptionsCookiesCSRF);
  }
}
