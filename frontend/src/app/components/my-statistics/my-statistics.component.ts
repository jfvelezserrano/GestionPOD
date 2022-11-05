import { Component, OnInit, Input} from '@angular/core';
import { StatisticsService } from 'src/app/services/statistics.service';
import { TeacherService } from 'src/app/services/teacher.service';
import Chart from 'chart.js/auto';
import { NgForm } from '@angular/forms';
import { StatisticsPersonal } from 'src/app/models/statistics-personal.model';
import { StatisticsMates } from 'src/app/models/statistics-mates.model';
import { Course } from 'src/app/models/course.model';
import { CourseTeacher } from 'src/app/models/course-teacher.model';
import { SubjectNameAndQuarter } from 'src/app/models/subject-name-and-quarter.model';
import { StatisticsGraphHours } from 'src/app/models/statistics-graph-hours.model';
import { StatisticsGraphPercentage } from 'src/app/models/statistics-graph-percentage.model';
import { BehaviorSubject } from 'rxjs';


@Component({
  selector: 'app-my-statistics',
  templateUrl: './my-statistics.component.html',
  styleUrls: ['./my-statistics.component.css']
})
export class MyStatisticsComponent implements OnInit {
  public personalStatistics: StatisticsPersonal;
  public mates: StatisticsMates[] = [];
  public courseChosen: number;
  public courses: Course[] = [];
  public subjects: SubjectNameAndQuarter[] = [];
  public editableData: CourseTeacher;

  public dataGraphsHours: StatisticsGraphHours[] = [];
  public subjectsGraphHours:  string[] = [];
  public myHoursSubjectsGraphHours: number[] = [];
  public hoursSubjectsGraphHours: number[] = [];

  public dataGraphsPercentage: StatisticsGraphPercentage[] = [];
  public subjectsGraphPercentage: string[] = [];
  public percentagesGraphPercentage: number[] = [];

  public chartHours: Chart;
  public chartPercentage: Chart;

  public showLoaderCourse: boolean;
  public isCourse: boolean;
  public testEmitter: BehaviorSubject<boolean>;

  constructor(
    private statisticsService: StatisticsService,
    private teacherService: TeacherService
  ) {
    this.editableData = new CourseTeacher(0, "");
    this.showLoaderCourse = true;
    this.testEmitter = new BehaviorSubject<boolean>(this.isCourse);
  }

  ngOnInit(): void {
    this.getPersonalStatistics();
    this.testEmitter.subscribe(data => {
      if(data != undefined && data){
        this.getMates();
      this.getCourses();
      this.getEditableData();
      };
    })
  }

  ngAfterViewInit():void{
    this.testEmitter.subscribe(data => {
      if(data != undefined && data){
        this.getHoursPerSubject();
        this.getPercentageHours();
      };
    })
  }

  getPersonalStatistics() {
    this.showLoaderCourse = true;
    this.statisticsService.getPersonalStatistics()
    .subscribe({
      next: (data) => {
        this.personalStatistics = data;
        this.showLoaderCourse = false;
        this.isCourse = true;
        this.testEmitter.next(this.isCourse);
      },
      error: (error) => {
        this.showLoaderCourse = false;
        var splitted = error.split(";"); 
        if(splitted[0] == '404'){
          this.isCourse = false;
          this.testEmitter.next(this.isCourse);
        }
      }
    });
  }

  getMates() {
    this.statisticsService.getMates()
    .subscribe({
      next: (data) => {
        this.mates = data;
      },
      error: (error) => {
        if(error === '404'){
        }
      }
    });
  }

  getEditableData(){
    this.teacherService.getEditableData()
    .subscribe({
      next: (data) => {
        this.editableData = data;
      },
      error: (error) => {
        if(error === '404'){
        }
      }
    });
  }

  onSubmit(form:NgForm){
    this.teacherService.editEditableData(form.value).subscribe({
      next: (data) => {
        this.getEditableData();
      },
      error: (error) => {
        if(error === '404'){
        }
      }
    });
  }

