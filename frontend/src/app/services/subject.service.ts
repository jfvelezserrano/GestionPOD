import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
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

  public httpOptionsContentType = {
    headers: new HttpHeaders().set( 'Content-Type', "application/json"),
    withCredentials: true
  };

  public httpOptionsCredentials = {
    withCredentials: true
  };

  constructor(
    private http: HttpClient
  ) { }
  
  getSubjectsByPOD(id:number, page:number, typeSort:string):Observable<SubjectTeacherBase[]>{
    return this.http.get<SubjectTeacherBase[]>(environment.urlApi + "/pods/" + id + "/subjects?page=" + page + "&typeSort=" + typeSort, this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<SubjectTeacherBase[]>;
  }

  getTitles(): Observable<string[]>{
    return this.http.get<string[]>(environment.urlApi + "/subjects/titles", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<string[]>;
  }

  getTitlesCurrentCourse(): Observable<string[]>{
    return this.http.get<string[]>(environment.urlApi + "/subjects/currentTitles", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<string[]>;
  }

  getSubjectsCurrentCourse(): Observable<string[]>{
    return this.http.get<string[]>(environment.urlApi + "/subjects/currentSubjects", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<string[]>;
  }

  getCampus(): Observable<string[]>{
    return this.http.get<string[]>(environment.urlApi + "/subjects/campus", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<string[]>;
  }

  getTypes(): Observable<string[]>{
    return this.http.get<string[]>(environment.urlApi + "/subjects/types", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<string[]>;
  }

  getAllInCurrentCourse(): Observable<Subject[]>{
    return this.http.get<Subject[]>(environment.urlApi + "/subjects", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<Subject[]>;
  }

  createSubject(subject:Subject, idPod:number){
    return this.http.post(environment.urlApi + "/pods/" + idPod + "/subjects", subject, this.httpOptionsContentType)
    .pipe(catchError(error => this.handleError(error)));
  }

  getById(id:number): Observable<SubjectTeacherBase>{
    return this.http.get<SubjectTeacherBase>(environment.urlApi + "/subjects/" + id, this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<SubjectTeacherBase>;
  }

  getRecordSubject(id:number): Observable<Map<String, String[]>>{
    return this.http.get<Map<String, String[]>>(environment.urlApi + "/subjects/" + id + "/record", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<Map<String, String[]>>;
  }

  search(typeSort:string, occupation?:string, quarter?:string, turn?:string, title?:string, subjectName?:string, emailTeacher?:string): Observable<SubjectTeacherStatus[]>{
    let params:any = {};
    if (occupation) params.occupation = occupation;
    if (quarter) params.quarter = quarter;
    if (turn) params.turn = turn;
    if (title) params.title = title;
    if (subjectName) params.subject = subjectName;
    if (emailTeacher) params.teacher = emailTeacher;
    params.typeSort = typeSort;

    return this.http.get<SubjectTeacherStatus[]>(environment.urlApi + "/subjects/search", { 
      params: params,
      withCredentials: true
    }).pipe(catchError(error => this.handleError(error))
		) as Observable<SubjectTeacherStatus[]>; 
  }

  handleError(error: Error) {
    return throwError(() => { return error.message; });
	}
}
