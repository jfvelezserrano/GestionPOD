import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from 'src/app/services/course.service';
import { SubjectService } from 'src/app/services/subject.service';
import { Subject } from 'src/app/models/subject.model';
import { NgForm } from '@angular/forms';
import { Schedule } from 'src/app/models/schedule.model';
import { saveAs } from 'file-saver';
import { TeacherService } from 'src/app/services/teacher.service';
import { StatisticsService } from 'src/app/services/statistics.service';
import { StatisticsGlobal } from 'src/app/models/statistics-global.model';
import { StatisticsTeacher } from 'src/app/models/statistics-teacher.model';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent implements OnInit {

  public globalStatistics: StatisticsGlobal;
  public teachersStatistics: StatisticsTeacher[];
  public page: number;
  public isMore: boolean;
  public isCourse: boolean;
  public showLoaderCourse: boolean;
  public typeSort: string;
  public valuesSorting: any = [
    {value: 'name', name: "Nombre"}
  ];

  constructor(
    private statisticsService: StatisticsService
  ) {
    this.typeSort = "name";
    this.isMore = false;
    this.showLoaderCourse = true;
    this.isCourse = true;
  }

  ngOnInit(): void {
    this.getGlobalStatistics();
    this.getFirstTeachers();
  }

  getGlobalStatistics(){
    this.page = 0;
    
    this.statisticsService.getGlobalStatistics().subscribe({
      next: (data) => {
        this.globalStatistics = data;
        this.showLoaderCourse = false;
      },
      error: (error) => {
        this.showLoaderCourse = false;
        if(error === '404'){
          this.isCourse = false;
        }
      }
    });
  }

  getFirstTeachers(){
    this.page = 0;
    
    this.statisticsService.getAllTeachersStatistics(this.page, this.typeSort).subscribe({
      next: (data) => {
        this.showLoaderCourse = false;
        this.teachersStatistics = data;
      },
      error: (error) => {
        this.showLoaderCourse = false;
        if(error === '404'){
          this.isCourse = false;
        }
      }
    });
  }
  
  loadMoreTeachers() {
    this.page = this.page + 1;
    this.showLoaderCourse = false;

    this.statisticsService.getAllTeachersStatistics(this.page,this.typeSort).subscribe({
      next: (data) => {
        this.teachersStatistics = this.teachersStatistics.concat(data);
        this.isMore = Object.keys(data).length == 12;
      },
      error: (error) => {
        if(error === '404'){
          this.isCourse = false;
        }
      }
    });
  }

  @HostListener("window:scroll", ["$event"])
  onWindowScroll() {
    let pos = (document.documentElement.scrollTop || document.body.scrollTop) + document.documentElement.offsetHeight;
    let max = document.documentElement.scrollHeight;

    if(pos == max && this.isMore){
      this.loadMoreTeachers();
    }
  }

}