  getCourses(){
    this.teacherService.getCourses()
    .subscribe({
      next: (data) => {
        this.courses = data;
        this.courseChosen = this.courses[0].id;
        this.getSubjectsByCourse();
      },
      error: (error) => {
        if(error === '404'){
        }
      }
    });
  }
  getSubjectsByCourse() {
    this.statisticsService.getMySubjectsByCourse(this.courseChosen)
    .subscribe({
      next: (data) => {
        this.subjects = data;
      },
      error: (error) => {
        if(error === '404'){
        }
      }
    });
  }

  getHoursPerSubject() {
    this.statisticsService.graphHoursPerSubject()
    .subscribe({
      next: (data) => {
        this.dataGraphsHours = data;
        this.dataGraphsHours.forEach((element: StatisticsGraphHours) => {
          this.subjectsGraphHours.push(String(element.subjectName));
          this.hoursSubjectsGraphHours.push(element.subjectTotalHours);
          this.myHoursSubjectsGraphHours.push(element.teacherChosenHours);
        });

        if(this.personalStatistics.correctedHours != 0){
          this.graphHoursPerSubject();
        }
      },
      error: (error) => {
        if(error === '404'){
        }
      }
    });
  }

  graphHoursPerSubject(){
    this.chartHours = new Chart("barChart", {
      type: 'bar',
      data: {
          labels: this.subjectsGraphHours,
          datasets: [{
            barThickness: 15,
            maxBarThickness: 15,
            minBarLength: 2,
            label: 'Mis horas',
            data: this.myHoursSubjectsGraphHours,
            backgroundColor: [
              'rgb(94, 175, 217)',
              'rgb(68, 157, 209)',
              'rgba(57, 119, 187)',
              'rgb(45, 81, 165)',
              'rgb(92, 76, 143)',
              'rgb(139, 70, 121)',
              'rgb(197, 61, 76)',
              'rgb(226, 56, 54)',
              'rgba(255, 70, 51)',
              'rgb(255, 87, 70)' 
            ],
          },
          {
            barThickness: 15,
            maxBarThickness: 15,
            minBarLength: 2,
            label: 'Horas totales',
            data: this.hoursSubjectsGraphHours,
            backgroundColor: [
              'rgb(167, 164, 164)'
            ],
          }
        ]
      },
      options: {
          scales: {
              y: {
                  beginAtZero: true
              },
              x: {
                  ticks: {display: false},
                  grid:{display: false}
              }            
          },

          plugins: {
              legend: {display: false,
              position:'bottom'}
          }
      }
  });
  }

  getPercentageHours() {
    this.statisticsService.graphPercentageHours()
    .subscribe({
      next: (data) => {
        this.dataGraphsPercentage = data;
        this.dataGraphsPercentage.forEach((element: StatisticsGraphPercentage) => {
          var matches = element.subjectName;
          if(matches.indexOf(' ') >= 0){
            matches = String(element.subjectName).replace(/[a-z áéíóúñç]/g, "");
          }
          this.subjectsGraphPercentage.push(String(matches));
          this.percentagesGraphPercentage.push(element.teacherHoursPercentage);
        });
        if(this.personalStatistics.correctedHours != 0){
          this.graphPercentageHours();
        }
      },
      error: (error) => {
        if(error === '404'){
        }
      }
    });
  }

  graphPercentageHours(){
    this.chartPercentage = new Chart("doughnutChart", {
      type: 'doughnut',
      data: {
        labels:this.subjectsGraphPercentage,
        datasets: [{
          label: 'Porcentaje Fuerza Docente',
          data: this.percentagesGraphPercentage,
          backgroundColor: [
            'rgb(94, 175, 217)',
            'rgb(68, 157, 209)',
            'rgba(57, 119, 187)',
            'rgb(45, 81, 165)',
            'rgb(92, 76, 143)',
            'rgb(139, 70, 121)',
            'rgb(197, 61, 76)',
            'rgb(226, 56, 54)',
            'rgba(255, 70, 51)',
            'rgb(255, 87, 70)' 
          ],
          borderWidth: 1
        }]
      },
      options: {
        plugins: {
          legend: {display: true,
            position:'right'},
        }
      }
    });
  }
}
