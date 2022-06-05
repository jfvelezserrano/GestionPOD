import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Params } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Teacher } from 'src/app/models/teacher';
import { CourseService } from 'src/app/services/course.service';
import { TeacherService } from 'src/app/services/teacher.service';


@Component({
  selector: 'app-admin-teachers',
  templateUrl: './admin-teachers.component.html',
  styleUrls: ['./admin-teachers.component.css']
})
export class AdminTeachersComponent implements OnInit {

  @ViewChild('teacherToDelete') teacherToDelete!: ElementRef;
  
  public teachers:any|undefined;
  public id:any;
  public idTeacher:any;
  public teacher:Teacher|any;
  public page:any;
  public typeSort:any;
  public isMore:any;
  public showLoader:boolean|any = false;
  public valuesSorting:any = [
    {value: 'name', name: "Nombre"},
    {value: 'email', name: "Email"}
  ];

  constructor(
    private teacherService: TeacherService,
    private courseService: CourseService,
    private route: ActivatedRoute,
    private modalService: NgbModal)
    { 
      this.typeSort = "name";
    }

  ngOnInit(): void {
    this.route.params.subscribe((params:Params) => {
      this.id = params['id'];
    });

    this.getTeachersInPod();
  }

  getTeachersInPod(){
    this.showLoader = true;
    this.page = 0;
    this.isMore = true;

    this.teacherService.getTeachersByPOD(this.id, this.page, this.typeSort).subscribe({
      next: (data) => {
        this.showLoader = false;
        this.teachers = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  loadMoreTeachers() {
    this.page = this.page + 1;

    this.teacherService.getTeachersByPOD(this.id, this.page,this.typeSort)
    .subscribe({
      next: (data) => {
        this.teachers = this.teachers.concat(data);
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

    if(pos == max  && this.isMore){
      this.loadMoreTeachers();
    }
  }

  deleteTeacherInPod(idTeacherToDelete:any){
    this.courseService.deleteTeacherInPod(this.id, idTeacherToDelete).subscribe({
      next: (_) => {
        this.getTeachersInPod();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

 open(model:any, teacherToDeleteInPod:any) {
  this.modalService.open(model, {});
  this.teacher = teacherToDeleteInPod;
  }

  openCreateTeacher(model:any) {
    this.modalService.open(model, {});
  }

  createTeacherInPod(form:NgForm) {
    this.teacherService.createTeacher(form.value, this.id).subscribe({
      next: (_) => {
        this.getTeachersInPod();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }
}
