import { Component, HostListener, OnInit, Input } from '@angular/core';
import { StatisticsService } from 'src/app/services/statistics.service';
import { StatisticsGlobal } from 'src/app/models/statistics-global.model';
import { StatisticsTeacher } from 'src/app/models/statistics-teacher.model';
import { BehaviorSubject } from 'rxjs';

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
  public typeSort: string;
  public showLoaderCourse: boolean;
  public isCourse: boolean;
  public testEmitter: BehaviorSubject<boolean>;
  public valuesSorting: any = [
    {value: 'name', name: "Nombre"}
  ];

  constructor(
    private statisticsService: StatisticsService
  ) {
    this.showLoaderCourse = true;
    this.typeSort = "name";
    this.isMore = false;
    this.testEmitter = new BehaviorSubject<boolean>(this.isCourse);
  }

  ngOnInit(): void {
    this.getGlobalStatistics();
    this.testEmitter.subscribe(data => {
      if(data != undefined && data){
        this.getFirstTeachers();
      };
    })
  }

  getGlobalStatistics(){
    this.page = 0;
    this.showLoaderCourse = true;
    this.statisticsService.getGlobalStatistics().subscribe({
      next: (data) => {
        this.showLoaderCourse = false;
        this.isCourse = true;
        this.testEmitter.next(this.isCourse);
        this.globalStatistics = data;
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

  getFirstTeachers(){
    this.page = 0;
    
    this.statisticsService.getAllTeachersStatistics(this.page, this.typeSort).subscribe({
      next: (data) => {
        this.teachersStatistics = data;
      },
      error: (error) => {
        if(error === '404'){
        }
      }
    });
  }
  
  loadMoreTeachers() {
    this.page = this.page + 1;

    this.statisticsService.getAllTeachersStatistics(this.page,this.typeSort).subscribe({
      next: (data) => {
        this.teachersStatistics = this.teachersStatistics.concat(data);
        this.isMore = Object.keys(data).length == 12;
      },
      error: (error) => {
        if(error === '404'){
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
