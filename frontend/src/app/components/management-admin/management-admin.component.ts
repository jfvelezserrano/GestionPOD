import { Component, OnInit } from '@angular/core';
import { TeacherRoles } from 'src/app/models/teacher-roles.model';
import { Teacher } from 'src/app/models/teacher.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TeacherService } from 'src/app/services/teacher.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-management-admin',
  templateUrl: './management-admin.component.html',
  styleUrls: ['./management-admin.component.css']
})
export class ManagementAdminComponent implements OnInit {

  public teachers:TeacherRoles[] = [];
  public admins: TeacherRoles[] = [];
  public adminTeacher: TeacherRoles;
  public emailNewAdmin: string;
  public showLoaderCourse: boolean;
  public isCourse: boolean;

  constructor(
    private teacherService: TeacherService,
    private modalService: NgbModal
    ) { 
      this.emailNewAdmin = "";
      this.adminTeacher = new TeacherRoles(-1, "", "", []);
      this.showLoaderCourse = true;
      this.isCourse = true;
    }

  ngOnInit(): void {
    this.getAllTeachersCurrentCourse();
    this.getAdmins();
  }

  getAllTeachersCurrentCourse() {
    this.teacherService.getAllTeachersCurrentCourse()
    .subscribe({
      next: (data) => {
        this.teachers = data;
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

  getAdmins() {
    this.teacherService.findByRole("ADMIN")
    .subscribe({
      next: (data) => {
        this.admins = data;
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

  onSubmit(form:NgForm){
    this.setAdmin();
    this.teacherService.changeRole(this.adminTeacher)
    .subscribe({
      next: (data) => {
        this.getAdmins();
        form.reset();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  removeRoleAdmin(){
    this.adminTeacher.roles.forEach((value:any,index: any)=>{
      if(value=="ADMIN") this.adminTeacher.roles.splice(index,1);
    });
    this.teacherService.changeRole(this.adminTeacher)
    .subscribe({
      next: (data) => {
        this.getAdmins();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  openToDelete(model:any, admin:TeacherRoles) {
    this.modalService.open(model, {});
    this.adminTeacher = admin;
  }

  setAdmin(){
    let teacherDB =  this.teachers.filter((x: { email: any; }) => x.email == this.emailNewAdmin)[0];
    this.adminTeacher.name = teacherDB.name;
    this.adminTeacher.email = this.emailNewAdmin;
    this.adminTeacher.roles = teacherDB.roles;
    let isPresent = teacherDB.roles.some(function(x: string){ return x === "ADMIN"});
    if(!isPresent){
      this.adminTeacher.roles.push("ADMIN");
    }
  }

}
