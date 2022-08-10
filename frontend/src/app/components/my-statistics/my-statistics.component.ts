import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CourseService } from 'src/app/services/course.service';
import { StatisticsService } from 'src/app/services/statistics.service';
import { TeacherService } from 'src/app/services/teacher.service';
import Chart from 'chart.js/auto';
import { NgForm } from '@angular/forms';


@Component({
  selector: 'app-my-statistics',
  templateUrl: './my-statistics.component.html',
  styleUrls: ['./my-statistics.component.css']
})
export class MyStatisticsComponent implements OnInit {

  public personalStatistics: number[]|any;
  public mates: any = [];
  public courseChosen: any;
  public courses:any = [];
  public subjects: any;
  public editableData:any = [];
  public dataGraphsHours: any = [];
  public subjectsGraphHours:  String[] = [];
  public myHoursSubjectsGraphHours: number[] = [];
  public hoursSubjectsGraphHours: number[] = [];

  public dataGraphsPercentage: any = [];
  public subjectsGraphPercentage: String[] = [];
  public percentagesGraphPercentage: number[] = [];

  public chartHours!: Chart;
  public chartPercentage!: Chart;

  constructor(
    private statisticsService: StatisticsService,
    private teacherService: TeacherService
  ) {
    this.editableData = [0, 0];
    this.personalStatistics = [0, 0, 0, 0, 0];
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
        this.personalStatistics = data;
      },
      error: (error) => {
        console.error(error);
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
        console.error(error);
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
        console.error(error);
      }
    });
  }

  onSubmit(form:NgForm){
    this.teacherService.editEditableData(form.value).subscribe({
      next: (data) => {
        this.getEditableData();
      },
      error: (error) => {
        console.error(error);
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
        console.error(error);
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
        console.error(error);
      }
    });
  }

  getHoursPerSubject() {
    this.statisticsService.graphHoursPerSubject()
    .subscribe({
      next: (data) => {
        this.dataGraphsHours = data;
        this.dataGraphsHours.forEach((element: any) => {
          this.subjectsGraphHours.push(String(element[0]));
          this.hoursSubjectsGraphHours.push(element[1]);
          this.myHoursSubjectsGraphHours.push(element[2]);
        });
        if(this.personalStatistics[3] != 0){
          this.graphHoursPerSubject();
        }
      },
      error: (error) => {
        console.error(error);
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
        this.dataGraphsPercentage.forEach((element: any) => {
          var matches = element[0];
          if(matches.indexOf(' ') >= 0){
            matches = String(element[0]).replace(/[a-z áéíóúñç]/g, "");
          }
          this.subjectsGraphPercentage.push(String(matches));
          this.percentagesGraphPercentage.push(element[1]);
        });
        if(this.personalStatistics[3] != 0){
          this.graphPercentageHours();
        }
      },
      error: (error) => {
        console.error(error);
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
