import { Component, OnInit } from '@angular/core';
import { Teacher } from 'src/app/models/teacher';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TeacherService } from 'src/app/services/teacher.service';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-management-admin',
  templateUrl: './management-admin.component.html',
  styleUrls: ['./management-admin.component.css']
})
export class ManagementAdminComponent implements OnInit {

  public teachers:any = [];
  public admins:any = [];
  public adminTeacher: Teacher|any;
  public emailNewAdmin:string|any;

  constructor(
    private teacherService: TeacherService,
    private modalService: NgbModal
    ) { 
      this.emailNewAdmin = "";
      this.adminTeacher = new Teacher(null, "", "", [], null, null);
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
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  getAdmins() {
    this.teacherService.findByRole("ADMIN")
    .subscribe({
      next: (data) => {
        this.admins = data;
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  onSubmit(form:NgForm){
    this.setAdmin();
    this.teacherService.editTeacher(this.adminTeacher)
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
    this.teacherService.editTeacher(this.adminTeacher)
    .subscribe({
      next: (data) => {
        this.getAdmins();
      },
      error: (error) => {
        console.error(error);
      }
    });
  }

  open(model:any, admin:Teacher) {
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
