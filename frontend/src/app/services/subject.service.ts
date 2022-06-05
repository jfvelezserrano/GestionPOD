import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, JsonpClientBackend } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { SubjectModel } from '../models/subject';

@Injectable({
  providedIn: 'root'
})
export class SubjectService {

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
  
  getSubjectsByPOD(id:any, page:number, typeSort:string){
    return this.http.get(environment.urlApi + "/pods/" + id + "/subjects?page=" + page + "&typeSort=" + typeSort, this.httpOptionsCookiesCSRF);
  }

  getTitles(){
    return this.http.get(environment.urlApi + "/subjects/titles", this.httpOptionsCookiesCSRF);
  }

  getCampus(){
    return this.http.get(environment.urlApi + "/subjects/campus", this.httpOptionsCookiesCSRF);
  }

  getTypes(){
    return this.http.get(environment.urlApi + "/subjects/types" , this.httpOptionsCookiesCSRF);
  }

  createSubject(subject:SubjectModel, idPod:number){
    return this.http.post(environment.urlApi + "/pods/" + idPod + "/subjects", subject, this.httpOptionsCookiesCSRF);
  }
}
