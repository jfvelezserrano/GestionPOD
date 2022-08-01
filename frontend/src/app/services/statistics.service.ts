import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { Teacher } from '../models/teacher';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {

  public csrfToken = document.cookie.replace(/(?:(?:^|.*;\s*)XSRF-TOKEN\s*\=\s*([^;]*).*$)|^.*$/, '$1');

  public httpOptionsCookiesCSRF = {
    headers: new HttpHeaders({ 'X-XSRF-TOKEN': this.csrfToken}),
    withCredentials: true
  };

  constructor(
    private http: HttpClient
  ) { }

  getPersonalStatistics(){
    return this.http.get(environment.urlApi + "/statistics", this.httpOptionsCookiesCSRF);
  }

  getMates(){
    return this.http.get(environment.urlApi + "/statistics/myMates", this.httpOptionsCookiesCSRF);
  }

  getMySubjectsByCourse(idCourse:number){
    return this.http.get(environment.urlApi + "/statistics/mySubjects/" + idCourse, this.httpOptionsCookiesCSRF);
  }

  graphHoursPerSubject(){
    return this.http.get(environment.urlApi + "/statistics/hoursPerSubject", this.httpOptionsCookiesCSRF);
  }

  graphPercentageHours(){
    return this.http.get(environment.urlApi + "/statistics/percentageHoursSubjects", this.httpOptionsCookiesCSRF);
  }
}