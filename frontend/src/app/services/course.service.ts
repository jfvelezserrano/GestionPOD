import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Course } from '../models/course.model';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CourseService {

  public httpOptionsCredentials = {
    withCredentials: true
  };

  constructor(
    private http: HttpClient
  ) { }

  createPOD(pod:FormData) {
    return this.http.post(environment.urlApi + "/pods", pod, this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		);
  }

  getPODs(): Observable<Course[]>{
    return this.http.get<Course[]>(environment.urlApi + "/pods", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<Course[]>;
  }

  getCurrentCourse(): Observable<Course>{
    return this.http.get<Course>(environment.urlApi + "/pods/currentCourse", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<Course>;
  }

  deleteTeacherInPod(idPod:number, idTeacher:number){
    return this.http.delete(environment.urlApi + "/pods/" + idPod + "/teachers/" + idTeacher, this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		);
  }

  deleteSubjectInPod(idPod:number, idSubject:number){
    return this.http.delete(environment.urlApi + "/pods/" + idPod + "/subjects/" + idSubject, this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		);
  }

  deletePod(id: number) {
    return this.http.delete(environment.urlApi + "/pods/" + id, this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		);
  }

  exportCSV(): Observable<any> {
    let headers = new HttpHeaders();
    headers = headers.append('Accept', 'text/csv; charset=UTF-8;');
    
    return this.http.get(environment.urlApi + "/pods/exportCSV", {
      withCredentials: true,
      headers: headers,
      observe: 'response',
      responseType: 'blob'
    }).pipe(catchError(error => this.handleError(error)));
  }

  handleError(error: Error) {
    return throwError(() => { return error.message; });
	}
}
