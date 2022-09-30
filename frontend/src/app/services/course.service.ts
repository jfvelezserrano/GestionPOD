import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Course } from '../models/course.model';
import { catchError } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class CourseService {
  
  public csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');

  public httpOptionsCookiesCSRF = {
    headers: new HttpHeaders({ 'X-XSRF-TOKEN': this.csrfToken}),
    withCredentials: true
  };

  public httpOptionsCookiesCSRFCSV = {
    headers: new HttpHeaders({ 'X-XSRF-TOKEN': this.csrfToken, observe:'response', responseType:'blob'}),
    withCredentials: true
  };


  public httpOptionsCookies = {
    withCredentials: true
  };

  constructor(
    private http: HttpClient
  ) { }

  createPOD(pod:FormData) {
    return this.http.post(environment.urlApi + "/pods", pod, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		);
  }

  getPODs(): Observable<Course[]>{
    return this.http.get<Course[]>(environment.urlApi + "/pods", this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<Course[]>;
  }

  deleteTeacherInPod(idPod:any, idTeacher:any){
    return this.http.delete(environment.urlApi + "/pods/" + idPod + "/teachers/" + idTeacher, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		);
  }

  deleteSubjectInPod(idPod:number, idSubject:number){
    return this.http.delete(environment.urlApi + "/pods/" + idPod + "/subjects/" + idSubject, this.httpOptionsCookiesCSRF)
    .pipe(catchError(error => this.handleError(error))
		);
  }

  deletePod(id: any) {
    return this.http.delete(environment.urlApi + "/pods/" + id, this.httpOptionsCookiesCSRF)
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
