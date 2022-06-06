import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Teacher } from '../models/teacher';

@Injectable({
  providedIn: 'root'
})
export class TeacherService {

  public csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');

  public httpOptionsCookiesCSRF = {
    headers: new HttpHeaders({ 'X-XSRF-TOKEN': this.csrfToken}),
    withCredentials: true
  };

  constructor(
    private http: HttpClient
  ) { }

  getTeachersByPOD(id:any, page:number, typeSort:string){
    return this.http.get(environment.urlApi + "/pods/" + id + "/teachers?page=" + page + "&typeSort=" + typeSort, this.httpOptionsCookiesCSRF);
  }

  createTeacher(form:any, idPod:number){
    return this.http.post(environment.urlApi + "/pods/" + idPod + "/teachers", form, this.httpOptionsCookiesCSRF);
  }

  getAllTeachersCurrentCourse(){
    return this.http.get(environment.urlApi + "/teachers", this.httpOptionsCookiesCSRF);
  }

  editTeacher(teacher:Teacher){
    return this.http.put(environment.urlApi + "/teachers", teacher, this.httpOptionsCookiesCSRF);
  }

  findByRole(role:string){
    return this.http.get(environment.urlApi + "/teachers?role=" + role, this.httpOptionsCookiesCSRF);
  }

}
