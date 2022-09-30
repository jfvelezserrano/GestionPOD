import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CourseService } from 'src/app/services/course.service';
import { StatisticsService } from 'src/app/services/statistics.service';
import { TeacherService } from 'src/app/services/teacher.service';
import Chart from 'chart.js/auto';
import { NgForm } from '@angular/forms';
import { Subject } from 'src/app/models/subject.model';
import { StatisticsPersonal } from 'src/app/models/statistics-personal.model';
import { StatisticsMates } from 'src/app/models/statistics-mates.model';
import { Course } from 'src/app/models/course.model';
import { CourseTeacher } from 'src/app/models/course-teacher.model';
import { SubjectNameAndQuarter } from 'src/app/models/subject-name-and-quarter.model';
import { StatisticsGraphHours } from 'src/app/models/statistics-graph-hours.model';
import { StatisticsGraphPercentage } from 'src/app/models/statistics-graph-percentage.model';


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

  constructor(
    private statisticsService: StatisticsService,
    private teacherService: TeacherService
  ) {
    this.editableData = new CourseTeacher(0, "");
    this.showLoaderCourse = true;
    this.isCourse = true;
  }

  ngOnInit(): void {
    this.getPersonalStatistics();
    this.getMates();
    this.getCourses();
    this.getEditableData();
  }

  ngAfterViewInit():void{
    this.getHoursPerSubject();
    this.getPercentageHours();
  }

  getPersonalStatistics() {
    this.statisticsService.getPersonalStatistics()
    .subscribe({
      next: (data) => {
        this.showLoaderCourse = false;
        this.personalStatistics = data;
      },
      error: (error) => {
        this.showLoaderCourse = false;
        if(error === '404'){
          this.isCourse = false;
        }
      }
    });
  }

  getMates() {
    this.statisticsService.getMates()
    .subscribe({
      next: (data) => {
        this.showLoaderCourse = false;
        this.mates = data;
      },
      error: (error) => {
        this.showLoaderCourse = false;
        if(error === '404'){
          this.isCourse = false;
        }
      }
    });
  }

  getEditableData(){
    this.teacherService.getEditableData()
    .subscribe({
      next: (data) => {
        this.showLoaderCourse = false;
        this.editableData = data;
      },
      error: (error) => {
        this.showLoaderCourse = false;
        if(error === '404'){
          this.isCourse = false;
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
        this.showLoaderCourse = false;
        if(error === '404'){
          this.isCourse = false;
        }
      }
    });
  }

  getCourses(){
    this.teacherService.getCourses()
    .subscribe({
      next: (data) => {
        this.showLoaderCourse = false;
        this.courses = data;
        this.courseChosen = this.courses[0].id;
        this.getSubjectsByCourse();
      },
      error: (error) => {
        this.showLoaderCourse = false;
        if(error === '404'){
          this.isCourse = false;
        }
      }
    });
  }
  getSubjectsByCourse() {
    this.statisticsService.getMySubjectsByCourse(this.courseChosen)
    .subscribe({
      next: (data) => {
        this.showLoaderCourse = false;
        this.subjects = data;
      },
      error: (error) => {
        this.showLoaderCourse = false;
        if(error === '404'){
          this.isCourse = false;
        }
      }
    });
  }

  getHoursPerSubject() {
    this.statisticsService.graphHoursPerSubject()
    .subscribe({
      next: (data) => {
        this.showLoaderCourse = false;
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
        this.showLoaderCourse = false;
        if(error === '404'){
          this.isCourse = false;
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
        this.showLoaderCourse = false;
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
        this.showLoaderCourse = false;
        if(error === '404'){
          this.isCourse = false;
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
