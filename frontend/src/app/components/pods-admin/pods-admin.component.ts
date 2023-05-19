import { Component, OnInit, ViewChild } from '@angular/core';
import { CourseService } from 'src/app/services/course.service';
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { Course } from 'src/app/models/course.model';
import { NgForm } from '@angular/forms';

@Component({
  selector: 'app-pods-admin',
  templateUrl: './pods-admin.component.html',
  styleUrls: ['./pods-admin.component.css']
})
export class PodsAdminComponent implements OnInit {

  @ViewChild('fileSubjects') fileSubjects!: any;
  @ViewChild('fileTeachers') fileTeachers!: any;
  @ViewChild('course') course!: any;

  public pods: Course[];
  public pod: Course;
  public currentSection: string = "Administrador";
  public currentSubsection: string = "Administrador > PODs";
  public showLoader:boolean;
  public showLoaderCreate:boolean;
  public error:string;
  
  constructor(
    private courseService: CourseService,
    private modalService: NgbModal
  ) {
    this.showLoader = false;
    this.showLoaderCreate = false;
  }

  ngOnInit(): void {
    this.getPods();
  }

  getPods(){
    this.showLoader = true;
    this.courseService.getPODs().subscribe({
      next: (data) => { 
        this.showLoader = false;
        this.pods = data;
      }
    });
  }

  onSubmit(form: NgForm) {
    this.showLoaderCreate = true;

    let formData = new FormData();

    let fileUploadSubjects = this.fileSubjects.nativeElement;
    formData.append("fileSubjects", fileUploadSubjects.files[0]);

    let fileUploadTeachers = this.fileTeachers.nativeElement;
    formData.append("fileTeachers", fileUploadTeachers.files[0]);

    let course = new Course(null, this.course.value);

    formData.append("course", new Blob([JSON.stringify(course)], { type: 'application/json' }));

    this.courseService.createPOD(formData).subscribe({
      next: (data) => {
        this.showLoaderCreate = false;
        this.error = '';
        form.reset();
        this.getPods();
      },
      error: (error) => {
        this.showLoaderCreate = false;
        let splitted = error.split("\\"); 
        this.error = splitted[1];
      }
    });
  }

  open(model:any, podToDelete:any) {
    this.modalService.open(model, {});
    this.pod = podToDelete;
  }

  deletePodById(id:any){
    this.courseService.deletePod(id).subscribe({
      next: (_) => {
        this.getPods();
      }
    });
  }
}
