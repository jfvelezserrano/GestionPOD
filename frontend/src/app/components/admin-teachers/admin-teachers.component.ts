import { Component, ElementRef, HostListener, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { ActivatedRoute, Params } from '@angular/router';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Teacher } from 'src/app/models/teacher.model';
import { CourseService } from 'src/app/services/course.service';
import { TeacherService } from 'src/app/services/teacher.service';
import { environment } from 'src/environments/environment';



@Component({
  selector: 'app-admin-teachers',
  templateUrl: './admin-teachers.component.html',
  styleUrls: ['./admin-teachers.component.css']
})
export class AdminTeachersComponent implements OnInit {
  @ViewChild('teacherToDelete') teacherToDelete!: ElementRef;
  
  public teachers: Teacher[];
  public id: number;
  public currentSection: string = "Administrador";
  public currentSubsection: string = "Administrador > Cursos > Profesores";
  public idTeacher: number;
  public teacher:Teacher;
  public page: number;
  public typeSort: string;
  public isMore: boolean;
  public error: string;
  public mainAdmin: string = environment.main_admin;
  public showLoader:boolean = false;

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
      this.showLoader = true;
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
        this.showLoader = false;
        let splitted = error.split("\\"); 
        if(splitted[0] == '404'){
        }
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
        
      }
    });
  }

  openToDelete(model:any, teacherToDeleteInPod:any) {
  this.modalService.open(model, {});
  this.teacher = teacherToDeleteInPod;
  }

  openCreateTeacher(model:any) {
    this.modalService.open(model, {});
  }

  onSubmit(form:NgForm) {
    this.teacherService.createTeacher(form.value, this.id).subscribe({
      next: (data) => {
        this.getTeachersInPod();
        this.error = '';
        form.reset();
      },
      error: (error) => {
        let splitted = error.split("\\"); 
        if(splitted[0] == '409'){
          this.error = splitted[1];
        }
      }
    });
  }
}
