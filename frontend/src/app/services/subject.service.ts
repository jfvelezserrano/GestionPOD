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

  constructor(
    private http: HttpClient
  ) { }
  
  getSubjectsByPOD(id:number, page:number, typeSort:string){
    return this.http.get(environment.urlApi + "/pods/" + id + "/subjects?page=" + page + "&typeSort=" + typeSort, this.httpOptionsCookiesCSRF);
  }

  getTitles(){
    return this.http.get(environment.urlApi + "/subjects/titles", this.httpOptionsCookiesCSRF);
  }

  getTitlesCurrentCourse(){
    return this.http.get(environment.urlApi + "/subjects/currentTitles", this.httpOptionsCookiesCSRF);
  }

  getCampus(){
    return this.http.get(environment.urlApi + "/subjects/campus", this.httpOptionsCookiesCSRF);
  }

  getTypes(){
    return this.http.get(environment.urlApi + "/subjects/types" , this.httpOptionsCookiesCSRF);
  }

  getAllCurrentCourse(){
    return this.http.get(environment.urlApi + "/subjects" , this.httpOptionsCookiesCSRF);
  }

  createSubject(subject:SubjectModel, idPod:number){
    return this.http.post(environment.urlApi + "/pods/" + idPod + "/subjects", subject, this.httpOptionsCookiesCSRF);
  }

  getById(id:number){
    return this.http.get(environment.urlApi + "/subjects/" + id, this.httpOptionsCookiesCSRF);
  }

  getRecordSubject(id:number){
    return this.http.get(environment.urlApi + "/subjects/" + id + "/record", this.httpOptionsCookiesCSRF);
  }

  search(typeSort:string, occupation?:string, quarter?:string, turn?:string, title?:string, idTeacher?:number){
    var params:any = {};
    if (occupation) params.occupation = occupation;
    if (quarter) params.quarter = quarter;
    if (turn) params.turn = turn;
    if (title) params.title = title;
    if (idTeacher) params.teacher = idTeacher;
    params.typeSort = typeSort;

    return this.http.get(environment.urlApi + "/subjects/search", { 
      params: params,
      headers: new HttpHeaders({ 'X-XSRF-TOKEN': this.csrfToken}),
      withCredentials: true
    }); 
  }
}
