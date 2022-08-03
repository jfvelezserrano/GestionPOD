import { Component, HostListener, OnInit } from '@angular/core';
import { ActivatedRoute, Params } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { NgbOffcanvas } from '@ng-bootstrap/ng-bootstrap';
import { CourseService } from 'src/app/services/course.service';
import { SubjectService } from 'src/app/services/subject.service';
import { SubjectModel } from 'src/app/models/subject';
import { NgForm } from '@angular/forms';
import { Schedule } from 'src/app/models/schedule';
import { saveAs } from 'file-saver';
import { TeacherService } from 'src/app/services/teacher.service';
import { StatisticsService } from 'src/app/services/statistics.service';

@Component({
  selector: 'app-statistics',
  templateUrl: './statistics.component.html',
  styleUrls: ['./statistics.component.css']
})
export class StatisticsComponent implements OnInit {

  public globalStatistics: number[]|any;
  public teachersStatistics:any;
  public page:any;
  public isMore:any;
  public typeSort:any;
  public valuesSorting:any = [
    {value: 'name', name: "Nombre"}
  ];

  constructor(
    private statisticsService: StatisticsService
  ) {
    this.typeSort = "name";
    this.globalStatistics = [0, 0, 0, 0, 0, 0, 0, 0, 0];
  }

  ngOnInit(): void {
    this.getGlobalStatistics();
    this.getFirstTeachersStatistics();
  }

  getGlobalStatistics(){
    this.page = 0;
    
    this.statisticsService.getGlobalStatistics().subscribe({
      next: (data) => {
        this.globalStatistics = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getFirstTeachersStatistics(){
    this.page = 0;
    
    this.statisticsService.getAllTeachersStatistics(this.page,this.typeSort).subscribe({
      next: (data) => {
        this.teachersStatistics = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }
  
  loadMoreSubjects() {
    this.page = this.page + 1;

    this.statisticsService.getAllTeachersStatistics(this.page,this.typeSort).subscribe({
      next: (data) => {
        this.teachersStatistics = this.teachersStatistics.concat(data);
        this.isMore = Object.keys(data).length == 12;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  @HostListener("window:scroll", ["$event"])
  onWindowScroll() {
    let pos = (document.documentElement.scrollTop || document.body.scrollTop) + document.documentElement.offsetHeight;
    let max = document.documentElement.scrollHeight;

    if(pos == max && this.isMore){
      this.loadMoreSubjects();
    }
  }

}
