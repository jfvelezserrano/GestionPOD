import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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

  joinSubject(idSubject:number, form:any){
    return this.http.post(environment.urlApi + "/teachers/join/" + idSubject, form, this.httpOptionsCookiesCSRF);
  }

  unjoinSubject(idSubject:number){
    return this.http.delete(environment.urlApi + "/teachers/unjoin/" + idSubject, this.httpOptionsCookiesCSRF);
  }

  changeRole(teacher:Teacher){
    return this.http.put(environment.urlApi + "/teachers/admin", teacher, this.httpOptionsCookiesCSRF);
  }

  findByRole(role:string){
    return this.http.get(environment.urlApi + "/teachers?role=" + role, this.httpOptionsCookiesCSRF);
  }

  getMySubjects(typeSort:string){
    return this.http.get(environment.urlApi + "/teachers/mySubjects?typeSort=" + typeSort, this.httpOptionsCookiesCSRF);
  }

  getMyCourses(){
    return this.http.get(environment.urlApi + "/teachers/myCourses", this.httpOptionsCookiesCSRF);
  }
}
