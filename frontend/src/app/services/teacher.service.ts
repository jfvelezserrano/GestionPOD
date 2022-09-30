import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Teacher } from '../models/teacher.model';
import { TeacherRoles } from '../models/teacher-roles.model';
import { Observable, throwError } from 'rxjs';
import { CourseTeacher } from '../models/course-teacher.model';
import { Course } from '../models/course.model';
import { Subject } from '../models/subject.model';
import { catchError } from 'rxjs/operators';
import { SubjectTeacherConflicts } from '../models/subject-teacher-conflicts.model';

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

  getTeachersByPOD(id:any, page:number, typeSort:string): Observable<Teacher[]>{
    return this.http.get<Teacher[]>(environment.urlApi + "/pods/" + id + "/teachers?page=" + page + "&typeSort=" + typeSort, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<Teacher[]>;
  }

  createTeacher(form:any, idPod:number){
    return this.http.post(environment.urlApi + "/pods/" + idPod + "/teachers", form, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		);
  }

  getAllTeachersCurrentCourse(): Observable<TeacherRoles[]>{
    return this.http.get<TeacherRoles[]>(environment.urlApi + "/teachers", this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<TeacherRoles[]>;
  }

  joinSubject(idSubject:number, form:any){
    return this.http.post(environment.urlApi + "/teachers/join/" + idSubject, form, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		);
  }

  unjoinSubject(idSubject:number){
    return this.http.delete(environment.urlApi + "/teachers/unjoin/" + idSubject, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		);
  }

  changeRole(teacher:Teacher): Observable<TeacherRoles>{
    return this.http.put<TeacherRoles>(environment.urlApi + "/teachers/role", teacher, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<TeacherRoles>;
  }

  findByRole(role:string): Observable<TeacherRoles[]>{
    return this.http.get<TeacherRoles[]>(environment.urlApi + "/teachers?role=" + role, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<TeacherRoles[]>;
  }

  getSubjects(typeSort:string): Observable<SubjectTeacherConflicts[]>{
    return this.http.get<SubjectTeacherConflicts[]>(environment.urlApi + "/teachers/mySubjects?typeSort=" + typeSort, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<SubjectTeacherConflicts[]>;
  }

  getCourses(): Observable<Course[]>{
    return this.http.get<Course[]>(environment.urlApi + "/teachers/myCourses", this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<Course[]>;
  }

  getEditableData(): Observable<CourseTeacher>{
    return this.http.get<CourseTeacher>(environment.urlApi + "/teachers/myEditableData", this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<CourseTeacher>;
  }

  editEditableData(form:any){
    return this.http.put(environment.urlApi + "/teachers/myEditableData", form, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		);
  }

  handleError(error: Error) {
    return throwError(() => { return error.message; });
	}
}
