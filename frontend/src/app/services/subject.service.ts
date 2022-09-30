import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders, JsonpClientBackend } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Subject } from '../models/subject.model';
import { catchError } from 'rxjs/operators';
import { SubjectTeacherBase } from '../models/subject-teacher-base.model';
import { SubjectTeacherStatus } from '../models/subject-teacher-status.model';

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
  
  getSubjectsByPOD(id:number, page:number, typeSort:string):Observable<SubjectTeacherBase[]>{
    return this.http.get<SubjectTeacherBase[]>(environment.urlApi + "/pods/" + id + "/subjects?page=" + page + "&typeSort=" + typeSort, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<SubjectTeacherBase[]>;
  }

  getTitles(): Observable<string[]>{
    return this.http.get<string[]>(environment.urlApi + "/subjects/titles", this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<string[]>;
  }

  getTitlesCurrentCourse(): Observable<string[]>{
    return this.http.get<string[]>(environment.urlApi + "/subjects/currentTitles", this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<string[]>;
  }

  getCampus(): Observable<string[]>{
    return this.http.get<string[]>(environment.urlApi + "/subjects/campus", this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<string[]>;
  }

  getTypes(): Observable<string[]>{
    return this.http.get<string[]>(environment.urlApi + "/subjects/types" , this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<string[]>;
  }

  getAllInCurrentCourse(): Observable<Subject[]>{
    return this.http.get<Subject[]>(environment.urlApi + "/subjects" , this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<Subject[]>;
  }

  createSubject(subject:Subject, idPod:number){
    return this.http.post(environment.urlApi + "/pods/" + idPod + "/subjects", subject, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error)));
  }

  getById(id:number): Observable<SubjectTeacherBase>{
    return this.http.get<SubjectTeacherBase>(environment.urlApi + "/subjects/" + id, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<SubjectTeacherBase>;
  }

  getRecordSubject(id:number): Observable<Map<String, String[]>>{
    return this.http.get<Map<String, String[]>>(environment.urlApi + "/subjects/" + id + "/record", this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<Map<String, String[]>>;
  }

  search(typeSort:string, occupation?:string, quarter?:string, turn?:string, title?:string, idTeacher?:number): Observable<SubjectTeacherStatus[]>{
    var params:any = {};
    if (occupation) params.occupation = occupation;
    if (quarter) params.quarter = quarter;
    if (turn) params.turn = turn;
    if (title) params.title = title;
    if (idTeacher) params.teacher = idTeacher;
    params.typeSort = typeSort;

    return this.http.get<SubjectTeacherStatus[]>(environment.urlApi + "/subjects/search", { 
      params: params,
      headers: new HttpHeaders({ 'X-XSRF-TOKEN': this.csrfToken}),
      withCredentials: true
    }).pipe(catchError(error => this.handleError(error))
		) as Observable<SubjectTeacherStatus[]>; 
  }

  handleError(error: Error) {
    return throwError(() => { return error.message; });
	}
}
