import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { environment } from 'src/environments/environment';
import { StatisticsPersonal } from '../models/statistics-personal.model';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { StatisticsMates } from '../models/statistics-mates.model';
import { SubjectNameAndQuarter } from '../models/subject-name-and-quarter.model';
import { StatisticsGraphHours } from '../models/statistics-graph-hours.model';
import { StatisticsGraphPercentage } from '../models/statistics-graph-percentage.model';
import { StatisticsGlobal } from '../models/statistics-global.model';
import { StatisticsTeacher } from '../models/statistics-teacher.model';

@Injectable({
  providedIn: 'root'
})
export class StatisticsService {

  public httpOptionsCredentials = {
    withCredentials: true
  };

  constructor(
    private http: HttpClient
  ) { }

  getPersonalStatistics(): Observable<StatisticsPersonal>{
    return this.http.get<StatisticsPersonal>(environment.urlApi + "/statistics/myData", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<StatisticsPersonal>;
  }

  getMates(): Observable<StatisticsMates[]>{
    return this.http.get<StatisticsMates[]>(environment.urlApi + "/statistics/myMates", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<StatisticsMates[]>;
  }

  getMySubjectsByCourse(idCourse:number): Observable<SubjectNameAndQuarter[]>{
    return this.http.get<SubjectNameAndQuarter[]>(environment.urlApi + "/statistics/mySubjects/" + idCourse, this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<SubjectNameAndQuarter[]>;
  }

  graphHoursPerSubject(): Observable<StatisticsGraphHours[]>{
    return this.http.get<StatisticsGraphHours[]>(environment.urlApi + "/statistics/myHoursPerSubject", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<StatisticsGraphHours[]>;
  }

  graphPercentageHours(): Observable<StatisticsGraphPercentage[]>{
    return this.http.get<StatisticsGraphPercentage[]>(environment.urlApi + "/statistics/myPercentageHoursSubjects", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<StatisticsGraphPercentage[]>;
  }

  getGlobalStatistics(): Observable<StatisticsGlobal>{
    return this.http.get<StatisticsGlobal>(environment.urlApi + "/statistics", this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<StatisticsGlobal>;
  }

  getAllTeachersStatistics(page:number, typeSort:string): Observable<StatisticsTeacher[]>{
    return this.http.get<StatisticsTeacher[]>(environment.urlApi + "/statistics/teachersStatistics?page=" + page + "&typeSort=" + typeSort, this.httpOptionsCredentials)
    .pipe(catchError(error => this.handleError(error))
		) as Observable<StatisticsTeacher[]>;
  }

  handleError(error: Error) {
    return throwError(() => { return error.message; });
	}
}
