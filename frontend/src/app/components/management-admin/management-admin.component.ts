import { Component, OnInit } from '@angular/core';
import { TeacherRoles } from 'src/app/models/teacher-roles.model';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { TeacherService } from 'src/app/services/teacher.service';
import { NgForm } from '@angular/forms';
import { BehaviorSubject } from 'rxjs';
import { environment } from 'src/environments/environment';

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
  public error: string;
  public showLoader:boolean;
  public currentSection: string = "Administrador";
  public currentSubsection: string = "Administrador > Gestión Altas";
  public mainAdmin: string = environment.main_admin;
  public testEmitter: BehaviorSubject<boolean>;

  constructor(
    private teacherService: TeacherService,
    private modalService: NgbModal
    ) { 
      this.showLoader = false;
      this.emailNewAdmin = "";
      this.adminTeacher = new TeacherRoles(-1, "", "", []);
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
      }
    });
  }

  getAdmins() {
    this.showLoader = true;
    this.teacherService.findByRole("ADMIN")
    .subscribe({
      next: (data) => {
        this.showLoader = false;
        this.admins = data;
      }
    });
  }

  onSubmit(form:NgForm){
    this.error = '';
    let teacher = this.existsTeacher();
    if(teacher != null){
      this.setAdmin(teacher);
      this.teacherService.changeRole(this.adminTeacher)
      .subscribe({
        next: (data) => {
          this.getAdmins();
          form.reset();
        },
        error: (error) => {
          let splitted = error.split("\\"); 
          this.error = splitted[1];
        }
      });
    } else{
      this.error = "La dirección de correo no existe";
    }
  }

  removeRoleAdmin(){
    this.adminTeacher.roles.forEach((value:any,index: any)=>{
      if(value=="ADMIN") this.adminTeacher.roles.splice(index,1);
    });
    this.teacherService.changeRole(this.adminTeacher)
    .subscribe({
      next: (data) => {
        this.getAdmins();
      }
    });
  }

  openToDelete(model:any, admin:TeacherRoles) {
    this.modalService.open(model, {});
    this.adminTeacher = admin;
  }

  existsTeacher(){
    return this.teachers.filter((x: { email: any; }) => x.email == this.emailNewAdmin)[0];
  }

  setAdmin(teacher: TeacherRoles){
    this.adminTeacher.name = teacher.name;
    this.adminTeacher.email = this.emailNewAdmin;
    this.adminTeacher.roles = teacher.roles;
    let isPresent = teacher.roles.some(function(x: string){ return x === "ADMIN"});
    if(!isPresent){
      this.adminTeacher.roles.push("ADMIN");
    }
  }

}
